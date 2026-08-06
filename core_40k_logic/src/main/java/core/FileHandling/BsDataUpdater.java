package core.FileHandling;

import static core.Parsing.ParseUtils.ReadFileAsString;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import core.DatabaseManager;

public class BsDataUpdater {

    private static final String REPO_OWNER = "BSData";
    private static final String REPO_NAME = "wh40k-11e";
    private static final String BRANCH = "main";
    private static final String API_BASE = "https://api.github.com";

    private static final Gson GSON = new Gson();

    private final FileHandler fileHandler;
    private final File bsDataDirectory;

    public BsDataUpdater(FileHandler fileHandler, File bsDataDirectory) {
        this.fileHandler = fileHandler;
        this.bsDataDirectory = bsDataDirectory;
    }

    public void checkAndUpdate() {
        synchronized (DatabaseManager.onlineDatabaseLock)
        {
            new Thread(() -> {
                try {
                        String latestSha = fetchLatestCommitSha();
                        String storedSha = ReadFileAsString(bsDataDirectory.toString(), "last_commit_sha.txt");
                        if (latestSha.equals(storedSha)) {
                            return;
                        }

                        if (storedSha.isEmpty()) {
                            downloadAllCatalogueFiles();
                        } else {
                            downloadChangedFiles(storedSha, latestSha);
                        }

                        fileHandler.SaveTextFile(bsDataDirectory, "last_commit_sha.txt", latestSha);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private String fetchLatestCommitSha() throws Exception {
        String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME + "/commits/" + BRANCH;
        JsonObject response = fetchJson(url);
        return response.get("sha").getAsString();
    }

    private void downloadChangedFiles(String fromSha, String toSha) throws Exception {
        String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME
                + "/compare/" + fromSha + "..." + toSha;
        JsonObject response = fetchJson(url);
        JsonArray files = response.getAsJsonArray("files");

        for (int i = 0; i < files.size(); i++) {
            JsonObject file = files.get(i).getAsJsonObject();
            String filename = file.get("filename").getAsString();
            String status = file.get("status").getAsString();

            if (!filename.endsWith(".json") && !filename.endsWith(".gst")) continue;

            if (status.equals("removed")) {
                deleteLocalFile(filename);
            } else {
                String rawUrl = file.get("raw_url").getAsString();
                downloadFile(rawUrl, filename);
            }
        }
    }
    private static String encodePath(String path) throws Exception {
        String[] segments = path.split("/");
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) encoded.append("/");
            encoded.append(java.net.URLEncoder.encode(segments[i], java.nio.charset.StandardCharsets.UTF_8)
                    .replace("+", "%20")); // URLEncoder encodes spaces as '+', which is form-encoding, not URL path encoding
        }
        return encoded.toString();
    }
    private void downloadAllCatalogueFiles() throws Exception {
        String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME
                + "/git/trees/" + BRANCH + "?recursive=1";
        JsonObject response = fetchJson(url);
        JsonArray tree = response.getAsJsonArray("tree");

        ArrayList<JsonObject> catFiles = new ArrayList<>();
        for (int i = 0; i < tree.size(); i++) {
            JsonObject item = tree.get(i).getAsJsonObject();
            String path = item.get("path").getAsString();
            if (path.endsWith(".json") || path.endsWith(".gst")) {
                catFiles.add(item);
            }
        }

        for (JsonObject item : catFiles) {
            String path = item.get("path").getAsString();
            String rawUrl = "https://raw.githubusercontent.com/" + REPO_OWNER
                    + "/" + REPO_NAME + "/" + BRANCH + "/" + encodePath(path);
            downloadFile(rawUrl, path); // pass the *unencoded* path here — this is your local filesystem path
        }
    }

    private void downloadFile(String url, String relativePath) throws Exception {
        File outputFile = new File(bsDataDirectory.toString(), relativePath);
        outputFile.getParentFile().mkdirs();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept", "application/vnd.github.v3.raw");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);
        // conn.setRequestProperty("Authorization", "token YOUR_TOKEN");

        try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            conn.disconnect();
        }
    }

    private void deleteLocalFile(String relativePath) {
        new File(bsDataDirectory.toString(), relativePath).delete();
    }

    private JsonObject fetchJson(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
        conn.setRequestProperty("User-Agent", "DamageCalculator40k");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);

        try (InputStream in = conn.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return GSON.fromJson(new String(bytes), JsonObject.class);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
        finally {
            conn.disconnect();
        }
    }
}
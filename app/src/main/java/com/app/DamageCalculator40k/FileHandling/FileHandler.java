package com.app.DamageCalculator40k.FileHandling;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityElementAdapter;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.Enums.Faction;
import com.app.DamageCalculator40k.Enums.Keyword;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.Parsing.XmlParser;
import com.app.DamageCalculator40k.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.app.DamageCalculator40k.Parsing.Parsing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public  class FileHandler  {

    private final String WAHAPEDIA_BASE_URL = "https://wahapedia.ru/wh40k10ed/";
    private final String LAST_ARGUMENT_LENGTH_NAME = "Last_argument_length.txt";


    private final File matchupDirectory;
    private final File armyDirectory;
    private final File wahapediaDataDirectory;
    private final File bsDataDirectory;

    private final Gson gson;
    private final ContentResolver contentResolver;
    private final AssetManager assetManager;


    public static volatile FileHandler instance;

    private BattleScribeUpdater battleScribeUpdater;

    public void UpdateBattlesScribeData(UpdateCallbackBsData updateCallbackBsData)
    {

        battleScribeUpdater.checkAndUpdate(updateCallbackBsData);
    }

    private Faction getFactionFromName(String fileName)
    {
        String fileEnumName = Parsing.toEnumName(fileName);

        for( Faction faction : Faction.values())
        {
            String factionName = faction.name();
            if(fileEnumName.contains(factionName))
            {
                return faction;
            }
        }
        return  Faction.Unidentified;
    }

    public ArrayList< Pair<String,Faction>> GetXMLData()
    {
        ArrayList< Pair<String,Faction>> retValues = new ArrayList<>();
        File[] files = bsDataDirectory.listFiles((dir, name) ->
                name.endsWith(".cat") || name.endsWith(".gst"));

        for (File file : files) {

            Faction faction = getFactionFromName(file.getName());

            retValues.add( new Pair<>(ReadFileAsString(bsDataDirectory.toString(),file.getName()),faction));

            if(faction == Faction.Unidentified)
            {
                Log.d("Database source problem","Faction for file name could not be found " + file.getName());
            }
        }
        return  retValues;
    }


    public void saveMatchup(Matchup matchup)
    {
        try {
            String jsonString = gson.toJson(matchup);
            File matchupSave = new File(matchupDirectory, matchup.name);
            FileWriter writer = new FileWriter(matchupSave);
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("json skrivning", "det sket sig");
        }
    }

    public static String ReadFileAsString(String directory, String name )
    {
        String stringToReturn = "";
        try{
            stringToReturn = new Scanner(new File(directory + "/" + name)).useDelimiter("\\A").next();
        }
        catch (Exception e)
        {
            Log.d("file handler","sket sig att lasa filen");
        }

        return stringToReturn;
    }

    private static void SaveTextFile(File directory, String name, String content )
    {
        try {
            FileWriter writer = new FileWriter(new File(directory, name));
            writer.write( content);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            Log.d("FileHandler","sket sig nar det skulle sparas localt senaste updaterat");
        }
    }

    public void SaveBsData(Context context, Uri fileUri)
    {
        try {
            // Is probably a better way to get the name that support  <29 versions
            DocumentFile documentFile = DocumentFile.fromSingleUri(context,fileUri);

            InputStream inputStream = contentResolver.openInputStream(fileUri);
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            File armySave = new File(armyDirectory, documentFile.getName());

            FileWriter writer = new FileWriter(armySave);
            writer.write(result);
            writer.flush();
            writer.close();


            inputStream.close();
            s.close();
        }
        catch (Exception e)
        {
            Log.d("I xml sparandet",e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String GetOnlineWahapediaResource( String onlineResource)
    {
        StringBuilder returnValue = new StringBuilder();
        try
        {
                URL API_URL = new URL(WAHAPEDIA_BASE_URL +onlineResource);
                HttpURLConnection con = (HttpURLConnection)API_URL.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);

                DataInputStream HTTPInput = new DataInputStream( con.getInputStream() );

                int ReadChunkSize = 4096;
                byte[] Buffer = new byte[ReadChunkSize];
                while(true)
                {
                    int ReadBytes = HTTPInput.read(Buffer);
                    if(ReadBytes <= 0)
                    {
                        break;
                    }
                    returnValue.append(new String(Buffer,0,ReadBytes, StandardCharsets.UTF_8));
                }
                con.disconnect();
                HTTPInput.close();
        }
        catch (Exception e)
        {
            Log.d ("Wahapedia update", e.getMessage());
        }

        return returnValue.toString();
    }
    public Pair<String,Context> UpdateWahapediaData(UpdateArgumentStruct Arguments)
    {
        //  Log.d("Wahapedia grejen: ", "den uppdaterade bra");
        String ReturnValue = "Success!";

        String localLastUpdate = ReadFileAsString(wahapediaDataDirectory.toString(),Arguments.LastUpdateURL);
        String onlineLastUpdate =  GetOnlineWahapediaResource(Arguments.LastUpdateURL);
        String previousArgumentLengthString = ReadFileAsString(wahapediaDataDirectory.toString(),LAST_ARGUMENT_LENGTH_NAME);

        if(localLastUpdate.equals(onlineLastUpdate) && Integer.parseInt(previousArgumentLengthString) == Arguments.FilesToDownload.size() )
        {
            return new Pair<>("Data is up to date", Arguments.context);
        }
        else
        {
            try {
                FileWriter writer = new FileWriter(new File(wahapediaDataDirectory, Arguments.LastUpdateURL));
                writer.write( onlineLastUpdate);
                writer.flush();
                writer.close();
            }
            catch (Exception e)
            {
                Log.d("FileHandler","sket sig nar det skulle sparas localt senaste updaterat");
            }
        }

        try
        {
            for(String File : Arguments.FilesToDownload)
            {
                String content = GetOnlineWahapediaResource(File);

                FileWriter writer = new FileWriter(new File(wahapediaDataDirectory,File));
                writer.write(content);

                writer.flush();
                writer.close();
            }

            File lastArgumentLength = new File(wahapediaDataDirectory, LAST_ARGUMENT_LENGTH_NAME);
            FileWriter writer = new FileWriter(lastArgumentLength);
            writer.write( String.valueOf(Arguments.FilesToDownload.size()));
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            Log.d ("Hej hej", e.getMessage());
            ReturnValue = "Error updating files: "+e.getMessage();
        }
        return new Pair<String,Context>(ReturnValue,Arguments.context);
    }

    public Matchup getMatchup(String matchupName)
    {
        File[] directoryListing = matchupDirectory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing)
            {
                // Do something with child

                if(child.getName().equals(matchupName))
                {
                    try {
                        Scanner s = new Scanner(child).useDelimiter("\\A");
                        String result = s.hasNext() ? s.next() : "";
                        return gson.fromJson(new FileReader(child.getAbsolutePath()), Matchup.class);
                    }
                    catch (Exception e)
                    {
                        Log.d("fil knas", e.getMessage());
                    }
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
            Log.d("saved matchups", "Hittade inte den givna matchupen" );
        }
        return null;
    }

    public Army getArmy(String armyName)
    {
        File[] directoryListing = armyDirectory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing)
            {
                // Do something with child
                if(child.getName().equals(armyName))
                {
                    try {
                        String rawJson = gson.toString();
                        Army army = gson.fromJson(new FileReader(child.getAbsolutePath()), Army.class);

                        return army;
                    }
                    catch (Exception e)
                    {
                        Log.d("fil knas", e.getMessage());
                    }
                }
            }
        } else {
            Log.d("saved matchups", "Hittade inte den givna matchupen" );
        }
        return null;
    }

    public static void InitializeFileHandler(Context context)
    {
        if(instance != null)
        {
            Log.d("FileHandler knas", "Filehandler is already initialized ");
            return;
        }
        context.getContentResolver();
        instance = new FileHandler(context);

    }

    public static FileHandler GetInstance( )
    {
        if(instance == null)
        {
            Log.d("File handler knas", "Trying to access the filehandler before it is initialized");
            return null;
        }
        return instance;
    }

    private FileHandler(Context context)
    {
        synchronized (FileHandler.class) {

            matchupDirectory = new File((context.getFilesDir()), "SavedMatchups");
            if (!matchupDirectory.exists()) {
                matchupDirectory.mkdir();
            }
            armyDirectory = new File((context.getFilesDir()), "SavedArmies");
            if (!armyDirectory.exists()) {
                armyDirectory.mkdir();
            }

            wahapediaDataDirectory = new File(context.getFilesDir(), "WahapediaData");
            if (!wahapediaDataDirectory.exists()) {
                wahapediaDataDirectory.mkdir();
            }
            bsDataDirectory = new File(context.getFilesDir(), "BsDataDirectory");
            if (!bsDataDirectory.exists()) {
                bsDataDirectory.mkdir();
            }

            assetManager = context.getAssets();
            contentResolver = context.getContentResolver();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Ability.class, new AbilityElementAdapter());
            gson = gsonBuilder.create();
            battleScribeUpdater = new BattleScribeUpdater(context);
        }
    }

    public void CreateArmyFromFile(Context context, Uri uri)
    {
        Army TestArmy = new Army();
        try {
            // Is probably a better way to get the name that support  <29 versions
            DocumentFile documentFile = DocumentFile.fromSingleUri(context,uri);

            InputStream inputStream = contentResolver.openInputStream(uri);
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            Parsing parser = new Parsing();

            TestArmy = parser.ParseGWListFormat(result);
            TestArmy.name = documentFile.getName();

            inputStream.close();
            s.close();
        }
        catch (Exception e)
        {
            Log.d("GW parsing",e.getMessage());
            e.printStackTrace();
        }

        try {

            String jsonString = gson.toJson(TestArmy);

            File armySave = new File(armyDirectory, TestArmy.name);

            FileWriter writer = new FileWriter(armySave);
            writer.write(jsonString);
            writer.flush();
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
            Log.d("json skrivning", "det sket sig armerna");
        }
    }

    public ArrayList<ArrayList<String>> GetWahapediaDataCSV(String fileName )
    {
        ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
        try
        {
            File fileToFind = new File(wahapediaDataDirectory + "/" + fileName);

            BufferedReader reader = new BufferedReader(new FileReader(fileToFind));
            String readString;
            String[] tempStringArray;

            while((readString = reader.readLine()) != null)
            {
                // This is required to make sure that inconsistent capitalization does not break the parsing
                readString = readString.toLowerCase();
                tempStringArray = readString.split("\\|");

                for(int i = 0; i < tempStringArray.length; i++)
                {
                    tempStringArray[i] = tempStringArray[i].trim();
                }

                ArrayList<String> tempArrayList = new ArrayList<>();
                Collections.addAll(tempArrayList, tempStringArray);
                arrayListToReturn.add(tempArrayList);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return arrayListToReturn;
    }

    public void SaveArmy(Army army)
    {
        try {

            String jsonString = gson.toJson(army);
            File armySave = new File(armyDirectory, army.name);
            FileWriter writer = new FileWriter(armySave);
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("json skrivning", "det sket sig");
        }
    }

    public void DeleteMatchup(String matchupName)
    {
        File fileToDelete = null;
        try {
            for(File file : matchupDirectory.listFiles())
            {
                if(file.getName().equals(matchupName))
                {
                    fileToDelete = file;

                    break;
                }
            }
        }
        catch (Exception e)
        {
            Log.d("FileHandler", "DeleteMatchup: knasade vid deleten )");


        }
        if(fileToDelete != null)
        {
            fileToDelete.delete();
        }

    }

    public ArrayList<String> GetSavedArmies()
    {
        ArrayList<String> armiesToReturn = new ArrayList<>();

        for (File file : armyDirectory.listFiles())
        {
            armiesToReturn.add(file.getName());
        }

        return  armiesToReturn;
    }


    public class BattleScribeUpdater {

        private static final String REPO_OWNER = "BSData";
        private static final String REPO_NAME = "wh40k-10e";
        private static final String BRANCH = "main";
        private static final String API_BASE = "https://api.github.com";

        private final Context context;

        public BattleScribeUpdater(Context context) {
            this.context = context;
        }

        // Call this on app start or when user requests update
        public void checkAndUpdate(UpdateCallbackBsData callback) {
            new Thread(() -> {
                try {
                    String latestSha = fetchLatestCommitSha();
                    String storedSha = ReadFileAsString(bsDataDirectory.toString(),context.getString(R.string.last_commit_sha));
                    if (latestSha.equals(storedSha)) {
                        callback.onComplete(false); // already up to date
                        return;
                    }

                    if (storedSha.isEmpty()) {
                        // First run, download all .cat files
                        downloadAllCatalogueFiles(callback);
                    } else {
                        // Only download changed files
                        downloadChangedFiles(storedSha, latestSha, callback);
                    }

                    SaveTextFile(bsDataDirectory,context.getString(R.string.last_commit_sha),latestSha);
                    callback.onComplete(true);

                } catch (Exception e) {
                    callback.onError(e);
                    e.printStackTrace();
                }
            }).start();
        }

        private String fetchLatestCommitSha() throws Exception {
            String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME
                    + "/commits/" + BRANCH;
            JSONObject response = fetchJson(url);
            return response.getString("sha");
        }

        private void downloadChangedFiles(String fromSha, String toSha,
                                          UpdateCallbackBsData callback) throws Exception {
            String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME
                    + "/compare/" + fromSha + "..." + toSha;
            JSONObject response = fetchJson(url);
            JSONArray files = response.getJSONArray("files");

            for (int i = 0; i < files.length(); i++) {
                JSONObject file = files.getJSONObject(i);
                String filename = file.getString("filename");
                String status = file.getString("status"); // added, modified, removed

                if (!filename.endsWith(".cat") && !filename.endsWith(".gst")) continue;

                if (status.equals("removed")) {
                    deleteLocalFile(filename);
                } else {
                    // raw_url gives direct download without API rate limits
                    String rawUrl = file.getString("raw_url");
                    downloadFile(rawUrl, filename);
                }

                callback.onProgress(i + 1, files.length(), filename);
            }
        }

        private void downloadAllCatalogueFiles(UpdateCallbackBsData callback) throws Exception {
            // Use the git tree API to list all files without downloading them
            String url = API_BASE + "/repos/" + REPO_OWNER + "/" + REPO_NAME
                    + "/git/trees/" + BRANCH + "?recursive=1";
            JSONObject response = fetchJson(url);
            JSONArray tree = response.getJSONArray("tree");

            // Filter to only .cat and .gst files
            ArrayList<JSONObject> catFiles = new ArrayList<>();
            for (int i = 0; i < tree.length(); i++) {
                JSONObject item = tree.getJSONObject(i);
                String path = item.getString("path");
                if (path.endsWith(".cat") || path.endsWith(".gst")) {
                    catFiles.add(item);
                }
            }

            for (int i = 0; i < catFiles.size(); i++) {
                JSONObject item = catFiles.get(i);
                String path = item.getString("path");
                String rawUrl = "https://raw.githubusercontent.com/" + REPO_OWNER
                        + "/" + REPO_NAME + "/" + BRANCH + "/" + path;
                downloadFile(rawUrl, path);
                callback.onProgress(i + 1, catFiles.size(), path);
            }
        }

        private void downloadFile(String url, String relativePath) throws Exception {
            File outputFile = new File(bsDataDirectory, relativePath);
            outputFile.getParentFile().mkdirs();

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3.raw");
            // Add auth token if you have one, unauthenticated is limited to 60 req/hour
            // conn.setRequestProperty("Authorization", "token YOUR_TOKEN");

            try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                conn.disconnect();
            }
        }

        private void deleteLocalFile(String relativePath) {
            new File(bsDataDirectory, relativePath).delete();
        }


        private JSONObject fetchJson(String url) throws Exception {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent", "DamageCalculator40k");

            try (InputStream in = conn.getInputStream()) {
                byte[] bytes = in.readAllBytes();
                return new JSONObject(new String(bytes));
            } finally {
                conn.disconnect();
            }
        }
}
    public interface UpdateCallbackBsData {
        void onProgress(int current, int total, String filename);
        void onComplete(boolean didUpdate);
        void onError(Exception e);
    }
}
package com.app.DamageCalculator40k.FileHandling;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import core.Abilities.Ability;
import core.AbilityElementAdapter;
import core.DatasheetModeling.Army;
import core.Enums.Faction;

import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import core.FileHandling.FileHandler;
import core.Parsing.Parsing;
import core.Util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public  class AndroidFileHandler extends FileHandler{

    private final String WAHAPEDIA_BASE_URL = "https://wahapedia.ru/wh40k10ed/";
    private final String LAST_ARGUMENT_LENGTH_NAME = "Last_argument_length.txt";


    private final File matchupDirectory;
    private final File armyDirectory;
    private final File wahapediaDataDirectory;
    private final File bsDataDirectory;

    private final Gson gson;
    private final ContentResolver contentResolver;
    private final AssetManager assetManager;


    public static volatile AndroidFileHandler instance;


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

    @Override
    public ArrayList< Pair<String,Faction>> GetXMLData()
    {
        ArrayList<Pair<String,Faction>> retValues = new ArrayList<>();
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

    @Override
    public ArrayList< Pair<String,Faction>> GetJsonData()
    {
        return new ArrayList<>();
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
        if(FileHandler.GetInstance() != null)
        {
            Log.d("FileHandler knas", "Filehandler is already initialized ");
            return;
        }
        context.getContentResolver();
        AndroidFileHandler androidFileHandler = new AndroidFileHandler(context);
        FileHandler.SetFileHandler(androidFileHandler);
        instance = androidFileHandler;
    }

    public static AndroidFileHandler GetInstance( )
    {
        if(instance == null)
        {
            Log.d("File handler knas", "Trying to access the filehandler before it is initialized");
            return null;
        }
        return instance;
    }

    private AndroidFileHandler(Context context)
    {
        synchronized (AndroidFileHandler.class) {

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
}
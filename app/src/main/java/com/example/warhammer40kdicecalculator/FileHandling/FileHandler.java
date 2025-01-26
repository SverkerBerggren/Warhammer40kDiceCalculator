package com.example.warhammer40kdicecalculator.FileHandling;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.AbilityElementAdapter;
import com.example.warhammer40kdicecalculator.DatabaseManager;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.Matchup;
import com.example.warhammer40kdicecalculator.ROSParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.warhammer40kdicecalculator.Parsing.Parsing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
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

    private final Gson gson;
    private final ContentResolver contentResolver;
    private final AssetManager assetManager;


    public static volatile FileHandler instance;


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

    public static String ReadFileAsString(String directory, String name, Context context)
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
                    returnValue.append(new String(Buffer, StandardCharsets.UTF_8));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String UpdateWahapediaData(UpdateArgumentStruct Arguments)
    {
        //  Log.d("Wahapedia grejen: ", "den uppdaterade bra");
        String ReturnValue = "Success!";

        String localLastUpdate = ReadFileAsString(wahapediaDataDirectory.toString(),Arguments.LastUpdateURL,Arguments.context);
        String onlineLastUpdate =  GetOnlineWahapediaResource(Arguments.LastUpdateURL);
        String previousArgumentLengthString = ReadFileAsString(wahapediaDataDirectory.toString(),LAST_ARGUMENT_LENGTH_NAME, Arguments.context);

        if(localLastUpdate.equals(onlineLastUpdate) && Integer.parseInt(previousArgumentLengthString) == Arguments.FilesToDownload.size() )
        {
            DatabaseManager.InitializeDatabaseManager(Arguments.context);
            return "Data is up to date";
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
        DatabaseManager.InitializeDatabaseManager(Arguments.context);
        return(ReturnValue);
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
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
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

            assetManager = context.getAssets();
            contentResolver = context.getContentResolver();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Ability.class, new AbilityElementAdapter());
            gson = gsonBuilder.create();
        }
    }

    public void CreateArmyFromFile(Uri uri)
    {
        File rozFile = new File(uri.getPath());
        Army TestArmy = new Army();
        String fileName = "";
        try {

            InputStream inputStream = contentResolver.openInputStream(uri);
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            ROSParser Parser = new ROSParser();
            //TestArmy = Parser.ParseArmy(result);
            Parsing parser = new Parsing();

            TestArmy = parser.ParseGWListFormat(result);

            TestArmy.name = rozFile.getName();

        }
        catch (Exception e)
        {

            Exception hej = e;
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

}

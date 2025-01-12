package com.example.warhammer40kdicecalculator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.warhammer40kdicecalculator.Parsing.Parsing;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

class UpdateArgumentStruct
{
    public Context context;
    public String URLPrefix = "";
    public String OutputPrefix = "";
    public ArrayList<String> FilesToDownload = new ArrayList<String>();
}

public  class FileHandler  {


    private final File matchupDirectory;
    private final File armyDirectory;
    private final File wahapediaUpdateDate;

    private final Gson gson;
    private final ContentResolver contentResolver;


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

    public void UpdateWahapediaDatabase()
    {

    }



    private  void SaveWahapediaUpdateDate()
    {

    }


    public String UpdateFiles(UpdateArgumentStruct Arguments)
    {
        //  Log.d("Wahapedia grejen: ", "den uppdaterade bra");
        String ReturnValue = "Success!";
        try
        {
            for(String File : Arguments.FilesToDownload)
            {
                URL API_URL = new URL(Arguments.URLPrefix+File);
                HttpURLConnection con = (HttpURLConnection)API_URL.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);

                DataInputStream HTTPInput = new DataInputStream( con.getInputStream() );

                OutputStream FileOutput = new FileOutputStream(Arguments.OutputPrefix+"/"+File);
                int ReadChunkSize = 4096;
                byte[] Buffer = new byte[ReadChunkSize];
                while(true)
                {
                    int ReadBytes = HTTPInput.read(Buffer);
                    FileOutput.write(Buffer,0,ReadBytes);
                    if(ReadBytes < ReadChunkSize)
                    {
                        break;
                    }
                }
                FileOutput.flush();
                FileOutput.close();
                HTTPInput.close();
            }
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
                        Matchup matchup = gson.fromJson(new FileReader(child.getAbsolutePath()), Matchup.class);

                        return matchup;
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

            wahapediaUpdateDate = new File(context.getFilesDir(), "UpdateDate");
            if (!wahapediaUpdateDate.exists()) {
                try {
                    wahapediaUpdateDate.createNewFile();
                } catch (Exception e) {
                    Log.d("knas", "knas med att gora filen for att checka updateringen");
                }
            }
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

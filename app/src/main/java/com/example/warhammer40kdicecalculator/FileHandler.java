package com.example.warhammer40kdicecalculator;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public  class FileHandler extends AppCompatActivity {


    private File matchupDirectory;

    private File armyDirectory;


    private Gson gson;

    private Context context;

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


    public FileHandler(Context context)
    {
        this.context = context;

        matchupDirectory = new File((context.getFilesDir()), "SavedMatchups");
        if(!matchupDirectory.exists()){
            matchupDirectory.mkdir();
        }
        armyDirectory = new File((context.getFilesDir()), "SavedArmies");
        if(!armyDirectory.exists()){
            armyDirectory.mkdir();
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ability.class, new AbilityElementAdapter());
        gson = gsonBuilder.create();


        //gson = new Gson();
    }
    public void CreateArmyFromFile(Uri uri)
    {

        File rozFile = new File(uri.getPath());

        Army TestArmy = new Army();
        String fileName = "";
        try {

            InputStream inputStream = context.getContentResolver().openInputStream(uri);



            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            ROSParser Parser = new ROSParser();
            TestArmy = Parser.ParseArmy(result);


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

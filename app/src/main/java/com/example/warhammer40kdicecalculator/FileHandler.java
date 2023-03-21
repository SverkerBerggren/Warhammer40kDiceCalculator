package com.example.warhammer40kdicecalculator;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public  class FileHandler extends AppCompatActivity {


    private File matchupDirectory;

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




                        Matchup matchup = gson.fromJson(new FileReader(child.getAbsolutePath()), Matchup.class);

                        return matchup;
                    }
                    catch (Exception e)
                    {
                        Log.d("fil knas", e.getMessage());
                    }

                }
                //  ((Button)matchupButton).setText(child.getName());


                //   inflater.inflate(R.,matchupsLayout);

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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ability.class, new AbilityElementAdapter());
        gson = gsonBuilder.create();


        //gson = new Gson();
    }


}

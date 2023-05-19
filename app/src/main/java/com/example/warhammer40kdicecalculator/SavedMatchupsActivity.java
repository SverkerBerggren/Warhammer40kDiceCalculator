package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.HammerOfTheEmperor;
import com.example.warhammer40kdicecalculator.Abilities.ReRollAmountOfHits;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedAttackAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class SavedMatchupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_matchups3);






        LayoutInflater inflater = getLayoutInflater();

        String filename = "myfile";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = getBaseContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            //Log.d("as", "onCreate: hej");
        }
        catch (Exception e)
        {
            Log.d("On create matchups", "onCreate: " + e.getMessage());
        }

    //    Log.d("grejer ", "" + getBaseContext().getFilesDir());

        //File dir = new File(getBaseContext().getFilesDir(), "SavedMatchups");
    //    Log.d("directoryn", "lagnden  " + dir.listFiles().length);
    //    if(!dir.exists()){
    //        dir.mkdir();
//
    //        Log.d("dir", "directory typ elelr?");
    //    }

        File dire = new File(getBaseContext().getFilesDir(),"SavedMatchups");
        File[] directoryListing = dire.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing)
            {
                // Do something with child




                View matchupButton = inflater.inflate(R.layout.matchupbutton,findViewById(R.id.SavedMatchupLinearLayout));

                Button button = matchupButton.findViewById(R.id.MatchupButton);

                button.setText(child.getName());

                button.setId(androidx.constraintlayout.widget.R.id.closest);
              //  ((Button)matchupButton).setText(child.getName());


             //   inflater.inflate(R.,matchupsLayout);

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.

            Log.d("saved matchups", "onCreate: sket sig med att kolla igenom foldern" );
        }

        DamageAmount damageAmount = new DamageAmount(1,1,1);
        RangedAttackAmount rangedAttackAmount = new RangedAttackAmount(2,2,2);

        ArrayList<Ability> listOfabilities = new ArrayList<>();

        listOfabilities.add(new HammerOfTheEmperor());

        ArrayList<RangedWeapon> listOfRangedWeapons = new ArrayList<>();



        RangedWeapon rangedWeapon = new RangedWeapon("test vapnet",2,6,damageAmount,rangedAttackAmount,listOfabilities);

        listOfRangedWeapons.add(rangedWeapon);


        Model spaceMarine = new Model("Space marine", 1,2,3,4,5,6,7,8,listOfabilities,listOfRangedWeapons,null);

        Model guardsman = new Model("Space marine", 123,321,123,4,5,6,7,8,listOfabilities,listOfRangedWeapons,null);

        ArrayList<Model> models = new ArrayList<>();
        models.add(spaceMarine);
        models.add(guardsman);

        ArrayList<Ability> unitAbility = new ArrayList<>();

        unitAbility.add(new ReRollAmountOfHits());

        Unit uniten = new Unit("Spacemarines",200,1,2,3,4,5,6,7,8,models,unitAbility);
     //   Army friendlyArmy = new Army();

        ArrayList<Unit> friendlyUnits = new ArrayList<>();

        friendlyUnits.add(uniten);
        friendlyUnits.add(uniten);

        Army friendlyArmy = new Army("Friendly army ",1,2,3,4,5,6,7,8,friendlyUnits,new ArrayList<Ability>(unitAbility));

        ArrayList<Unit> enemyUnits = new ArrayList<>();

        enemyUnits.add(uniten);
        enemyUnits.add(uniten);
        enemyUnits.add(uniten);
        enemyUnits.add(uniten);
        Army enemyArmy =new Army("Friendly army ",1,2,3,4,5,6,7,8,enemyUnits,new ArrayList<Ability>(unitAbility));

        Matchup forstaMatchupen = new Matchup("Matchup 5",friendlyArmy,enemyArmy);

        Gson gson = new Gson();

        String matchupJsonString = gson.toJson(forstaMatchupen);


        try {





            File gpxfile = new File(dire, forstaMatchupen.name);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(matchupJsonString);


          //  Log.d("json skrivning", "kommer den till slutet " + getBaseContext().getFilesDir());
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("json skrivning", "det sket sig");
        }

    //    Matchup matchupen = new Matchup("forsta matchupen", )

    }
    public void OpenCompareActivity(View v)
    {
        Intent intetionen = new Intent(this, CompareActivity.class);

        intetionen.putExtra( "SourceFile",((Button)v).getText());

        startActivity(intetionen);
    }
}
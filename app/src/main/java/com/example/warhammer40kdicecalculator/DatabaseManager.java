package com.example.warhammer40kdicecalculator;

import android.content.Context;

import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.util.LogPrinter;

import androidx.annotation.RequiresApi;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseManager {

    private final android.content.res.AssetManager assetManager;
    private final String dataDirectory;
    private final HashMap<String,String> UnitNames = new HashMap<>();
    private final HashMap<String, Weapon> WargearWeapons = new HashMap<>();

    public static volatile DatabaseManager instance;

    private final Object lock = new Object();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void InitializeDatabaseManager(Context context)
    {
        if(instance != null)
        {
            Log.d("Database manager"," Database manager is already initialized");
            return;
        }
        instance = new DatabaseManager(context);
    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private DatabaseManager(Context context)
    {   synchronized (lock)
        {
            assetManager = context.getAssets();
            dataDirectory = context.getDataDir().toString() + "/";
            ArrayList<ArrayList<String>> DatasheetWeapons = FileHandler.instance.GetWahapediaDataCSV("Datasheets_wargear.csv");
            if(DatasheetWeapons.size() > 0)
            {
                Log.d("wow","Den parsade sag vad");
            }
        }
    }

    private void CreateWeaponDatabase()
    {
        ArrayList<ArrayList<String>> DatasheetWeapons = FileHandler.instance.GetWahapediaDataCSV("Datasheets_wargear.csv");

        for( ArrayList<String> WeaponEntry : DatasheetWeapons)
        {
            Weapon weaponToConstruct = new Weapon();
            weaponToConstruct.name = WeaponEntry.get(4);
            weaponToConstruct.isMelee =   WeaponEntry.get(7).equals("Melee");

        }
    }

    private <DiceAmount> ParseDiceAmount(String string)
    {
        DiceAmount hej;
        String[] components = string.split(" ");
        for(String component : components)
        {
            if(component.equals("+"))
            {
                continue;
            }
            try {
                int numberOfAttacks =
            }
            catch (Exception exception){}


        }
    }

    public ArrayList<ArrayList<String>> ReadCsvFile(String fileName )
    {   synchronized (lock)
        {
            ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
            try {
                InputStreamReader is = new InputStreamReader(assetManager.open(dataDirectory + fileName));

                BufferedReader reader = new BufferedReader(is);
                String readString = "";
                String[] tempStringArray;

                while ((readString = reader.readLine()) != null) {
                    tempStringArray = readString.split("\\|");
                    ArrayList<String> tempArrayList = new ArrayList<String>();
                    for (int i = 0; i < tempStringArray.length; i++) {
                        tempArrayList.add(tempStringArray[i]);
                    }

                    arrayListToReturn.add(tempArrayList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arrayListToReturn;
        }
    }
}

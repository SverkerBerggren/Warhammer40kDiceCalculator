package com.example.warhammer40kdicecalculator;

import android.content.Context;

import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.util.LogPrinter;

import androidx.annotation.RequiresApi;

import com.example.warhammer40kdicecalculator.DatasheetModeling.DiceAmount;
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
            CreateWeaponDatabase();

        }
    }

    private void CreateWeaponDatabase()
    {
        ArrayList<ArrayList<String>> DatasheetWeapons = FileHandler.instance.GetWahapediaDataCSV("Datasheets_wargear.csv");

        int amountNotLength13 = 0;
        ArrayList<Integer> whatValue = new ArrayList<>();
        ArrayList<String> nameOfWeapons = new ArrayList<>();
        for(ArrayList<String> entry : DatasheetWeapons)
        {
            if(entry.size() != 13)
            {
                amountNotLength13++;
                whatValue.add(entry.size());
                if(entry.size() > 4)
                {
                    nameOfWeapons.add(entry.get(4));
                }
            }
        }

        for( ArrayList<String> weaponEntry : DatasheetWeapons)
        {
            Weapon weaponToConstruct = new Weapon();
            weaponToConstruct.name = weaponEntry.get(4);
            weaponToConstruct.isMelee =  weaponEntry.get(7).equals("Melee");
            weaponToConstruct.amountOfAttacks = ParseDiceAmount(weaponEntry.get(8));
            try {
                weaponToConstruct.ballisticSkill = Integer.parseInt(weaponEntry.get(9));
                weaponToConstruct.strength = Integer.parseInt(weaponEntry.get(10));
                weaponToConstruct.ap = Integer.parseInt(weaponEntry.get(11));
                weaponToConstruct.damageAmount = ParseDiceAmount(weaponEntry.get(12));
            }
            catch (Exception e)
            {
                Log.d("Weapon parsing","weapon entry did not match expected format");
            }
            WargearWeapons.put(weaponToConstruct.name,weaponToConstruct);
        }
    }

    private DiceAmount ParseDiceAmount(String string)
    {
        DiceAmount returnValue = new DiceAmount();
        String[] components = string.split("\\+");
        for(String component : components)
        {
            try {
                returnValue.baseAmount = Integer.parseInt(component);
                continue;
            }
            catch (Exception exception){}

            try {
                int diceAmount =  component.charAt(0) -'0';
                if(component.charAt(2) == '6')
                {
                    returnValue.numberOfD6 = diceAmount;
                }
                else
                {
                    returnValue.numberOfD3 = diceAmount;
                }
            }
            catch (Exception e)
            {
                Log.d("Dice parsing","dice value did not match expected format");
            }

        }
        return returnValue;
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

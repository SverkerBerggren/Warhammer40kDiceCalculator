package com.example.warhammer40kdicecalculator;

import android.content.Context;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.warhammer40kdicecalculator.DatasheetModeling.DiceAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {

    private final android.content.res.AssetManager assetManager;
    private final String dataDirectory;
    // Weapon names have a one to many relationship
    private final HashMap<IdNameKey, Weapon> DatasheetWargear = new HashMap<>();
    // Datasheet names are unique
    private final HashMap<String, Unit> Datasheets = new HashMap<>();
    //  Id have a one to many relationship
    private final HashMap<IdNameKey, Model> modelsDatasheet = new HashMap<>();
    public static volatile DatabaseManager instance;

    private final Object lock = new Object();

    public HashMap<IdNameKey, Weapon> GetDatasheetWargearDatabase() {return  DatasheetWargear;}
    public HashMap<String, Unit> GetDatasheetsDatabase() {return  Datasheets;}
    public HashMap<IdNameKey, Model> GetModelsDatasheetDatabase() {return  modelsDatasheet;}

    public static class IdNameKey
    {
        private String name;
        private String wahapediaId;

        public IdNameKey(String name, String wahapediaId)
        {
            this.name = name;
            this.wahapediaId = wahapediaId;
        }

        @Override
        public boolean equals(Object other)
        {
            if(this == other)
            {
                return true;
            }
            if (!(other instanceof IdNameKey))
            {
             return false;
            }
            IdNameKey otherKey = (IdNameKey)other;
            return name.equals(otherKey.name) && wahapediaId.equals(otherKey.wahapediaId);
        }
    }

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
            CreateDatasheetDatabase();
            CreateModelsDatasheet();
        }
    }
    private  void CreateModelsDatasheet()
    {
        ArrayList<ArrayList<String>> modelsData = FileHandler.instance.GetWahapediaDataCSV("Datasheets_models.csv");

        for( ArrayList<String> modelEntry : modelsData)
        {
            if(modelEntry.size() < 12)
            {
                Log.d("Models database", "Invalid model entry length");
                continue;
            }
            Model model = new Model();

            model.wahapediaDataId = modelEntry.get(0);
            model.name = modelEntry.get(2);
            try {
                model.toughness = Integer.parseInt( modelEntry.get(4));
                model.armorSave = Integer.parseInt( modelEntry.get(5).split("\\+")[0]);
                if(!modelEntry.get(6).equals("-"))
                {
                    model.invulnerableSave = Integer.parseInt( modelEntry.get(6));
                }
                model.wounds = Integer.parseInt( modelEntry.get(8));
            }
            catch (Exception e)
            {
                Log.d("Model database",e.getMessage());
                continue;
            }

            modelsDatasheet.put(new IdNameKey(model.wahapediaDataId,model.name),model);
        }
    }
    private  void CreateDatasheetDatabase()
    {
        ArrayList<ArrayList<String>> datasheetsData = FileHandler.instance.GetWahapediaDataCSV("Datasheets.csv");

        for( ArrayList<String> datasheetEntry : datasheetsData)
        {
            if(datasheetEntry.size() != 14)
            {
                Log.d("Datasheet database", "Invalid datasheet entry length");
                continue;
            }
            Unit unitDatasheet = new Unit();
            unitDatasheet.wahapediaDataId = datasheetEntry.get(0);
            unitDatasheet.unitName = datasheetEntry.get(1);

            Datasheets.put(unitDatasheet.unitName,unitDatasheet);
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
            if(weaponEntry.size() != 13)
            {
                continue;
            }
            Weapon weaponToConstruct = new Weapon();
            weaponToConstruct.WahapediaDataId = weaponEntry.get(0);
            weaponToConstruct.name = weaponEntry.get(4);
            weaponToConstruct.isMelee =  weaponEntry.get(7).equals("Melee");
            weaponToConstruct.amountOfAttacks = ParseDiceAmount(weaponEntry.get(8));
            try {
                if (!weaponEntry.get(9).equals("N/A"))
                {
                    weaponToConstruct.ballisticSkill = Integer.parseInt(weaponEntry.get(9));
                }
                weaponToConstruct.strength = Integer.parseInt(weaponEntry.get(10));
                weaponToConstruct.ap = Integer.parseInt(weaponEntry.get(11));
                weaponToConstruct.damageAmount = ParseDiceAmount(weaponEntry.get(12));
            }
            catch (Exception e)
            {
                Log.d("Weapon parsing",e.getMessage());
            }
            DatasheetWargear.put(new IdNameKey(weaponEntry.get(0),weaponToConstruct.name),weaponToConstruct);
        }
    }

    private DiceAmount ParseDiceAmount(String string)
    {
        DiceAmount returnValue = new DiceAmount();
        String[] components = string.split("\\+");
        for(String component : components)
        {
            component = component.trim();
            // Doubtful if there is a case in the game where there are more than 9 D3/D6 but it covers that case
            StringBuilder dicePrefix = new StringBuilder();
            boolean isDiceValue = false;
            char diceSuffix = '0';

            for(int i = 0; i < component.length(); i++ )
            {
                if(component.charAt(i) == 'D')
                {
                    isDiceValue = true;
                    continue;
                }

                if(Character.isDigit(component.charAt(i)) )
                {
                    if(!isDiceValue)
                    {
                        dicePrefix.append(component.charAt(i));
                    }
                    else
                    {
                        if(component.charAt(i) == '3' || component.charAt(i) == '6')
                        {
                            diceSuffix = component.charAt(i);
                        }
                        else
                        {
                            Log.d("Dice parsing", "Invalid dice suffix, only D3 and D6 exists");
                        }
                    }
                    continue;
                }
                Log.d("Dice parsing", "Unexpected character found in dice component");
            }
            try {
                if(!isDiceValue)
                {
                    returnValue.baseAmount = Integer.parseInt(component);
                }
                else
                {
                    int diceAmount = 1;
                    if(dicePrefix.length() != 0)
                    {
                        diceAmount = Integer.parseInt(dicePrefix.toString());
                    }
                    if(diceSuffix == '3')
                    {
                        returnValue.numberOfD3 = diceAmount;
                    }
                    else
                    {
                        returnValue.numberOfD6 = diceAmount;
                    }
                }
            }
            catch (Exception exception)
            {
                Log.d("Dice parsing", "Failed to convert dice representation");
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

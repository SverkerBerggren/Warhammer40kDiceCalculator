package com.example.warhammer40kdicecalculator;

import android.content.Context;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.LogPrinter;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseManager {

    private final android.content.res.AssetManager assetManager;
    private final HashMap<String,String> UnitNames = new HashMap<>();
    private final HashMap<String, Integer> listOfAllUnitId = new HashMap<String, Integer>();

    public static volatile DatabaseManager instance;

    private Object lock = new Object();

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

    private DatabaseManager(Context context)
    {   synchronized (lock)
        {
            assetManager = context.getAssets();
            ArrayList<ArrayList<String>> DatasheetWeapons = ReadCsvFile("Datasheets_wargear.csv");
        }
    }

    public ArrayList<ArrayList<String>> ReadCsvFile(String fileName )
    {
        ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
        try
        {
            InputStreamReader is = new InputStreamReader(assetManager.open(fileName));

            BufferedReader reader = new BufferedReader(is);
            String readString = "";
            String[] tempStringArray;

            while((readString = reader.readLine()) != null)
            {
                tempStringArray = readString.split("\\|");
                ArrayList<String> tempArrayList = new ArrayList<String>();
                for(int i = 0; i < tempStringArray.length; i++)
                {
                    tempArrayList.add(tempStringArray[i]);
                }

                arrayListToReturn.add(tempArrayList);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return arrayListToReturn;
    }
}

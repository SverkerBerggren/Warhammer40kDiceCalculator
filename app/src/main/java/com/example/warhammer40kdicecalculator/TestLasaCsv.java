package com.example.warhammer40kdicecalculator;

import android.content.Context;

import android.util.Log;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TestLasaCsv {

    private Context context;
    public HashMap<String, Integer> listOfAllUnitId = new HashMap<String, Integer>();

    private HashMap<String,String> UnitNames = new HashMap<>();

    public TestLasaCsv(Context context)
    {
        this.context = context;
    }

    public ArrayList<ArrayList<String>> ReadCsvFile(String fileName )
    {
        ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
        try
        {

            InputStreamReader is = new InputStreamReader(context.getAssets().open(fileName));

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

    public List<Model> createModelsFromBattleScribe(String fileName )
    {



        return  null;
    }

}

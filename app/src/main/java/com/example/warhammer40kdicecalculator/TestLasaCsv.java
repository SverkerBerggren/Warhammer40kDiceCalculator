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
 //   AssetManager assetManager = getAssets();

 //  try {
 //      InputStreamReader is = new InputStreamReader(getAssets().open("filename.csv"));
 //  }
 //  catch(IOException e)
 //  {
 //      e.printStackTrace();
 //  }
    private Context context;
    public HashMap<String, Integer> listOfAllUnitId = new HashMap<String, Integer>();

    public TestLasaCsv(Context context)
    {
        this.context = context;
    }


    public void testReadFile()
    {
        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open("Datasheets.csv"));

            BufferedReader reader = new BufferedReader(is);

            String testRad = reader.readLine();
            int tempInt = 0;
            while((testRad = reader.readLine()) != null)
            {
                String[] testInputen = testRad.split("\\|");
                tempInt = Integer.getInteger(testInputen[1]);



            }

            Log.d("tester", "" + tempInt);



            //Log.d("filereader", testInputen[0] + " " + testInputen[1] + "hej hej") ;
            //Log.d("filereader", testInputen[0] + " " + testInputen[1] + "hej hej") ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> ReadCsvFile(String fileName )
    {
        ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
        try
        {
          //  ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();

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

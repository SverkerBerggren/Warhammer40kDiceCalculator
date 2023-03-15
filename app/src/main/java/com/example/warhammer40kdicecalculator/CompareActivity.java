package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;

import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        LayoutInflater inflater =  getLayoutInflater();


        Unit testUnitJson = new Unit();
        File dir = new File(getBaseContext().getFilesDir(), "SavedMatchups");
        if(!dir.exists()){
            dir.mkdir();
        }
        try {

            Unit testUnit = new Unit();
            Gson gson = new Gson();
            Model testModel = new Model();

        //    testModel.listOfAbilites.add()

            testUnit.listOfAbilitys.add(new ReRollAmountOfHits());
            testUnit.listOfAbilitys.add(new HammerOfTheEmperor());



            testUnit.listOfModels.add(testModel);

            testModel.listOfAbilites.add(new HammerOfTheEmperor());

            String jsonString = gson.toJson(testUnit);
            Log.d("json check",  jsonString);





            File gpxfile = new File(dir, "arme 1 json");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(jsonString);


            Log.d("json skrivning", "kommer den till slutet " + getBaseContext().getFilesDir());
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            Log.d("json skrivning", "det sket sig");
        }

        View viewToModify = inflater.inflate(R.layout.unitviewprefab, findViewById(R.id.VerticalLayout));

        View viewIgen =  viewToModify.findViewById(R.id.TestId);











    }

    private HashMap<String, DataSheet> datasheetMap = new HashMap<>();


    private void createArmies()
    {
        
    }



    public void DropDownClick(View v)
    {
        Context context = v.getContext();


        ConstraintLayout constraintLayout = (ConstraintLayout) v.getParent();

        Log.d("Ui grejer", "Vad är höjden innan" +  constraintLayout.getMeasuredHeight());


        ViewGroup viewGroup = (ViewGroup) constraintLayout;

        for(int i = 0; i < viewGroup.getChildCount(); i++)
        {
            if(viewGroup.getChildAt(i) != v)
            {
                if(viewGroup.getChildAt(i).getVisibility() == View.VISIBLE)
                {
                    viewGroup.getChildAt(i).setVisibility(View.GONE);
                }
                else
                {
                    viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }

        Log.d("Ui grejer", "Vad är höjden efter" +  constraintLayout.getMeasuredHeight());



        Log.d("Ui grejer", "" +  v.getParent());
    }
}
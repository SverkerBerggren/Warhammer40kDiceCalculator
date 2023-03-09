package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.HashMap;

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        LayoutInflater inflater =  getLayoutInflater();



        View viewToModify = inflater.inflate(R.layout.unitviewprefab, findViewById(R.id.VerticalLayout));



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
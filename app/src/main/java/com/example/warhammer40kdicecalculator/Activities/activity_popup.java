package com.example.warhammer40kdicecalculator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.warhammer40kdicecalculator.R;

public class activity_popup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
   //     View popup = findViewById(R.id.Popup);
    //    popup.setVisibility(View.GONE);
    }

    public void OpenStats(View v)
    {
        Intent intenten = new Intent(this, StatisticActivity.class);

        startActivity(intenten);
    }

    public void ShowPopup(View v)
    {
   //     View popup = findViewById(R.id.Popup);
   //     popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v)
    {
     //   View popup = findViewById(R.id.Popup);
      //  popup.setVisibility(View.GONE);
    }

}
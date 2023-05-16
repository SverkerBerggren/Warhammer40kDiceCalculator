package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ActivityCreateMatchup extends AppCompatActivity {

    private Context context ;


    private ArrayList<CheckBox> clickedCheckBoxes = new ArrayList<>();
    FileHandler fileHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_matchup );


        context = getBaseContext();

        fileHandler = new FileHandler(context);

        findViewById(R.id.CreateMatchupButtonYo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateMatchup();
            }
        });

        CreateMatchupButtons();

    }


    public void CreateMatchupButtons()
    {

        ArrayList<String> armies = fileHandler.GetSavedArmies();

        LinearLayout linearLayout = findViewById(R.id.LinearLayoutArmies);

        for(String army : armies )
        {
            CheckBox checkBox = new CheckBox(context);

            checkBox.setText(army);



            checkBox.setOnClickListener(new OnCheckBoxClick());


            linearLayout.addView(checkBox);




        }
    }


    private class OnCheckBoxClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            CheckBox checkBoxClicked = (CheckBox) view;

            Boolean checked = checkBoxClicked.isChecked();

            if(checked)
            {   checkBoxClicked.setChecked(false);
                if(clickedCheckBoxes.size() != 2)
                {
                    clickedCheckBoxes.add(checkBoxClicked);
                    checkBoxClicked.setChecked(true);
                }
            }
            else
            {

                checkBoxClicked.setChecked(false);
                clickedCheckBoxes.remove(checkBoxClicked);

            }



        }
    }

    private void CreateMatchup()
    {
        if(clickedCheckBoxes.size() != 2)
        {
            return;
        }
        TextInputEditText nameText = findViewById(R.id.InputFieldName);

        Army friendlyArmy = fileHandler.getArmy(clickedCheckBoxes.get(0).getText().toString());
        Army enemyArmy = fileHandler.getArmy(clickedCheckBoxes.get(0).getText().toString());

        Matchup matchup = new Matchup(nameText.getText().toString(),friendlyArmy,enemyArmy);


        fileHandler.saveMatchup(matchup);

        Toast.makeText(context, "Matchup created", Toast.LENGTH_SHORT).show();
    }
}
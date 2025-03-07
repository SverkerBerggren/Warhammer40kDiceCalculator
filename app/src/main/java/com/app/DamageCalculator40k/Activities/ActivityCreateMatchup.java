package com.app.DamageCalculator40k.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ActivityCreateMatchup extends AppCompatActivity {

    private Context context ;
    private ArrayList<CheckBox> clickedCheckBoxes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_matchup );
        context = getBaseContext();

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

        ArrayList<String> armies = FileHandler.GetInstance().GetSavedArmies();

        LinearLayout linearLayout = findViewById(R.id.LinearLayoutArmies);

        for(String army : armies )
        {
            CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));

            checkBox.setText(army);


            checkBox.setTextSize(20);
        //    checkBox.setTextColor(androidx.appcompat.R.attr.colorPrimaryDark);


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

        Army friendlyArmy = FileHandler.GetInstance().getArmy(clickedCheckBoxes.get(0).getText().toString());
        Army enemyArmy = FileHandler.GetInstance().getArmy(clickedCheckBoxes.get(1).getText().toString());

        Matchup matchup = new Matchup(nameText.getText().toString(),friendlyArmy,enemyArmy);


        FileHandler.GetInstance().saveMatchup(matchup);

        Toast.makeText(context, "Matchup created", Toast.LENGTH_SHORT).show();
    }
}
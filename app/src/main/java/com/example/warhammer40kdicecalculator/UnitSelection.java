package com.example.warhammer40kdicecalculator;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class UnitSelection extends AppCompatActivity {

    private boolean myUnitsAttacking = true;
    private LinearLayout attackingLayout;
    private RadioGroup defendingGroup;
    private Context context;
    private Matchup matchup;
    private TextView attackingText;
    private TextView defendingText;
    private ArrayList<Unit> friendlyArmy;
    private ArrayList<Unit> enemyArmy;
    private LinearLayout chosenAttackingUnits;
    private LinearLayout chosenDefendingUnits;

    private HashMap<Integer,Unit> listOfAttackingUnits = new HashMap<Integer,Unit>();
    private int defendingUnitIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_selection);
        context = getBaseContext();

        FileHandler fileHandler = new FileHandler(context);
        //Log.d("testar intetion", "onCreate: " + getIntent().getStringExtra("SourceFile"));
        if (fileHandler.getMatchup( getIntent().getStringExtra("SourceFile")) != null)
        {
            matchup = fileHandler.getMatchup( getIntent().getStringExtra("SourceFile"));
        }
        else
        {
            matchup = fileHandler.getMatchup("Matchu fran ny");
        }

        attackingLayout = findViewById(R.id.AttackingLayout);
        attackingText = findViewById(R.id.attackingText);
        defendingGroup = findViewById(R.id.DefendingGroup);
        defendingText = findViewById(R.id.defendingText);
        chosenAttackingUnits = findViewById(R.id.ChosenAttackingUnits);
        chosenDefendingUnits = findViewById(R.id.ChosenDefendingUnit);


        friendlyArmy = matchup.friendlyArmy.units;
        enemyArmy = matchup.enemyArmy.units;

        CreateUnits(myUnitsAttacking);
    }

    private void CreateUnits(boolean iAttack)
    {
        if (iAttack)
        {
            attackingLayout.removeAllViews();
            defendingGroup.removeAllViews();

            attackingText.setText("My Units");
            defendingText.setText("Enemy Units");

            for (int i = 0; i < friendlyArmy.size(); i++)
            {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(friendlyArmy.get(i).unitName);
                checkBox.setOnClickListener(new ClickListenerChoice( true, friendlyArmy.get(i), i));
                attackingLayout.addView(checkBox);


            }
            for (int i = 0; i < enemyArmy.size(); i++)
            {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(enemyArmy.get(i).unitName);
                radioButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                radioButton.setOnClickListener(new ClickListenerChoice(false, enemyArmy.get(i), i));
                defendingGroup.addView(radioButton);
            }
        }
        else
        {
            attackingLayout.removeAllViews();
            defendingGroup.removeAllViews();

            attackingText.setText("Enemy Units");
            defendingText.setText("My Units");

            for (int i = 0; i < enemyArmy.size(); i++)
            {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(enemyArmy.get(i).unitName);
                checkBox.setOnClickListener(new ClickListenerChoice(true, enemyArmy.get(i), i));
                attackingLayout.addView(checkBox);
            }
            for (int i = 0; i < friendlyArmy.size(); i++)
            {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(friendlyArmy.get(i).unitName);
                radioButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                radioButton.setOnClickListener(new ClickListenerChoice(false, friendlyArmy.get(i), i));
                defendingGroup.addView(radioButton);
            }
        }

    }



    public void ChangeAttacking(View v)
    {
        myUnitsAttacking = !myUnitsAttacking;
        CreateUnits(myUnitsAttacking);
        chosenAttackingUnits.removeAllViews();
        chosenDefendingUnits.removeAllViews();
    }

    public void GoToStatistics(View v)
    {
        Intent intenten = new Intent(this, StatisticActivity.class);

        intenten.putExtra("AttackingUnitSize", listOfAttackingUnits.size());
        intenten.putExtra("MatchupName", matchup.name);
        int loops = 0;
        for (int key : listOfAttackingUnits.keySet())
        {
            intenten.putExtra("IndexOfUnitAttacking" + loops, key);
            loops++;
        }

        intenten.putExtra("IndexOfUnitDefending", defendingUnitIndex);
        intenten.putExtra("FirendlyArmyAttacking", myUnitsAttacking);

        startActivity(intenten);
    }

    class ClickListenerChoice implements View.OnClickListener
    {
        Boolean attacking;
        private Unit unitAdded;
        String name;
        int index;
        ClickListenerChoice(boolean attacking, Unit unitAdded, int index)
        {
            this.attacking = attacking;
            this.unitAdded = unitAdded;
            name = unitAdded.unitName;
            this.index = index;
        }
        @Override
        public void onClick(View view) {
            boolean checked = false;
            if (view instanceof RadioButton)
            {
                checked = ((RadioButton) view).isChecked();
            }
            else if (view instanceof  CheckBox)
            {
                checked = ((CheckBox) view).isChecked();
            }

            if (!checked)
            {
                if (attacking)
                {
                    Button bttnToRemove = chosenAttackingUnits.findViewWithTag(name);
                    listOfAttackingUnits.remove(unitAdded);
                    chosenAttackingUnits.removeView(bttnToRemove);
                }
                else
                {
                    Button bttnToRemove = chosenDefendingUnits.findViewWithTag(name);
                    chosenDefendingUnits.removeView(bttnToRemove);
                    defendingUnitIndex = -1;
                }
            }
            else
            {
                Button bttn = new Button(context);
                bttn.setTag(name);
                bttn.setText(name);
                if (attacking)
                {
                    chosenAttackingUnits.addView(bttn);
                    listOfAttackingUnits.put(index, unitAdded);
                }
                else
                {
                    chosenDefendingUnits.removeAllViews();
                    chosenDefendingUnits.addView(bttn);
                    defendingUnitIndex = index;
                }
            }
        }
    }
}


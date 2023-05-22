package com.example.warhammer40kdicecalculator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

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

    private ActivityResultLauncher<Intent> launchConditionsActivity;

    private ActivityResultLauncher<Intent> launchUnitEditActivity;


    private Conditions conditions = new Conditions();

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


        launchConditionsActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new  UnitSelection.UpdateConditions());
        launchUnitEditActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new UnitSelection.UpdateUnit());

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

    private class  UpdateConditions implements ActivityResultCallback<ActivityResult>
    {

        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK)
            {
                conditions =new Conditions( result.getData().getStringExtra("" + R.string.CONDITIONS));
            }
        }
    }
    private class  UpdateUnit implements ActivityResultCallback<ActivityResult>
    {

        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK)
            {
              //  conditions =new Conditions( result.getData().getStringExtra("" + R.string.CONDITIONS));
            }
        }
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
                CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));
                checkBox.setTextSize(14);

                checkBox.setText(friendlyArmy.get(i).unitName);
                checkBox.setOnClickListener(new ClickListenerChoice( true, friendlyArmy.get(i), i,"friendly"));
                attackingLayout.addView(checkBox);


            }
            for (int i = 0; i < enemyArmy.size(); i++)
            {
                RadioButton radioButton = new RadioButton(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_RadioButton));
                radioButton.setTextSize(14);

                radioButton.setText(enemyArmy.get(i).unitName);
                radioButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                radioButton.setOnClickListener(new ClickListenerChoice(false, enemyArmy.get(i), i,"enemy"));
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
                CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));
                checkBox.setTextSize(14);

                checkBox.setText(enemyArmy.get(i).unitName);
                checkBox.setOnClickListener(new ClickListenerChoice(true, enemyArmy.get(i), i,"enemy"));
                attackingLayout.addView(checkBox);
            }
            for (int i = 0; i < friendlyArmy.size(); i++)
            {
                RadioButton radioButton = new RadioButton(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_RadioButton));
                radioButton.setTextSize(14);
                radioButton.setText(friendlyArmy.get(i).unitName);
                radioButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                radioButton.setOnClickListener(new ClickListenerChoice(false, friendlyArmy.get(i), i,"friendly"));
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

        intenten.putExtra("" + R.string.CONDITIONS, conditions.toString());

        startActivity(intenten);
    }

    class ClickListenerChoice implements View.OnClickListener
    {
        Boolean attacking;
        private Unit unitAdded;
        String name;
        int index;

        private String allegiance;

        ClickListenerChoice(boolean attacking, Unit unitAdded, int index, String allegiance)
        {
            this.attacking = attacking;
            this.unitAdded = unitAdded;
            name = unitAdded.unitName;
            this.index = index;

            this.allegiance = allegiance;
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
                    listOfAttackingUnits.remove(index);
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


                bttn.setOnClickListener(new OnClickListenerEditUnit( new UnitIdentifier(allegiance,null,index,matchup.name)));
            }
        }
    }

    private class OnClickListenerEditUnit implements View.OnClickListener
    {

        private UnitIdentifier unitId;

        public OnClickListenerEditUnit(UnitIdentifier unitId)
        {
            this.unitId = unitId;
        }

        @Override
        public void onClick(View view) {
            StartUnitEditActivity(unitId);
        }
    }

    public void StartConditionActivity(View view)
    {
        Intent intent = new Intent(this, ConditionsActivity.class);

        intent.putExtra(""+R.string.CONDITIONS,conditions.toString());

        launchConditionsActivity.launch(intent);
    }


    public void StartUnitEditActivity(UnitIdentifier unitId)
    {
        Intent intent = new Intent(this, EditUnitActivity.class);

        intent.putExtra("matchup",matchup.name);

        intent.putExtra(""+R.string.UNIT_IDENTIFIER, unitId.toString());


        launchUnitEditActivity.launch(intent);

    }
}


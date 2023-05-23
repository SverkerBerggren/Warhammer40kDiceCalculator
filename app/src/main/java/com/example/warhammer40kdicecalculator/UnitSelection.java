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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
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


    private ActivityResultLauncher<Intent> activityResultLauncherAbility;

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

        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new UpdateAbilitiesStub());


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


    private class  UpdateAbilitiesStub implements ActivityResultCallback<ActivityResult>
    {

        @Override
        public void onActivityResult(ActivityResult result) {

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
                    View bttnToRemove = chosenAttackingUnits.findViewWithTag(name);
                    listOfAttackingUnits.remove(index);
                    chosenAttackingUnits.removeView(bttnToRemove);
                }
                else
                {
                    View bttnToRemove = chosenDefendingUnits.findViewWithTag(name);
                    chosenDefendingUnits.removeView(bttnToRemove);
                    defendingUnitIndex = -1;
                }
            }
            else
            {
            //    Button bttn = new Button(context);
            //    bttn.setTag(name);
            //    bttn.setText(name);

                View bttn = CreateTableRow(name,index,allegiance);
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


            //    bttn.setOnClickListener(new OnClickListenerEditUnit( new UnitIdentifier(allegiance,null,index,matchup.name)));
            }
        }
    }

    private View CreateTableRow(String name,int index,String allegiance)
    {

        UnitIdentifier unitId = new UnitIdentifier(allegiance,null,index,matchup.name);
        Button bttn = new Button(context);
        bttn.setTag(name);
        bttn.setText(name);
        bttn.setOnClickListener(new OnClickListenerEditUnit( unitId));


        Button editAbilitiesButton = new Button(context);
        editAbilitiesButton.setTag(name);
        editAbilitiesButton.setText("Abilities");

        editAbilitiesButton.setOnClickListener(new StartAbilitiesEdit(unitId));


        TableRow tableRow = new TableRow(context);

        tableRow.addView(bttn);
        tableRow.addView(editAbilitiesButton);

        tableRow.setTag(name);


        TableLayout tableLayout = new TableLayout(context);

        tableLayout.setTag(name);

        tableLayout.addView(tableRow);




      //  editAbilitiesButton.setOnClickListener();



        return tableLayout;
    }

    private class StartAbilitiesEdit implements View.OnClickListener
    {

        private UnitIdentifier unitIdentifier;


        public StartAbilitiesEdit(UnitIdentifier unitIdentifier)
        {
            this.unitIdentifier = unitIdentifier;

        }

        @Override
        public void onClick(View view) {



            Intent intent = new Intent(context, Activity_Edit_Abilities.class);
            // Identifier

            //UnitIdentifier identifier = (UnitIdentifier)view.getTag(R.string.UNIT_IDENTIFIER);
            intent.putExtra("" + R.string.UNIT_IDENTIFIER, unitIdentifier.toString());
            intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "unit");


            UIIdentifier uiId = new UIIdentifier("stub??",unitIdentifier);

            intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

            intent.putExtra("matchupName", matchup.name);



            activityResultLauncherAbility.launch(intent);
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


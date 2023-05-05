package com.example.warhammer40kdicecalculator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;

public class Activity_Edit_Abilities extends AppCompatActivity implements AbilityUIHolder  {
    Context context;
    LayoutInflater inflater;
    FileHandler fileHandler;
    Matchup matchup;

    TableLayout tableLayoutAbilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_abilities);

        Intent intent = getIntent();
        context = getBaseContext();
        inflater = getLayoutInflater();

        String unitAlliegance = intent.getStringExtra("" + R.string.UNIT_ALLEGIANCE);

        int indexUnit = intent.getIntExtra(""+R.string.UNIT_NUMBER, -1);
        int indexModel = intent.getIntExtra("indexModel", -1);
        String matchupName = intent.getStringExtra("matchupName");
        fileHandler = new FileHandler(context);
        matchup = fileHandler.getMatchup(matchupName);


        tableLayoutAbilities = findViewById(R.id.TableLayoutEditAbilities);

        Army army;

        ImageButton addAbilityButton = findViewById(R.id.EditAbilitiesAdd);

        if( unitAlliegance.equals("friendly"))
        {
            army = matchup.friendlyArmy;
        }
        else
        {
            army = matchup.enemyArmy;
        }

        if(indexUnit == -1)
        {

            for(Ability ability : army.abilities)
            {

            }
        }
        else if(indexModel == -1)
        {
            Unit unit = army.units.get(indexUnit);

            for(Ability ability : unit.listOfAbilitys)
            {
                CreateButton(ability,unit);
            }

            MainActivity mainActivity = new MainActivity();
            Activity_Edit_Abilities abilityHolder = this;
            addAbilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.SearchAbility(unit,matchup,findViewById(R.id.ConstraintLayoutEditAbilities),context,abilityHolder);
                }
            });

        }
        else
        {

        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void CreateButton(Ability ability, Unit unit)
    {
        TableRow tableRow = new TableRow(context);
        CheckBox checkBox = new CheckBox(context);
        ImageButton imageButton = new ImageButton(context);

        checkBox.setChecked(true);
        checkBox.setText(ability.name);
        checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        checkBox.setOnCheckedChangeListener (new OnCheckedChangeAbility(ability));
        imageButton.setImageResource(com.google.android.material.R.drawable.abc_ic_menu_cut_mtrl_alpha);

        imageButton.setOnClickListener(new OnClickRemoveAbility(unit,ability));

        tableRow.addView(checkBox);
        tableRow.addView(imageButton);

        tableLayoutAbilities.addView(tableRow,0);
    }

    private class OnClickRemoveAbility implements View.OnClickListener
    {
        Ability ability;

        Unit unit;

        public OnClickRemoveAbility(Unit unit,Ability ability)
        {
            this.unit = unit;
            this.ability = ability;



        }

        @Override
        public void onClick(View view) {

            boolean bool = unit.listOfAbilitys.remove(ability);

            Log.d("tog den bort ability", "" + bool);

            fileHandler.saveMatchup(matchup);

            TableLayout viewParent = (TableLayout)view.getParent().getParent();

            viewParent.removeView((TableRow)view.getParent());

        }
    }

    private class OnCheckedChangeAbility implements CheckBox.OnCheckedChangeListener
    {
        private Ability ability;

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


            ability.active = b;

            Log.d("Abilityns varde ar ", ""+ ability.active);
        }

        public OnCheckedChangeAbility(Ability ability)
        {
            this.ability = ability;
        }
    }

    @Override
    public void AbilityAdded(Ability ability, AbilityHolder abilityHolder) {


        Unit unit = (Unit)abilityHolder;

        CreateButton(ability,unit);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

            }
        }
    }

}
package com.example.warhammer40kdicecalculator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

import java.util.ArrayList;
import java.util.Collections;

public class Activity_Edit_Abilities extends AppCompatActivity implements AbilityUIHolder  {
    Context context;
    LayoutInflater inflater;
    FileHandler fileHandler;
    private Matchup matchup;

    private Identifier identifier;

    private String uiElement;

    TableLayout tableLayoutAbilities;


    private ArrayList<String> abilitiesAdded = new ArrayList<>();

    private ArrayList<String> abilitiesRemoved = new ArrayList<>();



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



        String typeOfIdentifier = intent.getStringExtra( ""+R.string.TYPE_OF_IDENTIFIER);

        ImageButton addAbilityButton = findViewById(R.id.EditAbilitiesAdd);
        fileHandler = new FileHandler(context);
        matchup = fileHandler.getMatchup(matchupName);

        uiElement =  intent.getStringExtra(""+R.string.UI_IDENTIFIER);



        tableLayoutAbilities = findViewById(R.id.TableLayoutEditAbilities);


        ScrollView scrollView = findViewById(R.id.ScrollViewEditAbilites);




        Army army = null;


        if(typeOfIdentifier.equals("model"))
        {
            identifier = new ModelIdentifier(intent.getStringExtra( ""+R.string.MODEL_IDENTIFIER));

            Model model = matchup.GetModel((ModelIdentifier) identifier);

            for(Ability ability : model.listOfAbilites)
            {
                CreateButton(ability,model);
            }

            MainActivity mainActivity = new MainActivity();
            Activity_Edit_Abilities abilityHolder = this;
            addAbilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.SearchAbility(model,matchup,findViewById(R.id.ConstraintLayoutEditAbilities),context,abilityHolder);
                }
            });
        }
        else if(typeOfIdentifier.equals("unit"))
        {
            identifier = new UnitIdentifier(intent.getStringExtra( ""+R.string.UNIT_IDENTIFIER));
            Unit unit = matchup.GetUnit((UnitIdentifier) identifier);
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
        else if(typeOfIdentifier.equals("army"))
        {
            identifier = new ArmyIdentifier(intent.getStringExtra(""+R.string.ARMY_IDENTIFIER));
            Army armyToUse = matchup.GetArmy((ArmyIdentifier) identifier);

            for(Ability ability : armyToUse.abilities)
            {
                CreateButton(ability,armyToUse);
            }

            MainActivity mainActivity = new MainActivity();
            Activity_Edit_Abilities abilityHolder = this;
            addAbilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.SearchAbility(armyToUse,matchup,findViewById(R.id.ConstraintLayoutEditAbilities),context,abilityHolder);
                }
            });
        }
        else if (typeOfIdentifier.equals("weapon"))
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

    private void CreateButton(Ability ability, AbilityHolder abilityHolder)
    {
        TableRow tableRow = new TableRow(context);
        CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));
        checkBox.setTextSize(20);
        ImageButton imageButton = new ImageButton(context);

        checkBox.setChecked(ability.active);
        checkBox.setText(ability.name);
        checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        CompareActivity compareActivity = new CompareActivity();
        compareActivity.Setup(context,matchup);
        checkBox.setOnClickListener(compareActivity. new OnClickDeactivate(ability));
        imageButton.setImageResource(com.google.android.material.R.drawable.abc_ic_menu_cut_mtrl_alpha);

        imageButton.setOnClickListener(new OnClickRemoveAbility(abilityHolder,ability));

        tableRow.addView(checkBox);
        tableRow.addView(imageButton);

        tableLayoutAbilities.addView(tableRow,0);
    }

    private class OnClickRemoveAbility implements View.OnClickListener
    {
        Ability ability;

        AbilityHolder abilityHolder;

        public OnClickRemoveAbility(AbilityHolder abilityHolder,Ability ability)
        {
            this.abilityHolder = abilityHolder;
            this.ability = ability;



        }

        @Override
        public void onClick(View view) {

            boolean bool = abilityHolder.RemoveAbility(ability);

            Log.d("tog den bort ability", "" + bool);

            fileHandler.saveMatchup(matchup);

            TableLayout viewParent = (TableLayout)view.getParent().getParent();

            viewParent.removeView((TableRow)view.getParent());

            if(abilitiesAdded.contains(ability.name))
            {
                abilitiesAdded.remove(ability.name);
            }
            else
            {
                abilitiesRemoved.add(ability.name);
            }

        }
    }
    @Override
    public void AbilityAdded(Ability ability, AbilityHolder abilityHolder) {





        if(abilitiesRemoved.contains(ability.name))
        {
            abilitiesRemoved.remove(ability.name);
        }
        else
        {
            abilitiesAdded.add(ability.name);
        }



        CreateButton(ability,abilityHolder);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent data = new Intent();
        data.putExtra("abilitiesRemoved", abilitiesRemoved.size());
        for(int i = 0; i < abilitiesRemoved.size(); i++)
        {
            data.putExtra("abilitiesRemoved" +i, abilitiesRemoved.get(i));
        }
        data.putExtra("abilitiesAdded", abilitiesAdded.size());
        for(int i = 0; i < abilitiesAdded.size(); i++)
        {
            data.putExtra("abilitiesAdded" +i, abilitiesAdded.get(i));
        }

        data.putExtra(""+R.string.UI_IDENTIFIER, uiElement);

        data.putExtra(""+ R.string.IDENTIFIER, identifier.toString());
        if(identifier instanceof UnitIdentifier)
        {
            data.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "unit");
            data.putExtra(""+R.string.UNIT_IDENTIFIER, identifier.toString());

        }
        if(identifier instanceof ModelIdentifier)
        {
            data.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "model");
            data.putExtra(""+R.string.MODEL_IDENTIFIER, identifier.toString());

        }
        if(identifier instanceof ArmyIdentifier)
        {
            data.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "army");
            data.putExtra(""+R.string.ARMY_IDENTIFIER, identifier.toString());

        }
        setResult(RESULT_OK,data);
        finish();;
    }



}
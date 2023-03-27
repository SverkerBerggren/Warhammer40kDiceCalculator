package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;


import com.jjoe64.graphview.GraphView;

import java.util.HashMap;

public class CompareActivity extends AppCompatActivity {

    private  FileHandler  fileHandler;
    private Matchup matchup;

    private int UNIT_ALLEGIANCE = R.string.UNIT_ALLEGIANCE;
    private int UNIT_MODIFIER = R.string.UNIT_MODIFIER;
    private int UNIT_NUMBER = R.string.UNIT_NUMBER;

    static private String DECREASE_WEAPONSKILL = "decreaseWeaponSkill";
    private String DECREASE_BALLISTICSKILL = "decreaseBallisticSkill";
    private String DECREASE_STRENGTH = "decreaseStrength";
    private String DECREASE_TOUGHNESS = "decreaseToughness";
    private String DECREASE_ARMORSAVE = "decreaseArmorSave";
    private String DECREASE_INVUL = "decreaseInvul";
    private String DECREASE_ATTACKS = "decreaseAttacks";



    private String INCREASE_WEAPONSKILL = "increaseWeaponSkill";
    private String INCREASE_BALLISTICSKILL = "increaseBallisticSkill";
    private String INCREASE_STRENGTH = "increaseStrength";
    private String INCREASE_TOUGHNESS = "increaseToughness";
    private String INCREASE_ARMORSAVE = "increaseArmorSave";
    private String INCREASE_INVUL = "increaseInvul";
    private String INCREASE_ATTACKS = "increaseAttacks";




    private String FRIENDLY = "friendly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        LayoutInflater inflater =  getLayoutInflater();

        GraphView hej = null;
        fileHandler = new FileHandler(getBaseContext());

        //Log.d("testar intetion", "onCreate: " + getIntent().getStringExtra("SourceFile"));

        matchup = fileHandler.getMatchup( getIntent().getStringExtra("SourceFile"));


        createArmies(matchup,inflater);


    //    Log.d("vad är texten " + )



      //  View viewToModify = inflater.inflate(R.layout.unitviewprefab, findViewById(R.id.VerticalLayout));

        //View viewIgen =  viewToModify.findViewById(R.id.TestId);



        







    }

    private HashMap<String, DataSheet> datasheetMap = new HashMap<>();


    private void createArmies(Matchup matchup, LayoutInflater inflater)
    {
        ViewGroup verticalLayout = null;
        for(int i = 0; i < matchup.friendlyArmy.units.size();i++)
        {




            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayout)));


            instaniateUnitButton(verticalLayout.getChildAt(i +1),matchup.friendlyArmy.units.get(i),i, FRIENDLY);
            //Log.d("grejer",""+viewToModify.getParent().toString());



        }



        Log.d("längd", ""+matchup.friendlyArmy.units.size());


    }

    private  void instaniateUnitButton(View buttonToModify, Unit unit, int unitNumber, String friendlyOrEnemy)
    {
        Button topButton = (Button)buttonToModify.findViewById(R.id.UnitTopButton);



        topButton.setText(unit.unitName);

      //  topButton.setId(R.id.noId);




        ImageButton decreaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseWeaponSkill));
        ImageButton increaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseWeaponSkill));


        ImageButton decreaseBallisticSkill = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseBallisticSkill));
        ImageButton increaseBallisticSkill = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseBallisticSkill));

        ImageButton decreaseStrength = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseStrength));
        ImageButton increaseStrength = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseStrength));

        ImageButton decreaseToughness = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseToughness));
        ImageButton increaseToughness = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseToughness));

        ImageButton decreaseArmorSave = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseArmorSave));
        ImageButton increaseArmorSave = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseArmorSave));

        ImageButton decreaseAttacks = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseAttacks));
        ImageButton increaseAttacks = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseAttacks));

        ImageButton decreaseInvul = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseInvul));
        ImageButton increaseInvul = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseInvul));

       // TextView weaponSkillIndicator = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicator);

       // weaponSkillIndicator.setTag(UNIT_ALLEGIANCE,friendlyOrEnemy);
       // weaponSkillIndicator.setTag(UNIT_NUMBER,unitNumber);
       // TextView hej = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicatorr);

        int test = R.id.WeaponSkillInd;

        decreaseWeaponSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseWeaponSkill.setTag(UNIT_MODIFIER, DECREASE_WEAPONSKILL);
        decreaseWeaponSkill.setTag(UNIT_NUMBER, unitNumber);


        decreaseBallisticSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseBallisticSkill.setTag(UNIT_MODIFIER, DECREASE_BALLISTICSKILL);
        decreaseBallisticSkill.setTag(UNIT_NUMBER, unitNumber);


        decreaseAttacks.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseAttacks.setTag(UNIT_MODIFIER, DECREASE_ATTACKS);
        decreaseAttacks.setTag(UNIT_NUMBER, unitNumber);


        decreaseArmorSave.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseArmorSave.setTag(UNIT_MODIFIER, DECREASE_ARMORSAVE);
        decreaseArmorSave.setTag(UNIT_NUMBER, unitNumber);


        decreaseInvul.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseInvul.setTag(UNIT_MODIFIER, DECREASE_INVUL);
        decreaseInvul.setTag(UNIT_NUMBER, unitNumber);


        decreaseToughness.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseToughness.setTag(UNIT_MODIFIER, DECREASE_TOUGHNESS);
        decreaseToughness.setTag(UNIT_NUMBER, unitNumber);


        decreaseStrength.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseStrength.setTag(UNIT_MODIFIER, DECREASE_STRENGTH);
        decreaseStrength.setTag(UNIT_NUMBER, unitNumber);



        increaseWeaponSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseWeaponSkill.setTag(UNIT_MODIFIER, INCREASE_WEAPONSKILL);
        increaseWeaponSkill.setTag(UNIT_NUMBER, unitNumber);


        increaseBallisticSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseBallisticSkill.setTag(UNIT_MODIFIER, INCREASE_BALLISTICSKILL);
        increaseBallisticSkill.setTag(UNIT_NUMBER, unitNumber);


        increaseAttacks.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseAttacks.setTag(UNIT_MODIFIER, INCREASE_ATTACKS);
        increaseAttacks.setTag(UNIT_NUMBER, unitNumber);


        increaseArmorSave.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseArmorSave.setTag(UNIT_MODIFIER, INCREASE_ARMORSAVE);
        increaseArmorSave.setTag(UNIT_NUMBER, unitNumber);


        increaseInvul.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseInvul.setTag(UNIT_MODIFIER, INCREASE_INVUL);
        increaseInvul.setTag(UNIT_NUMBER, unitNumber);


        increaseToughness.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseToughness.setTag(UNIT_MODIFIER, INCREASE_TOUGHNESS);
        increaseToughness.setTag(UNIT_NUMBER, unitNumber);


        increaseStrength.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseStrength.setTag(UNIT_MODIFIER, INCREASE_STRENGTH);
        increaseStrength.setTag(UNIT_NUMBER, unitNumber);






    }

    public void ChangeUnitModifer(View buttonClicked)
    {
        Log.d("hej", "kallas metoden");

        Unit unitToModify = null;
        ViewGroup parent = (ViewGroup) buttonClicked.getParent();
        if(((String)buttonClicked.getTag(UNIT_ALLEGIANCE)).equals("friendly"))
        {
            unitToModify = matchup.friendlyArmy.units.get(((int)buttonClicked.getTag(UNIT_NUMBER)) );
        }
        else
        {
            unitToModify =  matchup.enemyArmy.units.get(((int)buttonClicked.getTag(UNIT_NUMBER)) );
        }

        String buttonTag = (String) buttonClicked.getTag(UNIT_MODIFIER);

        if(buttonTag.equals(DECREASE_WEAPONSKILL))
        {
            unitToModify.weaponSkillModifier-=1;
            ((TextView)parent.findViewById(R.id.WeaponSkillInd)).setText(Integer.toString(unitToModify.weaponSkillModifier));
        }
        if(buttonTag.equals(DECREASE_BALLISTICSKILL))
        {
            unitToModify.ballisticSkillModifier-=1;
            ((TextView)parent.findViewById(R.id.BallisticSkillIndicator)).setText(Integer.toString(unitToModify.ballisticSkillModifier));
        }
        if(buttonTag.equals(DECREASE_STRENGTH))
        {
            unitToModify.strengthModifier-=1;
            ((TextView)parent.findViewById(R.id.StrengthIndicator)).setText(Integer.toString(unitToModify.strengthModifier));
        }
        if(buttonTag.equals(DECREASE_TOUGHNESS))
        {
            unitToModify.toughnessModifier-=1;
            ((TextView)parent.findViewById(R.id.ToughnessIndicator)).setText(Integer.toString(unitToModify.toughnessModifier));
        }
        if(buttonTag.equals(DECREASE_ARMORSAVE))
        {
            unitToModify.armorSaveModifier-=1;
            ((TextView)parent.findViewById(R.id.ArmorSaveIndicator)).setText(Integer.toString(unitToModify.armorSaveModifier));
        }
        if(buttonTag.equals(DECREASE_INVUL))
        {
            unitToModify.invulnerableSaveModifier-=1;
            ((TextView)parent.findViewById(R.id.InvulIndicator)).setText(Integer.toString(unitToModify.invulnerableSaveModifier));
        }
        if(buttonTag.equals(DECREASE_ATTACKS))
        {
            unitToModify.attacksModifier-=1;
            ((TextView)parent.findViewById(R.id.AttacksIndicator)).setText(Integer.toString(unitToModify.attacksModifier));
        }





        if(buttonTag.equals(INCREASE_WEAPONSKILL))
        {
            unitToModify.weaponSkillModifier+=1;
            ((TextView)parent.findViewById(R.id.WeaponSkillInd)).setText(Integer.toString(unitToModify.weaponSkillModifier));
        }
        if(buttonTag.equals(INCREASE_BALLISTICSKILL))
        {
            unitToModify.ballisticSkillModifier+=1;
            ((TextView)parent.findViewById(R.id.BallisticSkillIndicator)).setText(Integer.toString(unitToModify.ballisticSkillModifier));
        }
        if(buttonTag.equals(INCREASE_STRENGTH))
        {
            unitToModify.strengthModifier+=1;
            ((TextView)parent.findViewById(R.id.StrengthIndicator)).setText(Integer.toString(unitToModify.strengthModifier));
        }
        if(buttonTag.equals(INCREASE_TOUGHNESS))
        {
            unitToModify.toughnessModifier+=1;
            ((TextView)parent.findViewById(R.id.ToughnessIndicator)).setText(Integer.toString(unitToModify.toughnessModifier));
        }
        if(buttonTag.equals(INCREASE_ARMORSAVE))
        {
            unitToModify.armorSaveModifier+=1;
            ((TextView)parent.findViewById(R.id.ArmorSaveIndicator)).setText(Integer.toString(unitToModify.armorSaveModifier));
        }
        if(buttonTag.equals(INCREASE_INVUL))
        {
            unitToModify.invulnerableSaveModifier+=1;
            ((TextView)parent.findViewById(R.id.InvulIndicator)).setText(Integer.toString(unitToModify.invulnerableSaveModifier));
        }
        if(buttonTag.equals(INCREASE_ATTACKS))
        {
            unitToModify.attacksModifier+=1;
            ((TextView)parent.findViewById(R.id.AttacksIndicator)).setText(Integer.toString(unitToModify.attacksModifier));
        }

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
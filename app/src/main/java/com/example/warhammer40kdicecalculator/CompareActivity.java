package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.jjoe64.graphview.GraphView;

import org.w3c.dom.Text;

import java.util.ArrayList;
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


    private String DONT_DROP_DOWN = "dontDropDown";

    private String FRIENDLY = "friendly";

    private ArrayList<TableLayout> previousLayouts = new ArrayList<>();

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

            CreateModel(verticalLayout.getChildAt(i +1),matchup.friendlyArmy.units.get(i),i,FRIENDLY,inflater);


        }



        //Log.d("längd", ""+matchup.friendlyArmy.units.size());


    }

    private void CreateModel(View buttonToModify, Unit unit, int unitNumber,String friendlyArmy , LayoutInflater inflater)
    {


        if(friendlyArmy.equals(FRIENDLY))
        {   LinearLayout modelsLayout = (LinearLayout) buttonToModify.findViewById(R.id.ModelsSubLayout);
            for(int i = 0; i < matchup.friendlyArmy.units.get(unitNumber).listOfModels.size();i++)
            {
                Log.d("models mangd", "hur manga models " +matchup.friendlyArmy.units.get(unitNumber).listOfModels.size() );


                Model currentModel = matchup.friendlyArmy.units.get(unitNumber).listOfModels.get(i);

                View inflatedView = inflater.inflate(R.layout.model_stats_prefab,modelsLayout);

                ((Button)inflatedView.findViewById(R.id.inividualModelTopButton)).setText(currentModel.name);

                ((Button)inflatedView.findViewById(R.id.inividualModelTopButton)).setId(R.id.noId);





               SetModelStats(inflatedView.findViewById(R.id.ModelStatsIndicator), currentModel);
                inflatedView.findViewById(R.id.ModelStatsIndicator).setId(R.id.noId);


                ConstraintLayout constraintLayout = ((ConstraintLayout)inflatedView.getParent()).findViewWithTag("ConstraintLayoutModel");

                SetModelAbilites(currentModel, constraintLayout);


                constraintLayout.setTag("");


            }

        }

       // for(int i = 0; i <  )
    }

    private void SetModelAbilites(Model model, ConstraintLayout constraintLayout)
    {
          TableLayout abilityTable =  constraintLayout.findViewById(R.id.AbilityLayout);

          Context context = getBaseContext();

          abilityTable.setBackgroundColor(Color.parseColor("#DFDADA"));
          for(int i = 0; i < model.listOfAbilites.size(); i++)
          {   TableRow tableRow = new TableRow(context);
              tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
              tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


              TextView abilityTextView = new TextView(context);
              abilityTextView.setText(model.listOfAbilites.get(i).name);
              abilityTextView.setTextSize(10);
              abilityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

              tableRow.addView(abilityTextView);
              abilityTable.addView(tableRow);
          }
          TableRow tableRow = new TableRow(context);
          tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));

          ImageButton addButton = new ImageButton(getBaseContext());
          //addButton.setImageResource(com.google.android.material.R.drawable.abc_ic_star_black_36dp);




    }

    private void SetModelStats(TableRow tableRow, Model model)
    {



        ((TextView)tableRow.findViewById(R.id.BallisticSkillStatIndicator)).setText("" + model.ballisticSkill + "+");
        ((TextView)tableRow.findViewById(R.id.WeaponSkillStatIndicator)).setText("" + model.weaponSkill + "+");
        ((TextView)tableRow.findViewById(R.id.StrengthStatIndicator)).setText("" + model.strength + "+");
        ((TextView)tableRow.findViewById(R.id.ToughnessStatIndicator)).setText("" + model.toughness + "+");
        ((TextView)tableRow.findViewById(R.id.AttacksStatIndicator)).setText("" + model.attacks + "+");
        ((TextView)tableRow.findViewById(R.id.SaveStatIndicator)).setText("" + model.armorSave + "+");
        ((TextView)tableRow.findViewById(R.id.WoundsStatIndicator)).setText("" + model.wounds + "+");
        ((TextView)tableRow.findViewById(R.id.InvulStatIndicator)).setText("" + model.invulnerableSave + "+");





        ConstraintLayout constraintLayout = (ConstraintLayout)tableRow.getParent().getParent();

        TableLayout weaponLayout = constraintLayout.findViewWithTag("WeaponLayout");
        for(int i = 0; i < model.listOfRangedWeapons.size();i++)
        {
            AddWeapon(weaponLayout,model.listOfRangedWeapons.get(i));
        }


        weaponLayout.setTag("NoTag");




    }

    private void AddWeapon(TableLayout tableLayout, RangedWeapon weapon)
    {
        TableRow tableRow = new TableRow(getBaseContext());

        TextView nameText = new TextView(getBaseContext());
        nameText.setText(weapon.name);
        nameText.setTextSize(10);
        nameText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView abilityText = new TextView(getBaseContext());
        abilityText.setText(""+weapon.weaponRules.size());
        abilityText.setTextSize(10);
        abilityText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView attacksText = new TextView(getBaseContext());
        attacksText.setText(weapon.amountOfAttacks.numberOfD6 +"D6 + " + weapon.amountOfAttacks.numberOfD3
                +"D3 +" +weapon.amountOfAttacks.rawNumberOfAttacks);
        attacksText.setTextSize(10);
        attacksText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView strengthText = new TextView(getBaseContext());
        strengthText.setText("" +weapon.strength);
        strengthText.setTextSize(10);
        strengthText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView apText = new TextView(getBaseContext());
        apText.setText(""+weapon.ap);
        apText.setTextSize(10);
        apText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        TextView damageText = new TextView(getBaseContext());
        damageText.setText(weapon.damageAmount.d6DamageAmount + "D6 +" + weapon.damageAmount.d3DamageAmount + "D3 +" + weapon.damageAmount.rawDamageAmount);
        damageText.setTextSize(10);
        damageText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        tableRow.addView(nameText);
        tableRow.addView(abilityText);
        tableRow.addView(attacksText);
        tableRow.addView(strengthText);
        tableRow.addView(apText);
        tableRow.addView(damageText);


        tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));

        tableLayout.addView(tableRow);

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



        ViewGroup viewGroup = (ViewGroup) constraintLayout;

        for(int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View childView = viewGroup.getChildAt(i);

            if(childView.getTag() != null && childView.getTag().equals(DONT_DROP_DOWN))
            {
                continue;
            }
            if(childView != v)
            {
                if(childView.getVisibility() == View.VISIBLE)
                {
                    childView.setVisibility(View.GONE);
                }
                else
                {
                    childView.setVisibility(View.VISIBLE);
                }
            }
        }

        Log.d("Ui grejer", "Vad är höjden efter" +  constraintLayout.getMeasuredHeight());



        Log.d("Ui grejer", "" +  v.getParent());
    }
}
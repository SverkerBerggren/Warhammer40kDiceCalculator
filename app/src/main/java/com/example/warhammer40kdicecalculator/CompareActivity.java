package com.example.warhammer40kdicecalculator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

import java.util.ArrayList;
import java.util.HashMap;

public class CompareActivity extends AppCompatActivity {

    private  FileHandler  fileHandler;
    private Matchup matchup;

    private int UNIT_ALLEGIANCE = R.string.UNIT_ALLEGIANCE;
    private int UNIT_MODIFIER = R.string.UNIT_MODIFIER;
    private int UNIT_NUMBER = R.string.UNIT_NUMBER;

    private String DECREASE_WEAPONSKILL = "decreaseWeaponSkill";
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
    private String ENEMY = "enemy";


    private final String ABILITY_LAYOUT_UNIT = "AbilityLayoutUnit";

    private ArrayList<TableLayout> previousLayouts = new ArrayList<>();

    private LayoutInflater inflater;

    private boolean inflatedModelStats = false;

    private EditText weaponSkillView;
    private EditText ballisticSkillView;
    private EditText strengthView;
    private EditText toughnessView;
    private EditText woundsView;
    private EditText attacksView;
    private EditText armorSaveView;
    private EditText InvSaveView;

    private ConstraintLayout highestConstraint;

    ActivityResultLauncher<Intent>  activityResultLauncher;

    private UIIdentifier firstUIidentifer;
    private View firstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        inflater =  getLayoutInflater();
        fileHandler = new FileHandler(getBaseContext());
        matchup = fileHandler.getMatchup( getIntent().getStringExtra("SourceFile"));
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUiActivityCallbackAbilities());

        highestConstraint = findViewById(R.id.ConstraintLayoutCompare);

        createArmies(matchup,inflater);
    }


    private class UpdateUiActivityCallbackAbilities implements ActivityResultCallback<ActivityResult>
    {

        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            String identiferString = data.getStringExtra(""+R.string.IDENTIFIER);

            Identifier gameIdentifier = new UnitIdentifier(identiferString);

            UIIdentifier uiId = new UIIdentifier(data.getStringExtra(""+ R.string.UI_IDENTIFIER),gameIdentifier);

            if(gameIdentifier instanceof UnitIdentifier)
            {
                TableLayout tableLayout = highestConstraint.findViewWithTag(uiId);
                tableLayout.getChildAt(0).setBackgroundColor( Color.parseColor("#AA00FF"));

                ArrayList<String> abilitiesToRemove = StringArrayFromIntent(data,"abilitiesRemoved");
                ArrayList<String> abilitiesToAdd = StringArrayFromIntent(data, "abilitiesAdded");

                ArrayList<View> viewsToRemove = new ArrayList<>();
                for(int i = 0; i < tableLayout.getChildCount(); i++)
                {
                    TableRow childView = (TableRow) tableLayout.getChildAt(i);

                    for(int z = 0; z < childView.getChildCount(); z++)
                    {
                        TextView text = (TextView) childView.getChildAt(z);

                        if(abilitiesToRemove.contains(text.getText().toString()))
                        {
                            viewsToRemove.add(childView);
                        }
                    }
                }
                for( View view : viewsToRemove)
                {
                    tableLayout.removeView(view);
                }

                for(int i = 0; i < abilitiesToAdd.size(); i++)
                {
                    tableLayout.addView(CreateTableRow(abilitiesToAdd.get(i)));
                }
            }






            Log.d("hej","it do be callbacking");
        }
    }

    private View CreateTableRow(String text)
    {
        Context context = getBaseContext();

        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
        tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        TextView abilityTextView = new TextView(context);
        abilityTextView.setText(text);
        abilityTextView.setTextSize(10);
        abilityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        tableRow.addView(abilityTextView);

        return tableRow;
    }

    private ArrayList<String> StringArrayFromIntent(Intent intent, String key)
    {
        ArrayList<String> listToReturn = new ArrayList<>();

        int size = intent.getIntExtra(key,-1);


        for(int i = 0; i < size; i++)
        {
            listToReturn.add(intent.getStringExtra(key + i));
        }

        return listToReturn;
    }


    private HashMap<String, DataSheet> datasheetMap = new HashMap<>();


    private void createArmies(Matchup matchup, LayoutInflater inflater)
    {
        ViewGroup verticalLayout = null;
        for(int i = 0; i < matchup.friendlyArmy.units.size();i++)
        {
            UnitIdentifier unitIdentifier = new UnitIdentifier("friendly",null,i,matchup.name);
            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayoutFriendlyArmy)));
            instaniateUnitButton(verticalLayout.getChildAt(i +1),matchup.friendlyArmy.units.get(i),unitIdentifier);
            CreateUnitAbilites(matchup.friendlyArmy.units.get(i),findViewById(R.id.VerticalLayoutFriendlyArmy),inflater, unitIdentifier);
            CreateModel(verticalLayout.getChildAt(i +1),matchup.friendlyArmy.units.get(i),i,FRIENDLY,inflater);
        }
        for(int i = 0; i < matchup.enemyArmy.units.size();i++)
        {
            UnitIdentifier unitIdentifier = new UnitIdentifier("enemy",null,i,matchup.name);
            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayoutEnemyArmy)));
            instaniateUnitButton(verticalLayout.getChildAt(i +1),matchup.enemyArmy.units.get(i),unitIdentifier);
            CreateUnitAbilites(matchup.enemyArmy.units.get(i),findViewById(R.id.VerticalLayoutEnemyArmy),inflater, unitIdentifier);
            CreateModel(verticalLayout.getChildAt(i +1),matchup.enemyArmy.units.get(i),i,ENEMY,inflater);
        }

    }

    private void CreateUnitAbilites(Unit unit, LinearLayout linearLayout, LayoutInflater inflater, UnitIdentifier unitIdentifier)
    {
        TableLayout unitAbilitLayout = linearLayout.findViewWithTag("AbilityLayoutUnit");
        Context context = getBaseContext();

        UIIdentifier uiId = new UIIdentifier(ABILITY_LAYOUT_UNIT, unitIdentifier);


        if(firstUIidentifer == null)
        {
            firstUIidentifer = uiId;
            firstView = unitAbilitLayout;
        }


        unitAbilitLayout.setTag(uiId);


        //unitAbilitLayout.setTag

        for(int i = 0; i < unit.listOfAbilitys.size(); i++)
        {
            TableRow tableRow = new TableRow(context);
            tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
            tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            TextView abilityTextView = new TextView(context);
            abilityTextView.setText(unit.listOfAbilitys.get(i).name);
            abilityTextView.setTextSize(10);
            abilityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            tableRow.addView(abilityTextView);
            unitAbilitLayout.addView(tableRow);
        }

        ImageButton editAbilities = findViewById(R.id.EditUnitAbilities);

        editAbilities.setTag(R.string.UNIT_IDENTIFIER,unitIdentifier);

        editAbilities.setOnClickListener(new OnClickListenerEditAbilites(unit, uiId));

        editAbilities.setId(R.id.noId);

    }


    private class OnClickListenerEditAbilites implements View.OnClickListener
    {
        private Unit unit;
        private Model model;
        private Army army;
        private UIIdentifier uiId;


        public OnClickListenerEditAbilites(Army army)
        {
             this.army = army;
        }
        public OnClickListenerEditAbilites(Model model)
        {
            this.model = model;
        }
        public OnClickListenerEditAbilites(Unit unit, UIIdentifier uiId)
        {
            this.unit = unit;
            this.uiId = uiId;
        }


        @Override
        public void onClick(View view) {

            if(unit != null)
            {
                StartEditAbilites(view, unit,uiId);
            }
        }
    }

    public void StartEditAbilites(View view,Army army )
    {

    }
    public void StartEditAbilites(View view,Model model )
    {

    }
    public void StartEditAbilites(View view, Unit unit, UIIdentifier uiId )
    {
        Intent intent = new Intent(this, Activity_Edit_Abilities.class);
       // Identifier
        UnitIdentifier identifier = (UnitIdentifier)view.getTag(R.string.UNIT_IDENTIFIER);


       intent.putExtra(""+R.string.UNIT_ALLEGIANCE,identifier.allegiance);
       intent.putExtra(""+R.string.UNIT_NUMBER,identifier.index);
       intent.putExtra("matchupName",identifier.matchupName);
       intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);



       String unitString = identifier.toString();

       UnitIdentifier nyUnitId = new UnitIdentifier(unitString);

       Log.d("Unit string  " , unitString);
        Log.d("Unit string andra " , nyUnitId.toString());


        Log.d("ar de lika??  " , "" + nyUnitId.equals(identifier));


        activityResultLauncher.launch(intent);

       //startActivity(intent);

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


                ModelIdentifier modelId = new ModelIdentifier(FRIENDLY, null, unitNumber,i,matchup.name );

                ((ImageButton)inflatedView.findViewById(R.id.EditWeaponsButton)).setTag(R.string.MODEL_IDENTIFIER,modelId);
                ((ImageButton)inflatedView.findViewById(R.id.EditWeaponsButton)).setId(R.id.noId);

                ((ImageButton)inflatedView.findViewById(R.id.EditModelStatsButton)).setOnClickListener(new OnClickListenerModelStats(currentModel));

                ((ImageButton)inflatedView.findViewById(R.id.EditModelStatsButton)).setId(R.id.noId);

                SetModelStats(inflatedView.findViewById(R.id.ModelStatsIndicator), currentModel);
                inflatedView.findViewById(R.id.ModelStatsIndicator).setId(R.id.noId);


                ConstraintLayout constraintLayout = ((ConstraintLayout)inflatedView.getParent()).findViewWithTag("ConstraintLayoutModel");

                SetModelAbilites(currentModel, constraintLayout);


                constraintLayout.setTag("");


            }

        }

       // for(int i = 0; i <  )
    }

    public void EditWeaponActivityStart(View v)
    {
        Intent intent = new Intent(this,EditWeaponActivity.class);

        ModelIdentifier modelId = (ModelIdentifier) v.getTag(R.string.MODEL_IDENTIFIER);

        intent.putExtra(""+UNIT_ALLEGIANCE, modelId.allegiance);
        intent.putExtra("indexUnit", modelId.indexUnit);
        intent.putExtra("indexModel", modelId.indexModel);
        intent.putExtra("matchupName", modelId.matchupName);

        startActivity(intent);
    }

    private void SetModelAbilites(Model model, ConstraintLayout constraintLayout)
    {
          TableLayout abilityTable =  constraintLayout.findViewById(R.id.AbilityLayoutUnit);

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
          TableRow tableRowButton = new TableRow(context);
          tableRowButton.setBackgroundColor(Color.parseColor("#DFDADA"));

          ImageButton addButton = new ImageButton(getBaseContext());
          addButton.setImageResource(com.google.android.material.R.drawable.abc_ab_share_pack_mtrl_alpha);

          tableRowButton.addView(addButton);


        abilityTable.addView(tableRowButton);



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

    private class EditUnitButtonOnClick implements View.OnClickListener
    {
        private Unit unitToEdit;

        public EditUnitButtonOnClick(Unit unit)
        {
            unitToEdit = unit;
        }

        @Override
        public void onClick(View view) {
            inflater.inflate(R.layout.activity_popup, highestConstraint);
        }
    }

    private  void instaniateUnitButton(View buttonToModify, Unit unit, UnitIdentifier unitId)
    {
        Button topButton = (Button)buttonToModify.findViewById(R.id.UnitTopButton);



        topButton.setText(unit.unitName);

      //  topButton.setId(R.id.noId);
        ImageButton button = findViewById(R.id.EditUnitModifierButton);
        button.setOnClickListener(new EditUnitButtonOnClick(unit));




      //ImageButton decreaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseWeaponSkill));
      //ImageButton increaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseWeaponSkill));


      //ImageButton decreaseBallisticSkill = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseBallisticSkill));
      //ImageButton increaseBallisticSkill = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseBallisticSkill));

      //ImageButton decreaseStrength = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseStrength));
      //ImageButton increaseStrength = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseStrength));

      //ImageButton decreaseToughness = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseToughness));
      //ImageButton increaseToughness = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseToughness));

      //ImageButton decreaseArmorSave = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseArmorSave));
      //ImageButton increaseArmorSave = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseArmorSave));

      //ImageButton decreaseAttacks = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseAttacks));
      //ImageButton increaseAttacks = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseAttacks));

      //ImageButton decreaseInvul = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseInvul));
      //ImageButton increaseInvul = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseInvul));

       // TextView weaponSkillIndicator = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicator);

       // weaponSkillIndicator.setTag(UNIT_ALLEGIANCE,friendlyOrEnemy);
       // weaponSkillIndicator.setTag(UNIT_NUMBER,unitNumber);
       // TextView hej = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicatorr);

     // int test = R.id.WeaponSkillInd;

     // decreaseWeaponSkill.setTag(UNIT_ALLEGIANCE, unitId.allegiance);
     // decreaseWeaponSkill.setTag(UNIT_MODIFIER, DECREASE_WEAPONSKILL);
     // decreaseWeaponSkill.setTag(UNIT_NUMBER, unitId.index);


     // decreaseBallisticSkill.setTag(UNIT_ALLEGIANCE, unitId.allegiance);
     // decreaseBallisticSkill.setTag(UNIT_MODIFIER, DECREASE_BALLISTICSKILL);
     // decreaseBallisticSkill.setTag(UNIT_NUMBER, unitId.index);


   //    decreaseAttacks.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    decreaseAttacks.setTag(UNIT_MODIFIER, DECREASE_ATTACKS);
   //    decreaseAttacks.setTag(UNIT_NUMBER, unitNumber);


   //    decreaseArmorSave.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    decreaseArmorSave.setTag(UNIT_MODIFIER, DECREASE_ARMORSAVE);
   //    decreaseArmorSave.setTag(UNIT_NUMBER, unitNumber);


   //    decreaseInvul.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    decreaseInvul.setTag(UNIT_MODIFIER, DECREASE_INVUL);
   //    decreaseInvul.setTag(UNIT_NUMBER, unitNumber);


   //    decreaseToughness.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    decreaseToughness.setTag(UNIT_MODIFIER, DECREASE_TOUGHNESS);
   //    decreaseToughness.setTag(UNIT_NUMBER, unitNumber);


   //    decreaseStrength.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    decreaseStrength.setTag(UNIT_MODIFIER, DECREASE_STRENGTH);
   //    decreaseStrength.setTag(UNIT_NUMBER, unitNumber);



   //    increaseWeaponSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseWeaponSkill.setTag(UNIT_MODIFIER, INCREASE_WEAPONSKILL);
   //    increaseWeaponSkill.setTag(UNIT_NUMBER, unitNumber);


   //    increaseBallisticSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseBallisticSkill.setTag(UNIT_MODIFIER, INCREASE_BALLISTICSKILL);
   //    increaseBallisticSkill.setTag(UNIT_NUMBER, unitNumber);


   //    increaseAttacks.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseAttacks.setTag(UNIT_MODIFIER, INCREASE_ATTACKS);
   //    increaseAttacks.setTag(UNIT_NUMBER, unitNumber);


   //    increaseArmorSave.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseArmorSave.setTag(UNIT_MODIFIER, INCREASE_ARMORSAVE);
   //    increaseArmorSave.setTag(UNIT_NUMBER, unitNumber);


   //    increaseInvul.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseInvul.setTag(UNIT_MODIFIER, INCREASE_INVUL);
   //    increaseInvul.setTag(UNIT_NUMBER, unitNumber);


   //    increaseToughness.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseToughness.setTag(UNIT_MODIFIER, INCREASE_TOUGHNESS);
   //    increaseToughness.setTag(UNIT_NUMBER, unitNumber);


   //    increaseStrength.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
   //    increaseStrength.setTag(UNIT_MODIFIER, INCREASE_STRENGTH);
   //    increaseStrength.setTag(UNIT_NUMBER, unitNumber);






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

     //  if(buttonTag.equals(DECREASE_WEAPONSKILL))
     //  {
     //      unitToModify.weaponSkillModifier-=1;
     //      ((TextView)parent.findViewById(R.id.WeaponSkillInd)).setText(Integer.toString(unitToModify.weaponSkillModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_BALLISTICSKILL))
     //  {
     //      unitToModify.ballisticSkillModifier-=1;
     //      ((TextView)parent.findViewById(R.id.BallisticSkillIndicator)).setText(Integer.toString(unitToModify.ballisticSkillModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_STRENGTH))
     //  {
     //      unitToModify.strengthModifier-=1;
     //      ((TextView)parent.findViewById(R.id.StrengthIndicator)).setText(Integer.toString(unitToModify.strengthModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_TOUGHNESS))
     //  {
     //      unitToModify.toughnessModifier-=1;
     //      ((TextView)parent.findViewById(R.id.ToughnessIndicator)).setText(Integer.toString(unitToModify.toughnessModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_ARMORSAVE))
     //  {
     //      unitToModify.armorSaveModifier-=1;
     //      ((TextView)parent.findViewById(R.id.ArmorSaveIndicator)).setText(Integer.toString(unitToModify.armorSaveModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_INVUL))
     //  {
     //      unitToModify.invulnerableSaveModifier-=1;
     //      ((TextView)parent.findViewById(R.id.InvulIndicator)).setText(Integer.toString(unitToModify.invulnerableSaveModifier));
     //  }
     //  if(buttonTag.equals(DECREASE_ATTACKS))
     //  {
     //      unitToModify.attacksModifier-=1;
     //      ((TextView)parent.findViewById(R.id.AttacksIndicator)).setText(Integer.toString(unitToModify.attacksModifier));
     //  }





     //  if(buttonTag.equals(INCREASE_WEAPONSKILL))
     //  {
     //      unitToModify.weaponSkillModifier+=1;
     //      ((TextView)parent.findViewById(R.id.WeaponSkillInd)).setText(Integer.toString(unitToModify.weaponSkillModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_BALLISTICSKILL))
     //  {
     //      unitToModify.ballisticSkillModifier+=1;
     //      ((TextView)parent.findViewById(R.id.BallisticSkillIndicator)).setText(Integer.toString(unitToModify.ballisticSkillModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_STRENGTH))
     //  {
     //      unitToModify.strengthModifier+=1;
     //      ((TextView)parent.findViewById(R.id.StrengthIndicator)).setText(Integer.toString(unitToModify.strengthModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_TOUGHNESS))
     //  {
     //      unitToModify.toughnessModifier+=1;
     //      ((TextView)parent.findViewById(R.id.ToughnessIndicator)).setText(Integer.toString(unitToModify.toughnessModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_ARMORSAVE))
     //  {
     //      unitToModify.armorSaveModifier+=1;
     //      ((TextView)parent.findViewById(R.id.ArmorSaveIndicator)).setText(Integer.toString(unitToModify.armorSaveModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_INVUL))
     //  {
     //      unitToModify.invulnerableSaveModifier+=1;
     //      ((TextView)parent.findViewById(R.id.InvulIndicator)).setText(Integer.toString(unitToModify.invulnerableSaveModifier));
     //  }
     //  if(buttonTag.equals(INCREASE_ATTACKS))
     //  {
     //      unitToModify.attacksModifier+=1;
     //      ((TextView)parent.findViewById(R.id.AttacksIndicator)).setText(Integer.toString(unitToModify.attacksModifier));
     //  }

    }



    public void DropDownClick(View v)
    {
        Context context = v.getContext();


        ViewGroup constraintLayout = (ViewGroup) v.getParent();



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

    }

    public void OpenUnitSelection(View v)
    {
        Intent intenten = new Intent(this, UnitSelection.class);

        intenten.putExtra("SourceFile",matchup.name);

        startActivity(intenten);
    }


    private  class OnClickListenerModelStats implements View.OnClickListener
    {
        private  Model model;

        public OnClickListenerModelStats(Model model)
        {
            this.model = model;
        }

        @Override
        public void onClick(View view) {
            if(!inflatedModelStats)
            {
                inflater.inflate(R.layout.activity_popup,highestConstraint);
                ((Button)findViewById(R.id.SaveModelPopup)).setOnClickListener(new OnClickListenerModelSave(model));

                inflatedModelStats = true;
            }
            else
            {
                ShowPopup(view);
            }

            weaponSkillView = (EditText)findViewById(R.id.WeaponSkillModelPopup) ;
            ballisticSkillView   = (EditText)findViewById(R.id.BallisticSkillModelPopup) ;
            strengthView  = (EditText)findViewById(R.id.StrengthModelPopup) ;
            toughnessView = (EditText)findViewById(R.id.ToughnessModelPopup) ;
            woundsView  = (EditText)findViewById(R.id.WoundsModelPopup) ;
            attacksView  = (EditText)findViewById(R.id.AttacksModelPopup) ;
            armorSaveView  = (EditText)findViewById(R.id.ArmorSaveModelPopup) ;
            InvSaveView  = (EditText)findViewById(R.id.InvSaveModelPopup) ;

            weaponSkillView.setText(""+ model.weaponSkill);
            ballisticSkillView.setText(""+ model.ballisticSkill);
            strengthView.setText(""+ model.strength);
            toughnessView.setText(""+ model.toughness);
            woundsView.setText(""+ model.wounds);
            attacksView.setText(""+ model.attacks);
            armorSaveView.setText(""+ model.armorSave);
            InvSaveView.setText(""+ model.invulnerableSave);



        }
    }

    private class  OnClickListenerModelSave implements View.OnClickListener
    {
        private  Model model;

        public OnClickListenerModelSave(Model model)
        {
            this.model = model;
        }


        @Override
        public void onClick(View view) {

            model.weaponSkill = Integer.parseInt(weaponSkillView.getText().toString());
            model.ballisticSkill = Integer.parseInt(ballisticSkillView.getText().toString());
            model.strength = Integer.parseInt(strengthView.getText().toString());
            model.toughness = Integer.parseInt(toughnessView.getText().toString());
            model.wounds = Integer.parseInt(woundsView.getText().toString());
            model.attacks = Integer.parseInt(attacksView.getText().toString());
            model.armorSave = Integer.parseInt(armorSaveView.getText().toString());
            model.invulnerableSave = Integer.parseInt(InvSaveView.getText().toString());


            fileHandler.saveMatchup(matchup);

        }
    }


    public void ShowPopup(View v)
    {
        View popup = findViewById(R.id.ConstraintLayoutModelPopup);
        popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v)
    {
        View popup = findViewById(R.id.ConstraintLayoutModelPopup);
        popup.setVisibility(View.GONE);
    }

}
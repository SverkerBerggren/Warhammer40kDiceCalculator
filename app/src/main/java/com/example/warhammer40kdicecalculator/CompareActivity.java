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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.DeactivatableInterface;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AttackAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

import java.util.ArrayList;
import java.util.HashMap;

public class CompareActivity extends AppCompatActivity implements AbilityUIHolder{

    private  FileHandler  fileHandler;
    public Matchup matchup;

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


    public final String ABILITY_LAYOUT_UNIT = "AbilityLayoutUnit";
    public final String UI_WEAPON_LAYOUT_MODEL = "WeaponLayoutModel";
    public final String UI_UNIT_MODIFIER_LAYOUT = "UnitModifierLayout";
    public final String UI_MODEL_MODIFIER_LAYOUT = "ModelModifierLayout";
    public final String UI_ARMY_MODIFIER_LAYOUT = "ArmyModifierLayout";
    public final String UI_ABILITY_LAYOUT_MODEL = "AbilityLayoutModel";
    public final String UI_ARMY_ABILITY_LAYOUT = "ArmyAbilityLayout";
    public final String UI_WEAPON_LAYOUT = "UiWeaponLayout";



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

    private ImageButton weaponSkillIncrease;
    private ImageButton weaponSkillDecrease;
    private ImageButton ballisticSkillIncrease;
    private ImageButton ballisticSkillDecrease;
    private ImageButton strengthIncrease;
    private ImageButton strengthDecrease;
    private ImageButton toughnessIncrease;
    private ImageButton toughnessDecrease;
    private ImageButton woundsIncrease;
    private ImageButton woundsDecrease;
    private ImageButton attacksIncrease;
    private ImageButton attacksDecrease;
    private ImageButton armorSavesIncrease;
    private ImageButton armorSavesDecrease;
    private ImageButton invulnerableSavesIncrease;
    private ImageButton invulnerableSavesDecrease;

    private boolean popupActive = false;


    public ConstraintLayout highestConstraint;

    ActivityResultLauncher<Intent> activityResultLauncherAbility;
    ActivityResultLauncher<Intent> activityResultLauncherWeapon;



    private Context context;
    private boolean shouldStartActivityFromEditUnitContext = false;
    private EditUnitActivity editUnitActivity;


    public void SetShouldStartActivityFromEditUnitContext(boolean bool)
    {
        shouldStartActivityFromEditUnitContext = bool;
    }

    public void SetEditUnitActivity(EditUnitActivity appCompat)
    {
        editUnitActivity = appCompat;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        inflater =  getLayoutInflater();
        context = getBaseContext();

        fileHandler = new FileHandler(context);
        matchup = fileHandler.getMatchup( getIntent().getStringExtra("SourceFile"));
        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUiActivityCallbackAbilities());

        activityResultLauncherWeapon = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUIWeaponCallback());



        highestConstraint = findViewById(R.id.ConstraintLayoutCompare);

        createArmies(matchup,inflater);
    }

    public void Setup(Context context, Matchup matchup)
    {
        this.context =context ;
        fileHandler = new FileHandler(context);
        this.matchup = matchup;

        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUiActivityCallbackAbilities());

        activityResultLauncherWeapon = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUIWeaponCallback());

    }

    public void SetInflater(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void SetContext(Context context)
    {
        this.context = context;
    }

    @Override
    public void AbilityAdded(Ability ability, AbilityHolder abilityHolder) {

        if(abilityHolder instanceof Model)
        {
           // ModelIdentifier mode
        }

    }


    private class UpdateUiActivityCallbackAbilities implements ActivityResultCallback<ActivityResult>
    {
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            String identifierString = data.getStringExtra(""+R.string.TYPE_OF_IDENTIFIER);

            Identifier abilityHolderIdentifier = null;


            if(identifierString.equals("model"))
            {
                abilityHolderIdentifier = new ModelIdentifier(data.getStringExtra(""+R.string.MODEL_IDENTIFIER));
            }

            if(identifierString.equals("unit"))
            {
                abilityHolderIdentifier = new UnitIdentifier(data.getStringExtra(""+R.string.UNIT_IDENTIFIER));
            }


            if(identifierString.equals("army"))
            {
               abilityHolderIdentifier = new ArmyIdentifier(data.getStringExtra(""+R.string.ARMY_IDENTIFIER));
            }




            ArrayList<String> abilitiesToRemove = StringArrayFromIntent(data,"abilitiesRemoved");
            ArrayList<String> abilitiesToAdd = StringArrayFromIntent(data, "abilitiesAdded");

            UIIdentifier uiId = new UIIdentifier(data.getStringExtra(""+ R.string.UI_IDENTIFIER),abilityHolderIdentifier);


            UpdateAbilityRow(abilitiesToAdd,abilitiesToRemove,uiId);

            Log.d("hej","it do be callbacking");
        }
    }

    public class UpdateUIWeaponCallback implements ActivityResultCallback<ActivityResult>
    {
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            Intent data = result.getData();


            //boolean applyToAllModels = data.getBooleanExtra(""+ R.string.APPLY_TO_ALL, false);

            ModelIdentifier modelId =  new ModelIdentifier(data.getStringExtra(""+R.string.MODEL_IDENTIFIER));


            ReloadMatchup();

            Model model = matchup.GetModel(modelId);

            UIIdentifier uiId = new UIIdentifier(data.getStringExtra(""+R.string.UI_IDENTIFIER),modelId);

            TableLayout tableLayout = highestConstraint.findViewWithTag(uiId);

            tableLayout.removeViews(1,tableLayout.getChildCount() -1 );

            for(int i = 0; i < model.listOfRangedWeapons.size();i++)
            {
                AddWeapon(tableLayout,model.listOfRangedWeapons.get(i));
            }


        }
    }

    private void ReloadMatchup()
    {
        matchup = fileHandler.getMatchup(matchup.name);
    }

    private void UpdateAbilityRow(ArrayList<String> abilitiesToAdd, ArrayList<String> abilitiesToRemove, UIIdentifier uiId)
    {
        TableLayout tableLayout = highestConstraint.findViewWithTag(uiId);
        tableLayout.getChildAt(0).setBackgroundColor( Color.parseColor("#AA00FF"));
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

    public View CreateTableRow(String text)
    {


        TableRow tableRow = new TableRow(context);
        tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
        tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        TextView abilityTextView = new TextView(new ContextThemeWrapper(context, com.google.android.material.R.style.Widget_AppCompat_TextView));
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

        ArmyIdentifier armyFriendlyId = new ArmyIdentifier(FRIENDLY, matchup.name);
        UIIdentifier uiIdArmyFriendly = new UIIdentifier(UI_ARMY_MODIFIER_LAYOUT, armyFriendlyId);
        TableRow friendlyTableRow = (TableRow)findViewById(R.id.TableRowFriendlyArmy);
        ImageButton friendlyEditButton = (ImageButton)findViewById(R.id.EditFriendlyArmyButton);
        CreateModifiers(matchup.friendlyArmy, uiIdArmyFriendly, friendlyTableRow, friendlyEditButton);

        ArmyIdentifier armyEnemyId = new ArmyIdentifier(ENEMY, matchup.name);
        UIIdentifier uiIdArmyEnemy = new UIIdentifier(UI_ARMY_MODIFIER_LAYOUT, armyEnemyId);
        TableRow enemyTableRow = (TableRow)findViewById(R.id.TableRowEnemyArmy);
        ImageButton enemyEditButton = (ImageButton)findViewById(R.id.EditEnemyArmyButton);
        CreateModifiers(matchup.enemyArmy, uiIdArmyEnemy,enemyTableRow,enemyEditButton);

        CreateArmyAbilities(armyFriendlyId, matchup.friendlyArmy);
        CreateArmyAbilities(armyEnemyId,matchup.enemyArmy);


        instaniateArmies(FRIENDLY);
        instaniateArmies(ENEMY);

    }

    private void instaniateArmies(String allegiance)
    {
        ArrayList<Unit> units;
        ViewGroup verticalLayout;

        if(allegiance.equals(FRIENDLY))
        {
            units = matchup.friendlyArmy.units;
            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayoutFriendlyArmy)));
        }
        else
        {
            units = matchup.enemyArmy.units;
            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayoutEnemyArmy)));
        }
        for(int i = 0; i < units.size();i++)
        {
            Unit unit = units.get(i);
            UnitIdentifier unitIdentifier = new UnitIdentifier(allegiance,null,i,matchup.name);
            instaniateUnitButton(verticalLayout.getChildAt(verticalLayout.getChildCount()-1),unit,unitIdentifier);
            CreateUnitAbilites(unit,(LinearLayout) verticalLayout,inflater, unitIdentifier);

            TableRow tableRow = (TableRow)findViewById(R.id.TableRowUnitModifiers);
            ImageButton button = (ImageButton)findViewById(R.id.EditUnitModifierButton);
            UIIdentifier uiId = new UIIdentifier(UI_UNIT_MODIFIER_LAYOUT, unitIdentifier);
            CreateModifiers(unit, uiId, tableRow, button);
            CreateModel(verticalLayout.getChildAt(verticalLayout.getChildCount()-1),unit,i,FRIENDLY,inflater);
        }
    }

    private void CreateArmyAbilities(ArmyIdentifier armyIdentifier, Army army)
    {
        if(armyIdentifier.allegiance.equals("friendly"))
        {

            TableLayout tableLayout = findViewById(R.id.AbilityLayoutFriendlyArmy);
            for(int i = 0; i < army.abilities.size(); i++)
            {
                tableLayout.addView(CreateTableRow(army.abilities.get(i).name));
            }

            ImageButton editButton = findViewById(R.id.EditFriendlyArmyAbilities);


            UIIdentifier uiId = new UIIdentifier(UI_ARMY_ABILITY_LAYOUT, armyIdentifier);
            editButton.setOnClickListener(new OnClickListenerEditAbilites(army,uiId));


            editButton.setTag(R.string.ARMY_IDENTIFIER,armyIdentifier);


            tableLayout.setTag(uiId);
          //  editButton.setTag(uiId);

        }
        else
        {
            TableLayout tableLayout = findViewById(R.id.AbilityLayoutEnemyArmy);
            for(int i = 0; i < army.abilities.size(); i++)
            {
                tableLayout.addView(CreateTableRow(army.abilities.get(i).name));
            }

            ImageButton editButton = findViewById(R.id.EditEnemyArmyAbilities);


            UIIdentifier uiId = new UIIdentifier(UI_ARMY_ABILITY_LAYOUT, armyIdentifier);
            editButton.setOnClickListener(new OnClickListenerEditAbilites(army,uiId));

            editButton.setTag(R.string.ARMY_IDENTIFIER,armyIdentifier);

            tableLayout.setTag(uiId);


            //editButton.setTag(uiId);
        }
    }

    public void CreateModifiers(ModifierHolder modifierHolder, UIIdentifier uiId, TableRow tableRow, ImageButton button)
    {
        tableRow.setTag(uiId);
        tableRow.setId(R.id.noId);
        SetModifiers(uiId, modifierHolder);

        button.setOnClickListener(new OnClickListenerModelAndUnitStats(modifierHolder, uiId));
        button.setId(R.id.noId);
    }
    private void SetModifiers(UIIdentifier uiId, ModifierHolder modifierHolder)
    {
        TableRow tableRow = (TableRow)highestConstraint.findViewWithTag(uiId);

        ((TextView) (tableRow.getChildAt(0))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.Strength));
        ((TextView) (tableRow.getChildAt(1))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.Toughness));
        ((TextView) (tableRow.getChildAt(2))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.Wounds));
        ((TextView) (tableRow.getChildAt(3))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.Attacks));
        ((TextView) (tableRow.getChildAt(4))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.ArmorSaves));
        ((TextView) (tableRow.getChildAt(5))).setText("" + modifierHolder.GetModifierValue(UnitAndModelSkill.InvulnerableSaves));
    }


    public void CreateUnitAbilites(Unit unit, LinearLayout linearLayout, LayoutInflater inflater, UnitIdentifier unitIdentifier)
    {
        TableLayout unitAbilitLayout = linearLayout.findViewWithTag("AbilityLayoutUnit");


        UIIdentifier uiId = new UIIdentifier(ABILITY_LAYOUT_UNIT, unitIdentifier);

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

        ImageButton editAbilities = highestConstraint.findViewById(R.id.EditUnitAbilities);

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


        public OnClickListenerEditAbilites(Army army,UIIdentifier uiId)
        {
             this.army = army;
             this.uiId = uiId;
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

            if(army != null)
            {
                StartEditAbilites(view,army,uiId);
            }
        }
    }
    public void StartEditAbilites(View view, Unit unit, UIIdentifier uiId )
    {
        Intent intent = new Intent(context, Activity_Edit_Abilities.class);


       // Identifier
        UnitIdentifier identifier = (UnitIdentifier)view.getTag(R.string.UNIT_IDENTIFIER);

        intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER,"unit");

        intent.putExtra(""+R.string.UNIT_IDENTIFIER,identifier.toString());

        intent.putExtra("matchupName",identifier.matchupName);
        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

        if(shouldStartActivityFromEditUnitContext)
        {


            editUnitActivity.StartEditAbilites(view, uiId);
        }
        else
        {
            activityResultLauncherAbility.launch(intent);
        }
    }
    public void StartEditAbilites(View view, Model model, UIIdentifier uiId )
    {
        Intent intent = new Intent(context, Activity_Edit_Abilities.class);
        // Identifier
        ModelIdentifier identifier = (ModelIdentifier)view.getTag(R.string.MODEL_IDENTIFIER);

        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

        intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "model");
        intent.putExtra("" + R.string.MODEL_IDENTIFIER, identifier.toString());
        intent.putExtra("matchupName", matchup.name);

        if(shouldStartActivityFromEditUnitContext)
        {


            editUnitActivity.StartEditAbilites(view, uiId);
        }
        else
        {
            activityResultLauncherAbility.launch(intent);
        }

    }


    public void StartEditAbilites(View view, Army army, UIIdentifier uiId )
    {
        Intent intent = new Intent(this, Activity_Edit_Abilities.class);
        // Identifier
        ArmyIdentifier identifier = (ArmyIdentifier)view.getTag(R.string.ARMY_IDENTIFIER);

        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

        intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "army");
        intent.putExtra("" + R.string.ARMY_IDENTIFIER, identifier.toString());
        intent.putExtra("matchupName", matchup.name);





        activityResultLauncherAbility.launch(intent);

    }



    public void CreateModel(View buttonToModify, Unit unit, int unitNumber,String friendlyArmy , LayoutInflater inflater)
    {
        LinearLayout modelsLayout = (LinearLayout) buttonToModify.findViewById(R.id.ModelsSubLayout);
        for(int i = 0; i < unit.listOfModels.size();i++)
        {
            Model currentModel = unit.listOfModels.get(i);
            View inflatedView = inflater.inflate(R.layout.model_stats_prefab,modelsLayout);
            ((Button)highestConstraint.findViewWithTag("inividualModelTopButton")).setText(currentModel.name);
            ((Button)highestConstraint.findViewWithTag("inividualModelTopButton")).setTag("");

            ModelIdentifier modelId = new ModelIdentifier(friendlyArmy, null, unitNumber,i,matchup.name );
            ((ImageButton)inflatedView.findViewById(R.id.EditWeaponsButton)).setTag(R.string.MODEL_IDENTIFIER,modelId);
            ((ImageButton)inflatedView.findViewById(R.id.EditWeaponsButton)).setTag(R.string.UI_IDENTIFIER,new UIIdentifier(UI_WEAPON_LAYOUT_MODEL,modelId));
            ((ImageButton)inflatedView.findViewById(R.id.EditWeaponsButton)).setId(R.id.noId);

            // Add Weapon Button
            highestConstraint.findViewWithTag("AddWeaponModelButton").setOnClickListener(new OnClickAddWeapon(modelId));
            highestConstraint.findViewWithTag("AddWeaponModelButton").setTag("");
            ((CheckBox)highestConstraint.findViewById(R.id.DeactivateModelsButton)).setChecked(currentModel.active);
            highestConstraint.findViewById(R.id.DeactivateModelsButton).setOnClickListener(new OnClickDeactivate(currentModel));
            highestConstraint.findViewById(R.id.DeactivateModelsButton).setId(R.id.noId);
            ConstraintLayout constraintLayout = ((ConstraintLayout)inflatedView.getParent()).findViewWithTag("ConstraintLayoutModel");

            SetModelAbilites(currentModel, constraintLayout,  modelId);
            constraintLayout.setTag("");
            SetModelStats(inflatedView.findViewById(R.id.ModelStatsIndicator), currentModel, modelId);
        }
    }

    public class OnClickDeactivate implements  View.OnClickListener
    {
        public DeactivatableInterface deactivatable;

        public OnClickDeactivate(DeactivatableInterface deactivatable)
        {
            this.deactivatable = deactivatable;
        }
        @Override
        public void onClick(View view) {
            deactivatable.FlipActive();

            fileHandler.saveMatchup(matchup);
        }
    }

    private class OnClickAddWeapon implements View.OnClickListener
    {
        private boolean inflatedAddWeapon = false;

        private ConstraintLayout weaponAddConstraintLayout;

        private EditText weaponAddName;
        private EditText weaponAddAmountD3;
        private EditText weaponAddAmountD6;
        private EditText weaponAddAmount;
        private EditText weaponAddDamageD3;
        private EditText weaponAddDamageD6;
        private EditText weaponAddDamage;
        private EditText weaponAddStrength;
        private EditText weaponAddAP;

        private Button addButton;
        private Button cancelButton;
    //    private Model model;
        private ModelIdentifier modelId;
        public OnClickAddWeapon(ModelIdentifier modelId)
        {
    //        this.model = model;

            this.modelId = modelId;
        }

        private void ShowAddWeapon()
        {
            weaponAddConstraintLayout.setVisibility(View.VISIBLE);
        }
        private void HideAddWeapon()
        {
            weaponAddConstraintLayout.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if(!inflatedAddWeapon)
            {
                inflater.inflate(R.layout.add_weapon_popup,highestConstraint);

                weaponAddConstraintLayout = highestConstraint.findViewById(R.id.AddWeaponConstraintLayout);
                //((Button)findViewById(R.id.SaveModelPopup)).setOnClickListener(new OnClickListenerModelSave(model));
                weaponAddName = highestConstraint.findViewById(R.id.AddWeaponName);
                weaponAddAmountD3 = highestConstraint.findViewById(R.id.AddWeaponAmountD3);
                weaponAddAmountD6 = highestConstraint.findViewById(R.id.AddWeaponAmountD6);
                weaponAddAmount = highestConstraint.findViewById(R.id.AddWeaponAmount);
                weaponAddDamageD3 = highestConstraint.findViewById(R.id.AddWeaponDamageD3);
                weaponAddDamageD6 = highestConstraint.findViewById(R.id.AddWeaponDamageD6);
                weaponAddDamage = highestConstraint.findViewById(R.id.AddWeaponDamage);
                weaponAddStrength = highestConstraint.findViewById(R.id.AddWeaponStrength);
                weaponAddAP = highestConstraint.findViewById(R.id.AddWeaponAP);

                addButton = highestConstraint.findViewById(R.id.AddWeaponAddButton);
                cancelButton = highestConstraint.findViewById(R.id.AddWeaponCancelButton);



                inflatedAddWeapon = true;
            }
            else
            {
                ShowAddWeapon();

            }

            addButton.setOnClickListener(new AddWeapon());
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideAddWeapon();
                }
            });
        }

        private class AddWeapon implements  View.OnClickListener
        {
            private String name;
            private int amountD3,amountD6, amount,damageD3,damageD6,damage,strength,aP;


            @Override
            public void onClick(View view) {
                if (weaponAddName.getText().toString().isEmpty())
                {
                    weaponAddName.setError("Name is Required");
                    return;
                }
                else name = weaponAddName.getText().toString();
                if (weaponAddAmountD3.getText().toString().isEmpty()) amountD3 = 0;
                else amountD3 = Integer.parseInt(weaponAddAmountD3.getText().toString());
                if (weaponAddAmountD6.getText().toString().isEmpty()) amountD6 = 0;
                else amountD6 = Integer.parseInt(weaponAddAmountD6.getText().toString());
                if (weaponAddAmount.getText().toString().isEmpty()) amount = 0;
                else amount = Integer.parseInt(weaponAddAmount.getText().toString());
                if (weaponAddDamageD3.getText().toString().isEmpty()) damageD3 = 0;
                else damageD3 = Integer.parseInt(weaponAddDamageD3.getText().toString());
                if (weaponAddDamageD6.getText().toString().isEmpty()) damageD6 = 0;
                else damageD6 = Integer.parseInt(weaponAddDamageD6.getText().toString());
                if (weaponAddDamage.getText().toString().isEmpty()) damage = 0;
                else damage = Integer.parseInt(weaponAddDamage.getText().toString());
                if (weaponAddStrength.getText().toString().isEmpty()) strength = 0;
                else strength = Integer.parseInt(weaponAddStrength.getText().toString());
                if (weaponAddAP.getText().toString().isEmpty()) aP = 0;
                else aP = Integer.parseInt(weaponAddAP.getText().toString());

                DamageAmount dA = new DamageAmount(damage,damageD3,damageD6);
                AttackAmount rAA = new AttackAmount(amount,amountD3,amountD6);

                Weapon weapon = new Weapon(strength, aP, dA, rAA );
                weapon.name = name;

                Model modelToAddWeapon = matchup.GetModel(modelId);
                modelToAddWeapon.listOfRangedWeapons.add(weapon);




                fileHandler.saveMatchup(matchup);

                UIIdentifier uiIdentifier = new UIIdentifier(UI_WEAPON_LAYOUT_MODEL,modelId);


                AddWeapon(highestConstraint.findViewWithTag(uiIdentifier),weapon);



                HideAddWeapon();
            }
        }
    }


    public void EditWeaponActivityStart(View v)
    {
        Intent intent = new Intent(this,EditWeaponActivity.class);

        ModelIdentifier modelId = (ModelIdentifier) v.getTag(R.string.MODEL_IDENTIFIER);

        UIIdentifier uiId = (UIIdentifier) v.getTag(R.string.UI_IDENTIFIER);


        intent.putExtra(""+R.string.UI_IDENTIFIER,uiId.elementName);


        intent.putExtra("" +R.string.MODEL_IDENTIFIER, modelId.toString());


        intent.putExtra("matchupName",matchup.name);

        activityResultLauncherWeapon.launch(intent);

    }

    private void SetModelAbilites(Model model, ConstraintLayout constraintLayout, ModelIdentifier modelId)
    {
          TableLayout abilityTable =  highestConstraint.findViewWithTag("AbilityLayoutModels");




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



          UIIdentifier uiId = new UIIdentifier(UI_ABILITY_LAYOUT_MODEL,modelId);

          ImageButton addButton = highestConstraint.findViewById(R.id.EditModelAbilities);


          addButton.setOnClickListener(new OnClickListenerAddAbility(model,this,  uiId));

          addButton.setTag(R.string.MODEL_IDENTIFIER, modelId);




          abilityTable.addView(tableRowButton);

          addButton.setId(R.id.noId);

          abilityTable.setTag(uiId);


    }

   private class OnClickListenerAddAbility implements View.OnClickListener
   {
       private  Model abilityHolder;
       private  CompareActivity compareActivity;
       private UIIdentifier uiId;

       public OnClickListenerAddAbility(Model abilityHolder, CompareActivity compareActivity, UIIdentifier uiId)
       {
           this.abilityHolder = abilityHolder;
           this.compareActivity = compareActivity;
           this.uiId = uiId;
       }
       @Override
       public void onClick(View view) {

           StartEditAbilites(view, abilityHolder, uiId);

       }
   }

    private void SetModelStats(TableRow tableRow, Model model, ModelIdentifier modelId)
    {

        UIIdentifier uiId = new UIIdentifier(UI_MODEL_MODIFIER_LAYOUT, modelId);
        ImageButton button = (ImageButton)highestConstraint.findViewById(R.id.EditModelStatsButton);
        CreateModifiers(model,uiId, tableRow, button);





        ConstraintLayout constraintLayout = (ConstraintLayout)tableRow.getParent().getParent();

        TableLayout weaponLayout = constraintLayout.findViewWithTag("WeaponLayout");

        UIIdentifier uiIdWeapon = new UIIdentifier(UI_WEAPON_LAYOUT_MODEL,modelId);
        weaponLayout.setTag(uiIdWeapon);
        for(int i = 0; i < model.listOfRangedWeapons.size();i++)
        {
            AddWeapon(weaponLayout,model.listOfRangedWeapons.get(i));
        }







    }

    private void AddWeapon(TableLayout tableLayout, Weapon weapon)
    {
        TableRow tableRow = new TableRow(context);

        TextView nameText = new TextView(context);
        nameText.setText(weapon.name);
        nameText.setTextSize(10);
        nameText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView abilityText = new TextView(context);
        abilityText.setText(""+weapon.weaponRules.size());
        abilityText.setTextSize(10);
        abilityText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView attacksText = new TextView(context);
        attacksText.setText(SetWeaponAttacks(weapon));
        attacksText.setTextSize(10);
        attacksText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView strengthText = new TextView(context);
        strengthText.setText("" +weapon.strength);
        strengthText.setTextSize(10);
        strengthText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView apText = new TextView(context);
        apText.setText(""+weapon.ap);
        apText.setTextSize(10);
        apText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        TextView damageText = new TextView(context);
        damageText.setText(SetWeaponDamage(weapon));
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

    private String SetWeaponAttacks(Weapon rangedWeapon)
    {
        String stringToReturn = "";
        AttackAmount amount = rangedWeapon.amountOfAttacks;


        if(amount.numberOfD6 != 0)
        {
            stringToReturn+=  amount.numberOfD6 +"D6 ";
        }
        if(amount.numberOfD3 != 0)
        {
            stringToReturn+=  amount.numberOfD3 +"D3 ";
        }

        if(amount.rawNumberOfAttacks != 0)
        {
            stringToReturn+= "" + amount.rawNumberOfAttacks;
        }


        return stringToReturn;
    }
    private String SetWeaponDamage(Weapon rangedWeapon)
    {
        String stringToReturn = "";
        DamageAmount amount = rangedWeapon.damageAmount;


        if(amount.d6DamageAmount != 0)
        {
            stringToReturn+=  amount.d6DamageAmount +"D6 ";
        }
        if(amount.d3DamageAmount != 0)
        {
            stringToReturn+=  amount.d3DamageAmount +"D3 ";
        }

        if(amount.rawDamageAmount != 0)
        {
            stringToReturn+= "" + amount.rawDamageAmount;
        }


        return stringToReturn;
    }



    public void instaniateUnitButton(View buttonToModify, Unit unit, UnitIdentifier unitId)
    {
        Button topButton = (Button)buttonToModify.findViewById(R.id.UnitTopButton);
        topButton.setText(unit.unitName);
    }

    public void ChangeUnitModifer(View buttonClicked)
    {
     //   Log.d("hej", "kallas metoden");

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

    }



    public void DropDownClick(View v)
    {
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


    private  class OnClickListenerModelAndUnitStats implements View.OnClickListener
    {
        private  ModifierHolder modifierHolder;
        private UIIdentifier uiId;


        public OnClickListenerModelAndUnitStats(ModifierHolder modifierHolder, UIIdentifier uiId)
        {
            this.modifierHolder = modifierHolder;
            this.uiId = uiId;
        }


        @Override
        public void onClick(View view) {
            if(!inflatedModelStats)
            {
                inflater.inflate(R.layout.activity_popup,highestConstraint);

                weaponSkillView = (EditText)highestConstraint.findViewById(R.id.WeaponSkillModelPopup);
                ballisticSkillView   = (EditText)highestConstraint.findViewById(R.id.BallisticSkillModelPopup);
                strengthView  = (EditText)highestConstraint.findViewById(R.id.StrengthModelPopup);
                toughnessView = (EditText)highestConstraint.findViewById(R.id.ToughnessModelPopup);
                woundsView  = (EditText)highestConstraint.findViewById(R.id.WoundsModelPopup);
                attacksView  = (EditText)highestConstraint.findViewById(R.id.AttacksModelPopup);
                armorSaveView  = (EditText)highestConstraint.findViewById(R.id.ArmorSaveModelPopup);
                InvSaveView  = (EditText)highestConstraint.findViewById(R.id.InvSaveModelPopup);

                weaponSkillIncrease = (ImageButton)highestConstraint.findViewById(R.id.WeaponSkillIncrease);
                weaponSkillDecrease = (ImageButton)highestConstraint.findViewById(R.id.WeaponSkillDecrease);
                ballisticSkillIncrease = (ImageButton)highestConstraint.findViewById(R.id.BallisticSkillIncrease);
                ballisticSkillDecrease = (ImageButton)highestConstraint.findViewById(R.id.BallisticSkillDecrease);
                strengthIncrease = (ImageButton)highestConstraint.findViewById(R.id.StrengthIncrease);
                strengthDecrease = (ImageButton)highestConstraint.findViewById(R.id.StrengthDecrease);
                toughnessIncrease = (ImageButton)highestConstraint.findViewById(R.id.ToughnessIncrease);
                toughnessDecrease = (ImageButton)highestConstraint.findViewById(R.id.ToughnessDecrease);
                woundsIncrease = (ImageButton)highestConstraint.findViewById(R.id.WoundsIncrease);
                woundsDecrease = (ImageButton)highestConstraint.findViewById(R.id.WoundsDecrease);
                attacksIncrease = (ImageButton)highestConstraint.findViewById(R.id.AttacksIncrease);
                attacksDecrease = (ImageButton)highestConstraint.findViewById(R.id.AttacksDecrease);
                armorSavesIncrease = (ImageButton)highestConstraint.findViewById(R.id.SavesIncrease);
                armorSavesDecrease = (ImageButton)highestConstraint.findViewById(R.id.SavesDecrease);
                invulnerableSavesIncrease = (ImageButton)highestConstraint.findViewById(R.id.InvulnerableIncrease);
                invulnerableSavesDecrease = (ImageButton)highestConstraint.findViewById(R.id.InvulnerableDecrease);

                inflatedModelStats = true;
            }
            else
            {
                ShowPopup(view);
            }
            popupActive = true;
            ((Button)highestConstraint.findViewById(R.id.SaveModelPopup)).setOnClickListener(new OnClickListenerModelSave(modifierHolder, uiId));


            strengthView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.Strength));
            toughnessView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.Toughness));
            woundsView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.Wounds));
            attacksView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.Attacks));
            armorSaveView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.ArmorSaves));
            InvSaveView.setText(""+ modifierHolder.GetModifierValue(UnitAndModelSkill.InvulnerableSaves));

            strengthIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, strengthView,UnitAndModelSkill.Strength,true));
            strengthDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, strengthView,UnitAndModelSkill.Strength,false));
            toughnessIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, toughnessView,UnitAndModelSkill.Toughness,true));
            toughnessDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, toughnessView,UnitAndModelSkill.Toughness,false));
            woundsIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, woundsView,UnitAndModelSkill.Wounds,true));
            woundsDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, woundsView,UnitAndModelSkill.Wounds,false));
            attacksIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, attacksView,UnitAndModelSkill.Attacks,true));
            attacksDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, attacksView,UnitAndModelSkill.Attacks,false));
            armorSavesIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, armorSaveView,UnitAndModelSkill.ArmorSaves,true));
            armorSavesDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, armorSaveView,UnitAndModelSkill.ArmorSaves,false));
            invulnerableSavesIncrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, InvSaveView,UnitAndModelSkill.InvulnerableSaves,true));
            invulnerableSavesDecrease.setOnClickListener(new OnClickIncreaseStats(modifierHolder, InvSaveView,UnitAndModelSkill.InvulnerableSaves,false));
        }
    }

    public enum UnitAndModelSkill
    {   HitSkill,
        Strength,
        Toughness,
        Wounds,
        Attacks,
        ArmorSaves,
        InvulnerableSaves
    }

    private class OnClickIncreaseStats implements View.OnClickListener
    {
        private ModifierHolder modifierHolder;
        private EditText textToChange;
        private UnitAndModelSkill whatToChange;
        private int amount;
        public OnClickIncreaseStats(ModifierHolder modifierHolder, EditText textToChange, UnitAndModelSkill whatToChange, boolean increase)
        {
            this.modifierHolder = modifierHolder;
            this.textToChange = textToChange;
            this.whatToChange = whatToChange;
            if (increase) amount = 1;
            else amount = -1;
        }
        @Override
        public void onClick(View buttonClicked) {
            int value = modifierHolder.ChangeModifiers(whatToChange, amount);
            textToChange.setText(""+value);
        }
    }

    private class  OnClickListenerModelSave implements View.OnClickListener
    {
        private  ModifierHolder modifierHolder;
        private UIIdentifier uiId;

        public OnClickListenerModelSave(ModifierHolder modifierHolder, UIIdentifier uiId)
        {
            this.modifierHolder = modifierHolder;
            this.uiId = uiId;
        }


        @Override
        public void onClick(View view)
        {

            modifierHolder.SetModifierValue(UnitAndModelSkill.Strength, Integer.parseInt(strengthView.getText().toString()));
            modifierHolder.SetModifierValue(UnitAndModelSkill.Toughness, Integer.parseInt(toughnessView.getText().toString()));
            modifierHolder.SetModifierValue(UnitAndModelSkill.Wounds, Integer.parseInt(woundsView.getText().toString()));
            modifierHolder.SetModifierValue(UnitAndModelSkill.Attacks, Integer.parseInt(attacksView.getText().toString()));
            modifierHolder.SetModifierValue(UnitAndModelSkill.ArmorSaves, Integer.parseInt(armorSaveView.getText().toString()));
            modifierHolder.SetModifierValue(UnitAndModelSkill.InvulnerableSaves, Integer.parseInt(InvSaveView.getText().toString()));

            SetModifiers(uiId, modifierHolder);

            fileHandler.saveMatchup(matchup);
            ClosePopup(view);
        }
    }


    public void ShowPopup(View v)
    {
        View popup = highestConstraint.findViewById(R.id.ConstraintLayoutModelPopup);
        popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v)
    {
        View popup = highestConstraint.findViewById(R.id.ConstraintLayoutModelPopup);
        popup.setVisibility(View.GONE);
        popupActive = false;
    }

    @Override
    public void onBackPressed() {
        if (popupActive)
        {
            ClosePopup(null);
            return;
        }
        super.onBackPressed();
    }
}
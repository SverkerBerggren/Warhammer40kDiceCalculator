package com.example.warhammer40kdicecalculator.Activities;

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

import android.os.Trace;
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
import com.example.warhammer40kdicecalculator.AbilityUIHolder;
import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.DatabaseManager;
import com.example.warhammer40kdicecalculator.DatasheetModeling.GamePiece;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.DeactivatableInterface;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.DiceAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;
import com.example.warhammer40kdicecalculator.FileHandling.FileHandler;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;
import com.example.warhammer40kdicecalculator.Matchup;
import com.example.warhammer40kdicecalculator.ModifierHolder;
import com.example.warhammer40kdicecalculator.R;

import java.util.ArrayList;

public class CompareActivity extends AppCompatActivity implements AbilityUIHolder {

    public Matchup matchup;

    private String DONT_DROP_DOWN = "dontDropDown";

    private String FRIENDLY = "friendly";
    private String ENEMY = "enemy";

    // TODO: bababooey should be enum
    public final String ABILITY_LAYOUT_UNIT = "AbilityLayoutUnit";
    public final String UI_WEAPON_LAYOUT_MODEL = "WeaponLayoutModel";
    public final String UI_UNIT_MODIFIER_LAYOUT = "UnitModifierLayout";
    public final String UI_MODEL_MODIFIER_LAYOUT = "ModelModifierLayout";
    public final String UI_ARMY_MODIFIER_LAYOUT = "ArmyModifierLayout";
    public final String UI_ABILITY_LAYOUT_MODEL = "AbilityLayoutModel";
    public final String UI_ARMY_ABILITY_LAYOUT = "ArmyAbilityLayout";
    public final String UI_WEAPON_LAYOUT = "UiWeaponLayout";

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

    private ActivityResultLauncher<Intent> activityResultLauncherAbility;
    private ActivityResultLauncher<Intent> activityResultLauncherWeapon;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        inflater =  getLayoutInflater();
        context = getBaseContext();

        matchup = FileHandler.GetInstance().getMatchup( getIntent().getStringExtra("SourceFile"));
        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUiActivityCallbackAbilities());

        activityResultLauncherWeapon = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UpdateUIWeaponCallback());



        highestConstraint = findViewById(R.id.ConstraintLayoutCompare);
        Trace.beginSection("Arme skapande");
        createArmy(matchup.friendlyArmy,FRIENDLY);
        createArmy(matchup.enemyArmy,ENEMY);
        Trace.endSection();

    }

    public void Setup(Context context, Matchup matchup)
    {
        this.context =context ;
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
    public void AbilityAdded(AbilityEnum abilityEnum, GamePiece gamePiece) {

        if(gamePiece instanceof Model)
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

            ArrayList<String> abilitiesToRemove = data.getStringArrayListExtra("abilitiesRemoved");
            ArrayList<String> abilitiesToAdd = data.getStringArrayListExtra("abilitiesAdded");

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

            for(int i = 0; i < model.weapons.size(); i++)
            {
                AddWeapon(tableLayout,model.weapons.get(i));
            }


        }
    }

    private void ReloadMatchup()
    {
        matchup = FileHandler.GetInstance().getMatchup(matchup.name);
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

    private enum WidgetType
    {
        TableRow,
        ArmyEditButton,
        ArmyVerticalLayout,
        ArmyAbilitiesTableLayout,
        EditArmyAbilities
    }

    private void createArmy(Army army, String allegiance)
    {
        ArmyIdentifier armyId = new ArmyIdentifier(allegiance, matchup.name);
        UIIdentifier uiIdArmy = new UIIdentifier(UI_ARMY_MODIFIER_LAYOUT, armyId);
        TableRow tableRow = (TableRow)GetUiElement(allegiance,WidgetType.TableRow);
        ImageButton editButton = (ImageButton)GetUiElement(allegiance,WidgetType.ArmyEditButton);
        CreateModifiers(army, uiIdArmy, tableRow, editButton);
        CreateArmyAbilities(armyId,army);

        instantiateArmies(allegiance);
    }

    private Object GetUiElement( String allegiance, WidgetType type)
    {
        Trace.beginSection("Leta efter vy");
        switch (type)
        {
            case TableRow:
                Trace.endSection();
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.TableRowFriendlyArmy)):(findViewById(R.id.TableRowEnemyArmy));
            case ArmyEditButton:
                Trace.endSection();
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.EditFriendlyArmyButton)):(findViewById(R.id.EditEnemyArmyButton));
            case ArmyVerticalLayout:
                Trace.endSection();
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.VerticalLayoutFriendlyArmy)):(findViewById(R.id.VerticalLayoutEnemyArmy));
            case ArmyAbilitiesTableLayout:
                Trace.endSection();
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.AbilityLayoutFriendlyArmy)):(findViewById(R.id.AbilityLayoutEnemyArmy));
            case EditArmyAbilities:
                Trace.endSection();
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.EditFriendlyArmyAbilities)):(findViewById(R.id.EditEnemyArmyAbilities));
        }

        Log.d("Compare activity","Requested invalid ui element");
        return  null;
    }

    private ArrayList<Unit> GetMatchupUnitsFromAllegiance(String allegiance)
    {
        return (allegiance.equals(FRIENDLY) ? (matchup.friendlyArmy.units):(matchup.enemyArmy.units));
    }


    // TODO: verticalLayout.getChildAt(verticalLayout.getChildCount()-1) pattern wack af
    private void instantiateArmies(String allegiance)
    {
        ArrayList<Unit> units = GetMatchupUnitsFromAllegiance(allegiance);
        for(int i = 0; i < units.size();i++)
        {
            Trace.beginSection("Unit inflating");
            ViewGroup verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab,(ViewGroup)GetUiElement(allegiance,WidgetType.ArmyVerticalLayout));
            Trace.endSection();
            Unit unit = units.get(i);
            UnitIdentifier unitIdentifier = new UnitIdentifier(allegiance,null,i,matchup.name);
            instantiateUnitButton(verticalLayout.getChildAt(verticalLayout.getChildCount()-1),unit,unitIdentifier);
            CreateUnitAbilites(unit,(LinearLayout) verticalLayout,inflater, unitIdentifier);

            TableRow tableRow = (TableRow)findViewById(R.id.TableRowUnitModifiers);
            ImageButton button = (ImageButton)findViewById(R.id.EditUnitModifierButton);
            UIIdentifier uiId = new UIIdentifier(UI_UNIT_MODIFIER_LAYOUT, unitIdentifier);
            CreateModifiers(unit, uiId, tableRow, button);
            CreateModel(verticalLayout.getChildAt(verticalLayout.getChildCount()-1),unit,i,allegiance,inflater);
        }
    }

    private void CreateArmyAbilities(ArmyIdentifier armyIdentifier, Army army)
    {
        TableLayout tableLayout = (TableLayout) GetUiElement(armyIdentifier.allegiance,WidgetType.ArmyAbilitiesTableLayout);
        for(int i = 0; i < army.abilities.size(); i++)
        {
            tableLayout.addView(CreateTableRow(army.abilities.get(i).name));
        }
        ImageButton editButton = (ImageButton) GetUiElement(armyIdentifier.allegiance,WidgetType.EditArmyAbilities);
        UIIdentifier uiId = new UIIdentifier(UI_ARMY_ABILITY_LAYOUT, armyIdentifier);
        editButton.setOnClickListener(new OnClickListenerEditAbilites(army,uiId));
        editButton.setTag(IdentifierType.ARMY.GetResourceId(),armyIdentifier);
        tableLayout.setTag(uiId);
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


        for(AbilityEnum abilityEnum : unit.GetAbilityBitField())
        {
            Ability ability = DatabaseManager.getInstance().GetAbility(abilityEnum);
            TableRow tableRow = new TableRow(context);
            tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
            tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            TextView abilityTextView = new TextView(context);
            abilityTextView.setText(ability.name);
            abilityTextView.setTextSize(10);
            abilityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            tableRow.addView(abilityTextView);
            unitAbilitLayout.addView(tableRow);
        }

        ImageButton editAbilities = highestConstraint.findViewById(R.id.EditUnitAbilities);

        editAbilities.setTag(IdentifierType.UNIT.GetResourceId(),unitIdentifier);

        editAbilities.setOnClickListener(new OnClickListenerEditAbilites(unit, uiId));

        editAbilities.setId(R.id.noId);

    }


    private class OnClickListenerEditAbilites implements View.OnClickListener
    {
        private GamePiece gamePiece;
        private UIIdentifier uiId;


        public OnClickListenerEditAbilites(GamePiece gamePiece, UIIdentifier uiId)
        {
             this.gamePiece = gamePiece;
             this.uiId = uiId;
        }

        @Override
        public void onClick(View view) {
            StartEditAbilites(view, gamePiece,uiId);
        }
    }
    public void StartEditAbilites(View view, GamePiece gamePiece, UIIdentifier uiId )
    {
        Intent intent = new Intent(context, Activity_Edit_Abilities.class);

        //TODO: ASAP maste fixa att R.string.UNIT_IDENTIFIER ar en enum eller gettas. Enum eller kosntant?
        Identifier identifier = (Identifier) view.getTag(gamePiece.GetIdentifierType().GetResourceId());
        intent.putExtra(IdentifierType.IDENTIFIER.name(), gamePiece.GetIdentifierType().name());
        intent.putExtra(""+gamePiece.GetIdentifierType().GetResourceId(),identifier.toString());
        intent.putExtra("matchupName",identifier.GetMatchupName());
        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

        activityResultLauncherAbility.launch(intent);
    }

    // TODO: Tag systemet suger fet lowkey
    public void CreateModel(View buttonToModify, Unit unit, int unitNumber,String Allegiance , LayoutInflater inflater)
    {
        LinearLayout modelsLayout = (LinearLayout) buttonToModify.findViewById(R.id.ModelsSubLayout);
        for(int i = 0; i < unit.listOfModels.size();i++)
        {
            Model currentModel = unit.listOfModels.get(i);
            Trace.beginSection("Inflatea modeller");
            View inflatedView = inflater.inflate(R.layout.model_stats_prefab,modelsLayout);
            Trace.endSection();

            Button modelTopButton = highestConstraint.findViewWithTag("inividualModelTopButton");
            modelTopButton.setText(currentModel.name);
            modelTopButton.setTag("");

            ModelIdentifier modelId = new ModelIdentifier(Allegiance, null, unitNumber,i,matchup.name );

            ImageButton editWeaponButton = inflatedView.findViewById(R.id.EditWeaponsButton);
            editWeaponButton.setTag(IdentifierType.MODEL.GetResourceId(),modelId);
            editWeaponButton.setTag(R.string.UI_IDENTIFIER,new UIIdentifier(UI_WEAPON_LAYOUT_MODEL,modelId));
            editWeaponButton.setId(R.id.noId);

            ImageButton addWeaponButton = highestConstraint.findViewWithTag("AddWeaponModelButton");
            addWeaponButton.setOnClickListener(new OnClickAddWeapon(modelId));
            addWeaponButton.setTag("");

            CheckBox deactivateModelCheckBox = highestConstraint.findViewById(R.id.DeactivateModelsButton);
            deactivateModelCheckBox.setChecked(currentModel.active);
            deactivateModelCheckBox.setOnClickListener(new OnClickDeactivate(currentModel));
            deactivateModelCheckBox.setId(R.id.noId);
            ConstraintLayout constraintLayout = ((ConstraintLayout)inflatedView.getParent()).findViewWithTag("ConstraintLayoutModel");

            SetModelAbilites(currentModel, constraintLayout,  modelId);
            constraintLayout.setTag("");
            SetModelStats(inflatedView.findViewById(R.id.ModelStatsIndicator), currentModel, modelId);
            modelTopButton.callOnClick();
        }
    }

    // TODO: wack borde finnas ett battre satt men pallar tji
    public class OnClickDeactivateAbility implements  View.OnClickListener
    {
        private AbilityBitField bitField;
        private AbilityEnum abilityEnum;

        public OnClickDeactivateAbility(AbilityBitField abilityBitField, AbilityEnum abilityEnum)
        {
            this.bitField = abilityBitField;
            this.abilityEnum = abilityEnum;
        }
        @Override
        public void onClick(View view) {
            bitField.SetActive(abilityEnum,!bitField.IsSet(abilityEnum));

            FileHandler.GetInstance().saveMatchup(matchup);
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

            FileHandler.GetInstance().saveMatchup(matchup);
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

                DiceAmount dA = new DiceAmount(damage,damageD3,damageD6);
                DiceAmount rAA = new DiceAmount(amount,amountD3,amountD6);

                Weapon weapon = new Weapon(strength, aP, dA, rAA );
                weapon.name = name;

                Model modelToAddWeapon = matchup.GetModel(modelId);
                modelToAddWeapon.weapons.add(weapon);




                FileHandler.GetInstance().saveMatchup(matchup);

                UIIdentifier uiIdentifier = new UIIdentifier(UI_WEAPON_LAYOUT_MODEL,modelId);


                AddWeapon(highestConstraint.findViewWithTag(uiIdentifier),weapon);



                HideAddWeapon();
            }
        }
    }


    public void EditWeaponActivityStart(View v)
    {
        Intent intent = new Intent(this,EditWeaponActivity.class);

        ModelIdentifier modelId = (ModelIdentifier) v.getTag(IdentifierType.MODEL.GetResourceId());

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
          for(AbilityEnum abilityEnum : model.GetAbilityBitField())
          {
              Ability ability = DatabaseManager.getInstance().GetAbility(abilityEnum);
              TableRow tableRow = new TableRow(context);
              tableRow.setBackgroundColor(Color.parseColor("#DFDADA"));
              tableRow.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


              TextView abilityTextView = new TextView(context);
              abilityTextView.setText(ability.name);
              abilityTextView.setTextSize(10);
              abilityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

              tableRow.addView(abilityTextView);
              abilityTable.addView(tableRow);
          }
          TableRow tableRowButton = new TableRow(context);
          tableRowButton.setBackgroundColor(Color.parseColor("#DFDADA"));
          UIIdentifier uiId = new UIIdentifier(UI_ABILITY_LAYOUT_MODEL,modelId);
          ImageButton addButton = highestConstraint.findViewById(R.id.EditModelAbilities);

          addButton.setOnClickListener(new OnClickListenerEditAbilites(model,  uiId));
          addButton.setTag(IdentifierType.MODEL.GetResourceId(), modelId);
          abilityTable.addView(tableRowButton);
          addButton.setId(R.id.noId);
          abilityTable.setTag(uiId);
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
        for(int i = 0; i < model.weapons.size(); i++)
        {
            AddWeapon(weaponLayout,model.weapons.get(i));
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
        abilityText.setText(""+weapon.GetAbilityBitField().Count());
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
        DiceAmount amount = rangedWeapon.amountOfAttacks;


        if(amount.numberOfD6 != 0)
        {
            stringToReturn+=  amount.numberOfD6 +"D6 ";
        }
        if(amount.numberOfD3 != 0)
        {
            stringToReturn+=  amount.numberOfD3 +"D3 ";
        }

        if(amount.baseAmount != 0)
        {
            stringToReturn+= "" + amount.baseAmount;
        }


        return stringToReturn;
    }
    private String SetWeaponDamage(Weapon rangedWeapon)
    {
        String stringToReturn = "";
        DiceAmount amount = rangedWeapon.damageAmount;


        if(amount.numberOfD6 != 0)
        {
            stringToReturn+=  amount.numberOfD6 +"D6 ";
        }
        if(amount.numberOfD3 != 0)
        {
            stringToReturn+=  amount.numberOfD3 +"D3 ";
        }

        if(amount.baseAmount != 0)
        {
            stringToReturn+= "" + amount.baseAmount;
        }


        return stringToReturn;
    }



    public void instantiateUnitButton(View buttonToModify, Unit unit, UnitIdentifier unitId)
    {
        Button topButton = (Button)buttonToModify.findViewById(R.id.UnitTopButton);
        topButton.setText(unit.unitName);
    }

    public void DropDownClick(View v)
    {
        ViewGroup viewGroup =  (ViewGroup) v.getParent();

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

            FileHandler.GetInstance().saveMatchup(matchup);
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
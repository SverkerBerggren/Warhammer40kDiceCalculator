package com.app.DamageCalculator40k.Activities;

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


import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityUIHolder;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.DeactivatableInterface;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.Enums.StatModifier;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Identifiers.ArmyIdentifier;
import com.app.DamageCalculator40k.Identifiers.Identifier;
import com.app.DamageCalculator40k.Identifiers.ModelIdentifier;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;
import com.app.DamageCalculator40k.Identifiers.UnitIdentifier;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;
import com.app.DamageCalculator40k.UI.EditModifierPopup;
import com.app.DamageCalculator40k.UI.ModifierEditing;

import java.util.ArrayList;

public class CompareActivity extends AppCompatActivity implements AbilityUIHolder, ModifierEditing {

    public Matchup matchup;
    private String DONT_DROP_DOWN = "dontDropDown";

    private String FRIENDLY = "friendly";
    private String ENEMY = "enemy";

    // TODO: bababooey should be enum
    public enum WidgetType
    {
        TableRow,
        ArmyEditButton,
        ArmyVerticalLayout,
        ArmyAbilitiesTableLayout,
        EditArmyAbilities,
        AbilityLayoutUnit,
        WeaponLayoutModel,
        ModelModifierLayout,
        UnitModifierLayout,
        ArmyModifierLayout,
        ModelAbilityLayout,
        ArmyAbilityLayout,
        AbilityEditButton,
        AbilityLayout,
        WeaponLayout
    }

    private LayoutInflater inflater;

    private EditModifierPopup editModifierPopup;

    public ViewGroup highestConstraint;

    private ActivityResultLauncher<Intent> activityResultLauncherAbility;
    private ActivityResultLauncher<Intent> activityResultLauncherWeapon;

    private Context context;


    // Hard coded af do be like that
    public void UpdateModifierValue(StatModifier modifier, int value, UIIdentifier uiId)
    {
        ViewGroup tableLayout = highestConstraint.findViewWithTag(uiId);

        ViewGroup tableRow = (TableRow)tableLayout.getChildAt(1);
        TextView textView = (TextView) tableRow.getChildAt(modifier.ordinal());
        textView.setText(context.getString(R.string.SingleIntegerText,value));
    }

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


        editModifierPopup = new EditModifierPopup(highestConstraint,inflater,matchup, context, this);
        Trace.beginSection("Arme skapande");
        createArmy(matchup.friendlyArmy,FRIENDLY);
        createArmy(matchup.enemyArmy,ENEMY);
        Trace.endSection();

    }

    public void Setup(Context context, Matchup matchup, LayoutInflater inflater, ViewGroup highestConstraint)
    {
        this.highestConstraint = highestConstraint;
        this.context =context ;
        this.matchup = matchup;
        this.inflater = inflater;

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
    public void AbilityAdded(Ability abilityEnum, GamePiece gamePiece) {

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

            UIIdentifier uiId = new UIIdentifier((WidgetType) data.getSerializableExtra(""+ R.string.UI_IDENTIFIER),abilityHolderIdentifier);


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

            UIIdentifier uiId = new UIIdentifier((WidgetType) data.getSerializableExtra(""+R.string.UI_IDENTIFIER),modelId);

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

    private void createArmy(Army army, String allegiance)
    {
        ArmyIdentifier armyId = new ArmyIdentifier(allegiance, matchup.name);
        UIIdentifier uiIdArmy = new UIIdentifier(WidgetType.ArmyModifierLayout, armyId);
        TableLayout tableLayout = (TableLayout) GetUiElement(allegiance,WidgetType.ArmyModifierLayout);
        ImageButton editButton = (ImageButton)GetUiElement(allegiance,WidgetType.ArmyEditButton);
        CreateModifiers(army, uiIdArmy, editButton, tableLayout);
        CreateGamePieceAbilities(highestConstraint,army,armyId);

        instantiateArmies(allegiance);
    }


    //TODO: Hela skiten med att ha friendly ar cap. Borde bort men manana
    private Object GetUiElement( String allegiance, WidgetType type)
    {
        switch (type)
        {
            case ArmyEditButton:
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.EditFriendlyArmyButton)):(findViewById(R.id.EditEnemyArmyButton));
            case ArmyVerticalLayout:
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.VerticalLayoutFriendlyArmy)):(findViewById(R.id.VerticalLayoutEnemyArmy));
            case ArmyAbilitiesTableLayout:
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.AbilityLayoutFriendlyArmy)):(findViewById(R.id.AbilityLayoutEnemyArmy));
            case EditArmyAbilities:
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.EditFriendlyArmyAbilities)):(findViewById(R.id.EditEnemyArmyAbilities));
            case ArmyModifierLayout:
                return (allegiance.equals(FRIENDLY)) ? (findViewById(R.id.FriendlyArmyModifier)):(findViewById(R.id.EnemyArmyModifiers));
        }

        Log.d("Compare activity","Requested invalid ui element");
        return  null;
    }

    private int GetUIResourceId(WidgetType widgetType, Identifier identifier)
    {
        switch (widgetType)
        {
            case AbilityEditButton:
                if(identifier.GetIdentifierEnum().equals(IdentifierType.MODEL))
                {
                    return R.id.EditModelAbilities;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.UNIT))
                {
                    return R.id.EditUnitAbilities;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.ARMY))
                {
                    return  (identifier.allegiance.equals(FRIENDLY)) ? (R.id.EditFriendlyArmyAbilities):( R.id.EditEnemyArmyAbilities);
                }
                break;
            case AbilityLayout:
                if(identifier.GetIdentifierEnum().equals(IdentifierType.MODEL))
                {
                    return R.id.AbilityLayoutModels;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.UNIT))
                {
                    return R.id.AbilityLayoutUnits;
                }
                if(identifier.GetIdentifierEnum().equals(IdentifierType.ARMY))
                {
                    return  (identifier.allegiance.equals(FRIENDLY)) ? (R.id.AbilityLayoutFriendlyArmy):( R.id.AbilityLayoutEnemyArmy);
                }
        }
        return 0;
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
            UnitIdentifier unitIdentifier = new UnitIdentifier(allegiance,null,i,matchup.name);
            CreateUnit(unitIdentifier,(ViewGroup)GetUiElement(unitIdentifier.allegiance,WidgetType.ArmyVerticalLayout) );
        }
    }

    public void CreateUnit(UnitIdentifier unitIdentifier, ViewGroup parentView)
    {
        Unit unit = matchup.GetUnit(unitIdentifier);
        ViewGroup verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab,parentView, false);
        instantiateUnitButton(verticalLayout,unit,unitIdentifier);
        CreateGamePieceAbilities(verticalLayout, unit, unitIdentifier);

        TableLayout tableLayout = verticalLayout.findViewById(R.id.UnitTable);
        ImageButton button = verticalLayout.findViewById(R.id.EditUnitModifierButton);
        UIIdentifier uiId = new UIIdentifier(WidgetType.UnitModifierLayout, unitIdentifier);
        CreateModifiers(unit, uiId, button,tableLayout);
        CreateModel(verticalLayout,unit,unitIdentifier.index,unitIdentifier.allegiance,inflater);
        parentView.addView(verticalLayout);
    }

    private void CreateArmyAbilities(ArmyIdentifier armyIdentifier, Army army)
    {
        TableLayout tableLayout = (TableLayout) GetUiElement(armyIdentifier.allegiance,WidgetType.ArmyAbilitiesTableLayout);
        for(int i = 0; i < army.GetAbilities().size(); i++)
        {
            tableLayout.addView(CreateTableRow(army.GetAbilities().get(i).name));
        }
        ImageButton editButton = (ImageButton) GetUiElement(armyIdentifier.allegiance,WidgetType.EditArmyAbilities);
        UIIdentifier uiId = new UIIdentifier(WidgetType.ArmyAbilityLayout, armyIdentifier);
        editButton.setOnClickListener(new OnClickListenerEditAbilites(army,uiId));
        editButton.setTag(IdentifierType.ARMY.GetResourceId(),armyIdentifier);
        tableLayout.setTag(uiId);
    }

    public void CreateModifiers(GamePiece gamePiece, UIIdentifier uiId , ImageButton button, TableLayout parentLayout)
    {
        parentLayout.setTag(uiId);

        ContextThemeWrapper themeNames = new ContextThemeWrapper(context,R.style.TableRowModifierName);
        ContextThemeWrapper themeValues = new ContextThemeWrapper(context,R.style.TableRowModifierValues);
        TableRow tableRowNames = new TableRow(themeNames);
        TableRow tableRowValues = new TableRow(themeValues);
        for(StatModifier statModifier : StatModifier.values())
        {
            TextView textViewName = new TextView(themeNames);
            textViewName.setText(statModifier.GetAbbreviation());
            tableRowNames.addView(textViewName);


            TextView textViewValues = new TextView(themeValues);
            textViewValues.setText( context.getString(R.string.SingleIntegerText,gamePiece.getStatModifiers().GetModifier(statModifier)));
            tableRowValues.addView(textViewValues);
        }
        parentLayout.addView(tableRowNames);
        parentLayout.addView(tableRowValues);

        button.setOnClickListener(editModifierPopup.GetOpenModifierPopupOnClickListener(gamePiece,uiId));
        button.setId(R.id.noId);
    }

    private void CreateGamePieceAbilities(ViewGroup parentView, GamePiece gamePiece, Identifier identifier)
    {
        TableLayout abilityTable =  parentView.findViewById(GetUIResourceId(WidgetType.AbilityLayout,identifier));

        ContextThemeWrapper abilityTheme = new ContextThemeWrapper(context,R.style.AbilitiesTable);
        for(Ability ability : gamePiece.GetAbilities())
        {
            TableRow tableRow = new TableRow(abilityTheme);
            TextView abilityTextView = new TextView(abilityTheme);
            abilityTextView.setText(ability.name);

            tableRow.addView(abilityTextView);
            abilityTable.addView(tableRow);
        }
        TableRow tableRowButton = new TableRow(abilityTheme);
        UIIdentifier uiId = new UIIdentifier(WidgetType.AbilityEditButton,identifier);
        ImageButton addButton = parentView.findViewById(GetUIResourceId(WidgetType.AbilityEditButton,identifier));

        addButton.setOnClickListener(new OnClickListenerEditAbilites(gamePiece,  uiId));
        addButton.setTag(identifier.GetIdentifierEnum().GetResourceId(), identifier);
        abilityTable.addView(tableRowButton);
        abilityTable.setTag(uiId);
    }

    private class OnClickListenerEditAbilites implements View.OnClickListener
    {
        private final GamePiece gamePiece;
        private final UIIdentifier uiId;


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
        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.widgetType);

        activityResultLauncherAbility.launch(intent);
    }

    // TODO: Tag systemet suger fet lowkey
    public void CreateModel(ViewGroup parentView, Unit unit, int unitNumber, String Allegiance , LayoutInflater inflater)
    {
        LinearLayout modelsLayout = parentView.findViewById(R.id.ModelsSubLayout);
        for(int i = 0; i < unit.listOfModels.size();i++)
        {
            Model currentModel = unit.listOfModels.get(i);
            Trace.beginSection("Inflatea modeller");
            ViewGroup inflatedView = (ViewGroup) inflater.inflate(R.layout.model_stats_prefab,modelsLayout, false);
            Trace.endSection();

            Button modelTopButton = inflatedView.findViewWithTag("inividualModelTopButton");
            modelTopButton.setText(currentModel.name);
            modelTopButton.setTag("");

            ModelIdentifier modelId = new ModelIdentifier(Allegiance, null, unitNumber,i,matchup.name );

            ImageButton editWeaponButton = inflatedView.findViewById(R.id.EditWeaponsButton);
            editWeaponButton.setTag(IdentifierType.MODEL.GetResourceId(),modelId);
            editWeaponButton.setTag(R.string.UI_IDENTIFIER,new UIIdentifier(WidgetType.WeaponLayoutModel,modelId));
            editWeaponButton.setId(R.id.noId);

            ImageButton addWeaponButton = inflatedView.findViewWithTag("AddWeaponModelButton");
            addWeaponButton.setOnClickListener(new OnClickAddWeapon(modelId));
            addWeaponButton.setTag("");

            CheckBox deactivateModelCheckBox = inflatedView.findViewById(R.id.DeactivateModelsButton);
            deactivateModelCheckBox.setChecked(currentModel.active);
            deactivateModelCheckBox.setOnClickListener(new OnClickDeactivate(currentModel));
            deactivateModelCheckBox.setId(R.id.noId);

            CreateGamePieceAbilities(inflatedView, currentModel,  modelId);
            SetModelStats( inflatedView,currentModel, modelId);
            modelTopButton.callOnClick();
            modelsLayout.addView(inflatedView);
        }
    }

    public class OnClickDeactivateAbility implements  View.OnClickListener
    {
        private final Ability ability;

        public OnClickDeactivateAbility(Ability ability)
        {
            this.ability = ability;
        }
        @Override
        public void onClick(View view) {
            ability.active = !ability.active;

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

                Weapon weapon = new Weapon();
                weapon.strength = strength;
                weapon.ap = aP;
                weapon.damageAmount = dA;
                weapon.amountOfAttacks = rAA;
                weapon.name = name;

                Model modelToAddWeapon = matchup.GetModel(modelId);
                modelToAddWeapon.weapons.add(weapon);




                FileHandler.GetInstance().saveMatchup(matchup);

                UIIdentifier uiIdentifier = new UIIdentifier(WidgetType.WeaponLayoutModel,modelId);


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


        intent.putExtra(""+R.string.UI_IDENTIFIER,uiId.widgetType);


        intent.putExtra("" +R.string.MODEL_IDENTIFIER, modelId.toString());


        intent.putExtra("matchupName",matchup.name);

        activityResultLauncherWeapon.launch(intent);

    }

    private void SetModelStats(ViewGroup parentView, Model model, ModelIdentifier modelId)
    {
        TableLayout tableLayout = parentView.findViewById(R.id.layoutStatsen);

        UIIdentifier uiId = new UIIdentifier(WidgetType.ModelModifierLayout, modelId);
        ImageButton button = (ImageButton)parentView.findViewById(R.id.EditModelStatsButton);
        CreateModifiers(model,uiId, button, tableLayout);

        TableLayout weaponLayout = parentView.findViewWithTag("WeaponLayout");

        UIIdentifier uiIdWeapon = new UIIdentifier(WidgetType.WeaponLayoutModel,modelId);
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
        abilityText.setText(""+weapon.GetAbilities().size());
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



    @Override
    public void onBackPressed() {

        editModifierPopup.ClosePopup();

        super.onBackPressed();
    }
}
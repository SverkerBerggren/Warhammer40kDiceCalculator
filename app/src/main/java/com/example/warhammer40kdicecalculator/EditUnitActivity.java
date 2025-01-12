package com.example.warhammer40kdicecalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AttackAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UIIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

import java.util.ArrayList;

public class EditUnitActivity extends AppCompatActivity implements AbilityUIHolder{


    private Matchup matchup;
    private Context context;

    private ConstraintLayout highestConstraint;
    private CompareActivity compareActivity;

    private ActivityResultLauncher<Intent>    activityResultLauncherWeapon;
    private ActivityResultLauncher<Intent> activityResultLauncherAbility;

    private UnitIdentifier unitIdentifier;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_unit);

        context = getBaseContext();
        Intent intent = getIntent();
        highestConstraint = findViewById(R.id.ConstraintLayoutEditUnit);
        matchup = FileHandler.GetInstance().getMatchup(intent.getStringExtra("matchup"));

        unitIdentifier = new UnitIdentifier(intent.getStringExtra(""+R.string.UNIT_IDENTIFIER));


        LayoutInflater inflater = getLayoutInflater();

        Army army;

        if(unitIdentifier.allegiance.equals("friendly"))
        {
            army = matchup.friendlyArmy;
        }
        else
        {
            army = matchup.enemyArmy;
        }
        compareActivity = new CompareActivity();
        compareActivity.highestConstraint = highestConstraint;
        compareActivity.Setup(context,matchup);
        compareActivity.SetInflater(inflater);
        compareActivity.SetShouldStartActivityFromEditUnitContext(true);
        compareActivity.SetEditUnitActivity(this);

        ViewGroup verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.LinearLayoutEditUnit)));
        compareActivity.instaniateUnitButton(verticalLayout.getChildAt(verticalLayout.getChildCount()-1),army.units.get(unitIdentifier.index),unitIdentifier);
        compareActivity.CreateUnitAbilites(army.units.get(unitIdentifier.index),(LinearLayout) verticalLayout,inflater, unitIdentifier);
        TableRow tableRow = (TableRow)findViewById(R.id.TableRowUnitModifiers);
        ImageButton button = (ImageButton)findViewById(R.id.EditUnitModifierButton);
        UIIdentifier uiId = new UIIdentifier(compareActivity.UI_UNIT_MODIFIER_LAYOUT, unitIdentifier);

        compareActivity.CreateModifiers(army.units.get(unitIdentifier.index), uiId, tableRow, button);
        compareActivity.CreateModel(highestConstraint,army.units.get(unitIdentifier.index),unitIdentifier.index,unitIdentifier.allegiance,inflater);




        Button topButton = findViewById(R.id.UnitTopButton);



        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compareActivity.DropDownClick(view);
            }
        });
        findViewById(R.id.modelsTopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compareActivity.DropDownClick(view);

            }
        });
        findViewById(R.id.abilitiesTopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compareActivity.DropDownClick(view);

            }
        });



        activityResultLauncherWeapon = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new OnActivityResultEditUnitWeapon() );

        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new OnActivityResultEditUnitAbilities());


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }


        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        topButton.callOnClick();
    }

    public void Setup(Context context, Matchup matchup)
    {

        this.context = context;
        this.matchup = matchup;
        activityResultLauncherWeapon = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new OnActivityResultEditUnitWeapon() );

        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new OnActivityResultEditUnitAbilities());

    }

    @Override
    public void AbilityAdded(Ability ability, AbilityHolder abilityHolder) {

    }

    public void ClosePopup(View v)
    {
        View popup = highestConstraint.findViewById(R.id.ConstraintLayoutModelPopup);
        popup.setVisibility(View.GONE);
    //    popupActive = false;
    }

    public void DropDownClick(View v)
    {
        ViewGroup constraintLayout = (ViewGroup) v.getParent();



        ViewGroup viewGroup = (ViewGroup) constraintLayout;

        for(int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View childView = viewGroup.getChildAt(i);

            if(childView.getTag() != null && childView.getTag().equals("dontDropDown"))
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent data = new Intent();

        setResult(RESULT_OK,data);

        finish();
    }

    public class OnActivityResultEditUnitWeapon implements ActivityResultCallback<ActivityResult>
    {

        @Override
        public void onActivityResult(ActivityResult result) {

            int resultCode = result.getResultCode();
            Intent data = result.getData();

            ModelIdentifier modelId =  new ModelIdentifier(data.getStringExtra(""+R.string.MODEL_IDENTIFIER));

            matchup = FileHandler.GetInstance().getMatchup(matchup.name);

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
    public class OnActivityResultEditUnitAbilities implements ActivityResultCallback<ActivityResult>
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

    public void StartEditAbilites(View view, UIIdentifier uiId)
    {
        Intent intent = new Intent(context, Activity_Edit_Abilities.class);
        // Identifier
        if(uiId.id instanceof ModelIdentifier)
        {
            ModelIdentifier identifier = (ModelIdentifier)view.getTag(R.string.MODEL_IDENTIFIER);
            intent.putExtra("" + R.string.MODEL_IDENTIFIER, identifier.toString());
            intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "model");
        }
        else
        {
            UnitIdentifier identifier = (UnitIdentifier)view.getTag(R.string.UNIT_IDENTIFIER);
            intent.putExtra("" + R.string.UNIT_IDENTIFIER, identifier.toString());
            intent.putExtra(""+R.string.TYPE_OF_IDENTIFIER, "unit");
        }

        intent.putExtra(""+R.string.UI_IDENTIFIER, uiId.elementName);

        intent.putExtra("matchupName", matchup.name);



        activityResultLauncherAbility.launch(intent);
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
            tableLayout.addView(compareActivity.CreateTableRow(abilitiesToAdd.get(i)));
        }
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
}

package com.app.DamageCalculator40k.Activities;

import static com.app.DamageCalculator40k.UI.UiUtils.CreateAbilityTableRow;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Trace;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityUIHolder;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.Enums.StatModifier;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Identifiers.Allegiance;
import com.app.DamageCalculator40k.Identifiers.ArmyIdentifier;
import com.app.DamageCalculator40k.Identifiers.Identifier;
import com.app.DamageCalculator40k.Identifiers.IdentifierUtils;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;
import com.app.DamageCalculator40k.Identifiers.UnitIdentifier;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;
import com.app.DamageCalculator40k.UI.EditModifierPopup;
import com.app.DamageCalculator40k.UI.ModifierEditing;
import com.app.DamageCalculator40k.UI.UiUtils;
import com.app.DamageCalculator40k.UI.WidgetType;

import java.util.ArrayList;

public class CompareActivity extends AppCompatActivity implements AbilityUIHolder, ModifierEditing {

    private Matchup matchup;
    private LayoutInflater inflater;
    private EditModifierPopup editModifierPopup;
    private ViewGroup highestConstraint;
    private Context context;
    private ActivityResultLauncher<Intent> activityResultLauncherAbility;

    @Override
    public void UpdateAddedAndRemovedAbilities(ArrayList<String> abilitiesToAdd, ArrayList<String> abilitiesToRemove, UIIdentifier uiId)
    {
        UiUtils.UpdateAbilityRow(abilitiesToAdd,abilitiesToRemove,uiId,context,highestConstraint);
    }

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
        highestConstraint = findViewById(R.id.ConstraintLayoutCompare);

        activityResultLauncherAbility = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new UiUtils.UpdateUiActivityCallbackAbilities(this));


        editModifierPopup = new EditModifierPopup(highestConstraint,inflater,matchup, context, this);
        createArmy(findViewById(R.id.VerticalLayoutArmies),matchup.friendlyArmy, Allegiance.friendly);
        createArmy(findViewById(R.id.VerticalLayoutArmies),matchup.enemyArmy,Allegiance.enemy);
    }


    @Override
    public void AbilityAdded(Ability abilityEnum, GamePiece gamePiece) {

        if(gamePiece instanceof Model)
        {
           // ModelIdentifier mode
        }

    }
    private ArrayList<Unit> GetMatchupUnitsFromAllegiance(Allegiance allegiance)
    {
        return (allegiance.equals(Allegiance.friendly) ? (matchup.friendlyArmy.units):(matchup.enemyArmy.units));
    }

    public void DropDownClick(View view)
    {
        UiUtils.DropDownClick(view);
    }

    //TODO: Hela skiten med att ha friendly ar cap. Borde bort men manana

    private void createArmy(ViewGroup parentView, Army army, Allegiance allegiance)
    {
        View inflatedView = inflater.inflate(R.layout.army_layout,parentView,false);


        ArmyIdentifier armyId = new ArmyIdentifier(allegiance, matchup.name);
        UIIdentifier uiIdArmy = new UIIdentifier(WidgetType.ArmyModifierLayout, armyId);
        TableLayout modifiersTable = inflatedView.findViewById(R.id.ArmyModifiers);
        ImageButton editButton = inflatedView.findViewById(R.id.EditArmyModifiersButton);
        UiUtils.CreateModifiers(army, uiIdArmy, editButton, modifiersTable,context,editModifierPopup);
        UiUtils.CreateGamePieceAbilities(inflatedView,army,armyId,context,activityResultLauncherAbility);
        // instantiateArmies(allegiance);
        CreateUnitButton(allegiance, inflatedView);
        parentView.addView(inflatedView);
    }

    private void CreateUnitButton(Allegiance allegiance, View parentView)
    {
        ArrayList<Unit> units = GetMatchupUnitsFromAllegiance(allegiance);
        ContextThemeWrapper buttonTheme = new ContextThemeWrapper(context,R.style.UnitButton);
        ViewGroup layout = parentView.findViewById(R.id.ArmyLinearLayout);
        for(int i = 0; i < units.size();i++)
        {
            UnitIdentifier unitIdentifier = new UnitIdentifier(allegiance,null,i,matchup.name);
            Button unitButton = new Button(buttonTheme);
            unitButton.setText(units.get(i).unitName);
            unitButton.setOnClickListener(button -> {
                Intent intent = new Intent(this,EditUnitActivity.class);
                EditUnitActivity.FillIntent(intent,unitIdentifier);
                startActivity(intent);
            });
            unitButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(unitButton );
        }
    }
    public void OpenUnitSelection(View v)
    {
        Intent intenten = new Intent(this, UnitSelection.class);

        intenten.putExtra("SourceFile",matchup.name);

        startActivity(intenten);
    }
}
package com.app.DamageCalculator40k.Activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityUIHolder;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.DatabaseManager;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Identifiers.ArmyIdentifier;
import com.app.DamageCalculator40k.Identifiers.Identifier;
import com.app.DamageCalculator40k.Identifiers.IdentifierUtils;
import com.app.DamageCalculator40k.Identifiers.ModelIdentifier;
import com.app.DamageCalculator40k.Identifiers.UnitIdentifier;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;

import java.util.ArrayList;

public class Activity_Edit_Abilities extends AppCompatActivity implements AbilityUIHolder {
    private Context context;
    private Matchup matchup;
    private Identifier identifier;
    private String uiElement;
    private TableLayout tableLayoutAbilities;
    private final AbilityBitField abilitiesAdded = new AbilityBitField(AbilityEnum.ReRollOnes);
    private final AbilityBitField abilitiesRemoved = new AbilityBitField(AbilityEnum.ReRollOnes);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_abilities);

        Intent intent = getIntent();
        context = getBaseContext();

        ImageButton addAbilityButton = findViewById(R.id.EditAbilitiesAdd);
        uiElement =  intent.getStringExtra(""+R.string.UI_IDENTIFIER);
        tableLayoutAbilities = findViewById(R.id.TableLayoutEditAbilities);

        identifier = IdentifierUtils.GetIdentifierFromExtra(intent);
        matchup = FileHandler.GetInstance().getMatchup(identifier.GetMatchupName());
        GamePiece gamePiece = matchup.GetGamePiece(identifier);

        for(AbilityEnum ability : gamePiece.GetAbilityBitField())
        {
            CreateButton(ability, gamePiece);
        }
        MainActivity mainActivity = new MainActivity();
        Activity_Edit_Abilities editAbilityActivity = this;
        addAbilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.SearchAbility(gamePiece,matchup,findViewById(R.id.ConstraintLayoutEditAbilities),context,editAbilityActivity);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void CreateButton(AbilityEnum abilityEnum, GamePiece gamePiece)
    {
        Ability ability = DatabaseManager.getInstance().GetAbility(abilityEnum);

        TableRow tableRow = new TableRow(context);
        CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));
        checkBox.setTextSize(20);
        ImageButton imageButton = new ImageButton(context);

        checkBox.setChecked(gamePiece.IsActive(abilityEnum));
        checkBox.setText(ability.name);
        checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        CompareActivity compareActivity = new CompareActivity();
        compareActivity.Setup(context,matchup);
        checkBox.setOnClickListener(compareActivity. new OnClickDeactivateAbility(gamePiece.GetAbilityBitField(), abilityEnum));
        imageButton.setImageResource(com.google.android.material.R.drawable.abc_ic_menu_cut_mtrl_alpha);

        imageButton.setOnClickListener(new OnClickRemoveAbility(gamePiece,abilityEnum));

        tableRow.addView(checkBox);
        tableRow.addView(imageButton);

        tableLayoutAbilities.addView(tableRow,0);
    }

    private class OnClickRemoveAbility implements View.OnClickListener
    {
        private AbilityEnum abilityEnum;
        private GamePiece gamePiece;

        public OnClickRemoveAbility(GamePiece gamePiece, AbilityEnum ability)
        {
            this.gamePiece = gamePiece;
            this.abilityEnum = ability;
        }

        @Override
        public void onClick(View view) {

            gamePiece.GetAbilityBitField().Set(abilityEnum,false);

            FileHandler.GetInstance().saveMatchup(matchup);

            TableLayout viewParent = (TableLayout)view.getParent().getParent();

            viewParent.removeView((TableRow)view.getParent());

            abilitiesAdded.Set(abilityEnum,false);
            abilitiesRemoved.Set(abilityEnum,true);
        }
    }
    @Override
    public void AbilityAdded(AbilityEnum ability, GamePiece gamePiece) {

        abilitiesAdded.Set(ability,true);
        abilitiesRemoved.Set(ability,false);
        CreateButton(ability, gamePiece);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent data = new Intent();

        ArrayList<String> removedArray = new ArrayList<>();
        for(AbilityEnum abilityEnum : abilitiesRemoved)
        {
            removedArray.add(DatabaseManager.getInstance().GetAbility(abilityEnum).name);
        }
        data.putStringArrayListExtra("abilitiesRemoved" , removedArray);

        ArrayList<String> addedArray = new ArrayList<>();
        for(AbilityEnum abilityEnum :  abilitiesAdded)
        {
            addedArray.add(DatabaseManager.getInstance().GetAbility(abilityEnum).name);
        }
        data.putStringArrayListExtra("abilitiesAdded" , addedArray);

        data.putExtra(""+R.string.UI_IDENTIFIER, uiElement);

        // TODO: Ta bort skiten
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
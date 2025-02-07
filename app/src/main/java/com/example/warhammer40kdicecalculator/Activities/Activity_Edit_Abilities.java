package com.example.warhammer40kdicecalculator.Activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.AbilityUIHolder;
import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.DatabaseManager;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.FileHandling.FileHandler;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.IdentifierUtils;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;
import com.example.warhammer40kdicecalculator.Matchup;
import com.example.warhammer40kdicecalculator.R;

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

        String matchupName = intent.getStringExtra("matchupName");

        ImageButton addAbilityButton = findViewById(R.id.EditAbilitiesAdd);
        matchup = FileHandler.GetInstance().getMatchup(matchupName);
        uiElement =  intent.getStringExtra(""+R.string.UI_IDENTIFIER);
        tableLayoutAbilities = findViewById(R.id.TableLayoutEditAbilities);

        identifier = IdentifierUtils.GetIdentifierFromExtra(intent);
        AbilityHolder abilityHolder = matchup.GetAbilityHolder(identifier);

        for(AbilityEnum ability : abilityHolder.GetAbilityBitField())
        {
            CreateButton(ability,abilityHolder);
        }
        MainActivity mainActivity = new MainActivity();
        Activity_Edit_Abilities editAbilityActivity = this;
        addAbilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.SearchAbility(abilityHolder,matchup,findViewById(R.id.ConstraintLayoutEditAbilities),context,editAbilityActivity);
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

    private void CreateButton(AbilityEnum abilityEnum, AbilityHolder abilityHolder)
    {
        Ability ability = DatabaseManager.getInstance().GetAbility(abilityEnum);

        TableRow tableRow = new TableRow(context);
        CheckBox checkBox = new CheckBox(new ContextThemeWrapper(this, androidx.appcompat.R.style.Base_Widget_AppCompat_CompoundButton_CheckBox));
        checkBox.setTextSize(20);
        ImageButton imageButton = new ImageButton(context);

        checkBox.setChecked(abilityHolder.IsActive(abilityEnum));
        checkBox.setText(ability.name);
        checkBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        CompareActivity compareActivity = new CompareActivity();
        compareActivity.Setup(context,matchup);
        checkBox.setOnClickListener(compareActivity. new OnClickDeactivateAbility(abilityHolder.GetAbilityBitField(), abilityEnum));
        imageButton.setImageResource(com.google.android.material.R.drawable.abc_ic_menu_cut_mtrl_alpha);

        imageButton.setOnClickListener(new OnClickRemoveAbility(abilityHolder,abilityEnum));

        tableRow.addView(checkBox);
        tableRow.addView(imageButton);

        tableLayoutAbilities.addView(tableRow,0);
    }

    private class OnClickRemoveAbility implements View.OnClickListener
    {
        private AbilityEnum abilityEnum;
        private AbilityHolder abilityHolder;

        public OnClickRemoveAbility(AbilityHolder abilityHolder,AbilityEnum ability)
        {
            this.abilityHolder = abilityHolder;
            this.abilityEnum = ability;
        }

        @Override
        public void onClick(View view) {

            abilityHolder.GetAbilityBitField().Set(abilityEnum,false);

            FileHandler.GetInstance().saveMatchup(matchup);

            TableLayout viewParent = (TableLayout)view.getParent().getParent();

            viewParent.removeView((TableRow)view.getParent());

            abilitiesAdded.Set(abilityEnum,false);
            abilitiesRemoved.Set(abilityEnum,true);
        }
    }
    @Override
    public void AbilityAdded(AbilityEnum ability, AbilityHolder abilityHolder) {

        abilitiesAdded.Set(ability,true);
        abilitiesRemoved.Set(ability,false);
        CreateButton(ability,abilityHolder);
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
package com.example.warhammer40kdicecalculator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;

import java.util.ArrayList;
import java.util.HashMap;

public class EditWeaponActivity extends AppCompatActivity implements AbilityUIHolder{
    private LayoutInflater inflater;
    private ImageButton abilityPopupButton;
    private boolean inflatedPopup= false;
    private EditText rawNumberOfAttacksView;
    private EditText attacksAmountD3View;
    private EditText attacksAmountD6View;
    private EditText rawDamageAmountView;
    private EditText damageAmountD3View;
    private EditText damageAmountD6View;
    private EditText strengthView;
    private EditText apView;

    private LinearLayout linearLayout;

    private  FileHandler fileHandler;
    private  Matchup matchup;
    private Context context;

    private EditWeaponActivity abilityHolder = this;


    private String uiElementName = "";
    private ModelIdentifier modelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weapon);

        Intent intent = getIntent();
        context = getBaseContext();
        inflater = getLayoutInflater();



        String matchupName = intent.getStringExtra("matchupName");

        modelId = new ModelIdentifier(intent.getStringExtra(""+R.string.MODEL_IDENTIFIER));

        uiElementName = intent.getStringExtra(""+R.string.UI_IDENTIFIER);

        fileHandler = new FileHandler(context);


        matchup = fileHandler.getMatchup(matchupName);

        ArrayList<RangedWeapon> weapons = new ArrayList<>();

        weapons = matchup.GetModel(modelId).listOfRangedWeapons;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.WeaponEditTableLayout);

        for (int i = 0; i < weapons.size(); i++) {
            Button weaponButton = new Button(context);

            weaponButton.setText(weapons.get(i).name);

            weaponButton.setOnClickListener(new OnClickListenerWeapon(weapons.get(i),context));

            TableRow tableRow = new TableRow(context);
            CheckBox checkBox = new CheckBox(context);
            checkBox.setChecked(weapons.get(i).active);
            CompareActivity compareActivity = new CompareActivity();
            checkBox.setOnClickListener(compareActivity. new OnClickDeactivate(weapons.get(i)));
            tableLayout.addView(tableRow);
            tableRow.addView(weaponButton);
            tableRow.addView(checkBox);

        }





        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void AbilityAdded(Ability ability, AbilityHolder abilityHolder) {
        PopulateAbilites((RangedWeapon) abilityHolder);
    }

    private class OnClickListenerWeapon implements View.OnClickListener {
        RangedWeapon weapon;

        Context context;


        public OnClickListenerWeapon(RangedWeapon weapon,Context context) {
            this.weapon = weapon;
            this.context = context;
        }

        public void onClick(View v) {

            if(!inflatedPopup)
            {
                inflater.inflate(R.layout.popup_weapon, findViewById(R.id.WeaponConstraintLayout));
                ((Button)findViewById(R.id.SaveButtonWeapon)).setOnClickListener(new OnClickListenerSaveWeapon(weapon));
                inflatedPopup = true;

                rawNumberOfAttacksView =    ((EditText) findViewById(R.id.AttacksRawAmountWeaponPopup));
                attacksAmountD3View =    ((EditText) findViewById(R.id.AttacksAmountD3WeaponPopup));
                attacksAmountD6View =    ((EditText) findViewById(R.id.AttacksAmountD6WeaponPopup));
                rawDamageAmountView =    ((EditText) findViewById(R.id.RawDamagePopup));
                damageAmountD3View =    ((EditText) findViewById(R.id.DamageAmountD3Popup));
                damageAmountD6View =    ((EditText) findViewById(R.id.DamageAmountD6Popup));
                strengthView =    ((EditText) findViewById(R.id.StrengthPopup));
                apView =    ((EditText) findViewById(R.id.ApPopup));

                linearLayout = ((LinearLayout)findViewById(R.id.LinearLayoutAbilityWeapon) );

                abilityPopupButton = findViewById(R.id.SearchPopupWeapon);
                abilityPopupButton.setOnClickListener(new AbilitySearchPopupWeapon(weapon,context));
            }
            else
            {
                ShowPopup(v);
            }



            rawNumberOfAttacksView.setText(""+weapon.amountOfAttacks.rawNumberOfAttacks);
            attacksAmountD3View.setText(""+weapon.amountOfAttacks.numberOfD3);
            attacksAmountD6View.setText(""+weapon.amountOfAttacks.numberOfD6);
            rawDamageAmountView.setText(""+weapon.damageAmount.rawDamageAmount);
            damageAmountD3View.setText(""+weapon.damageAmount.d3DamageAmount);
            damageAmountD6View.setText(""+weapon.damageAmount.d6DamageAmount);
            strengthView.setText(""+weapon.strength);
            apView.setText(""+weapon.ap);
//
            PopulateAbilites(weapon);
        }

    }

    private void PopulateAbilites( RangedWeapon weapon)
    {   linearLayout.removeAllViews();
        for(Ability ability : weapon.weaponRules)
        {

            CheckBox abilityCheckBox = new CheckBox(context);
            abilityCheckBox.setText(ability.name);

            linearLayout.addView(abilityCheckBox);
        }
    }

    private class OnClickListenerSaveWeapon implements View.OnClickListener
    {

        private RangedWeapon weapon;

        public OnClickListenerSaveWeapon(RangedWeapon weapon)
        {
            this.weapon = weapon;
        }


        @Override
        public void onClick(View view) {

            weapon.amountOfAttacks.rawNumberOfAttacks= Integer.parseInt( rawNumberOfAttacksView.getText().toString());
            weapon.amountOfAttacks.numberOfD3 =  Integer.parseInt( attacksAmountD3View.getText().toString());
            weapon.amountOfAttacks.numberOfD6 =  Integer.parseInt( attacksAmountD6View.getText().toString());
            weapon.damageAmount.rawDamageAmount = Integer.parseInt(rawDamageAmountView.getText().toString());
            weapon.damageAmount.d3DamageAmount = Integer.parseInt(damageAmountD3View.getText().toString());
            weapon.damageAmount.d6DamageAmount = Integer.parseInt(damageAmountD6View.getText().toString());
            weapon.strength = Integer.parseInt(strengthView.getText().toString());
            weapon.ap = Integer.parseInt(apView.getText().toString());


            fileHandler.saveMatchup(matchup);

        }
    }

    HashMap<String, Ability> map = new HashMap<String, Ability>();

    public void ShowPopup(View v) {
        View popup = findViewById(R.id.ConstraintLayoutPopup);
        popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v) {
        View popup = findViewById(R.id.ConstraintLayoutPopup);
        popup.setVisibility(View.GONE);
    }


    public void ShowSearch(View v) {
        if (findViewById(R.id.SearchLayout) == null) {
            LayoutInflater inf = getLayoutInflater();
            ViewGroup constraintLayout = findViewById(R.id.ConstraintLayout);
            inf.inflate(R.layout.search_prefab, constraintLayout);
        } else {
            View searchLayout = findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.VISIBLE);
        }

        SearchView searchView = findViewById(R.id.searchView);
        ListView listView = findViewById(R.id.listView);

        ArrayList<String> searchList = new ArrayList<String>() {{
            add("Ability1");
            add("Deal More Damage");
            add("Take More Damage");
        }};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) parent.getItemAtPosition(position);

                Ability ability = map.get(item);

                View searchLayout = findViewById(R.id.SearchLayout);
                searchLayout.setVisibility(View.GONE);


            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }


        });

    }
    private class AbilitySearchPopupWeapon implements View.OnClickListener
    {
        private RangedWeapon weapon;
        private Context context;
        public AbilitySearchPopupWeapon(RangedWeapon weapon, Context context)
        {
            this.weapon = weapon;
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            MainActivity mainActivity = new MainActivity();

            mainActivity.SearchAbility(weapon, matchup, findViewById(R.id.WeaponConstraintLayout), context,abilityHolder);
        }
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();

        Intent data = new Intent();
    //    data.putExtra("abilitiesRemoved", abilitiesRemoved.size());



        data.putExtra(""+R.string.MODEL_IDENTIFIER, modelId.toString());
        data.putExtra(""+R.string.UI_IDENTIFIER, uiElementName);


        setResult(RESULT_OK,data);
        finish();

    }
}
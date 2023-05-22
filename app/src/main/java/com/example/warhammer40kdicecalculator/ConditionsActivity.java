package com.example.warhammer40kdicecalculator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class ConditionsActivity extends AppCompatActivity {


    private LinearLayout linearLayout;
    private Context context;
    private Conditions conditions = new Conditions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        linearLayout = findViewById(R.id.ConditionLinearLayout);

        context = getBaseContext();

        if(getIntent().getStringExtra(""+R.string.CONDITIONS) != null)
        {
            conditions = new Conditions( getIntent().getStringExtra(""+R.string.CONDITIONS));
        }

        CreateConditionCheckboxes(conditions);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    private void CreateConditionCheckboxes(Conditions condition)
    {

        CreateCheckBox("rapidFireRange",condition.rapidFireRange);
        CreateCheckBox("Resolve melee attacks",condition.meleeCombat);
        CreateCheckBox("Resolve ranged attacks",condition.rangedCombat);
        CreateCheckBox("Medium blast",condition.mediumBlast);
        CreateCheckBox("Full blast",condition.fullBlast);
        CreateCheckBox("Tactical doctrine",condition.tacticalDoctrine);
        CreateCheckBox("Devastator doctrine",condition.devastatorDoctrine);
        CreateCheckBox("Assault doctrine",condition.assaultDoctrine);
        CreateCheckBox("Plus one to wound",condition.plusOneToWound);
        CreateCheckBox("Dakka half range",condition.dakkaHalfRange);
    }

    private void CreateCheckBox(String tag, Boolean active)
    {
        CheckBox checkBox = new CheckBox(context);

        if(tag.equals("rapidFireRange"))
        {
            checkBox.setTag("rapidFireRange");
            checkBox.setText("In rapid fire range");
            checkBox.setChecked(active);
        }
        if(tag.equals("Resolve melee attacks"))
        {
            checkBox.setTag("Resolve melee attacks");
            checkBox.setText("Resolve melee attacks");
            checkBox.setChecked(active);
        }
        if(tag.equals("Resolve ranged attacks"))
        {
            checkBox.setTag("Resolve ranged attacks");
            checkBox.setText("Resolve ranged attacks");
            checkBox.setChecked(active);
        }
        if(tag.equals("Medium blast"))
        {
            checkBox.setTag("Medium blast");
            checkBox.setText("Medium blast");
            checkBox.setChecked(active);
        }
        if(tag.equals("Full blast"))
        {
            checkBox.setTag("Full blast");
            checkBox.setText("Full blast");
            checkBox.setChecked(active);
        }
        if(tag.equals("Tactical doctrine"))
        {
            checkBox.setTag("Tactical doctrine");
            checkBox.setText("Tactical doctrine");
            checkBox.setChecked(active);
        }
        if(tag.equals("Devastator doctrine"))
        {
            checkBox.setTag("Devastator doctrine");
            checkBox.setText("Devastator doctrine");
            checkBox.setChecked(active);
        }
        if(tag.equals("Assault doctrine"))
        {
            checkBox.setTag("Assault doctrine");
            checkBox.setText("Assault doctrine");
            checkBox.setChecked(active);
        }
        if(tag.equals("Plus one to wound"))
        {
            checkBox.setTag("Plus one to wound");
            checkBox.setText("Plus one to wound");
            checkBox.setChecked(active);
        }
        if(tag.equals("Dakka half range"))
        {
            checkBox.setTag("Dakka half range");
            checkBox.setText("Dakka half range");
            checkBox.setChecked(active);
        }
        checkBox.setOnCheckedChangeListener(new OnCheckBoxChange());

        linearLayout.addView(checkBox);
    }

    private class OnCheckBoxChange implements CheckBox.OnCheckedChangeListener
    {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            String tag = (String) compoundButton.getTag();

            if(tag.equals("rapidFireRange"))
                conditions.rapidFireRange = b;
            if(tag.equals("Resolve melee attacks"))
                conditions.meleeCombat = b;
            if(tag.equals("Resolve ranged attacks"))
                conditions.rangedCombat = b;
            if(tag.equals("Medium blast"))
                conditions.mediumBlast = b;
            if(tag.equals("Full blast"))
                conditions.fullBlast = b;
            if(tag.equals("Tactical doctrine"))
                conditions.tacticalDoctrine = b;
            if(tag.equals("Devastator doctrine"))
                conditions.devastatorDoctrine = b;
            if(tag.equals("Assault doctrine"))
                conditions.assaultDoctrine = b;
            if(tag.equals("Plus one to wound"))
                conditions.plusOneToWound = b;
            if(tag.equals("Dakka half range"))
                conditions.dakkaHalfRange = b;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent data = new Intent();

        data.putExtra(""+R.string.CONDITIONS, conditions.toString());

        setResult(RESULT_OK,data);

        finish();

    }
}
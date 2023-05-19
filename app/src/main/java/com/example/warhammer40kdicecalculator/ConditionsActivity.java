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
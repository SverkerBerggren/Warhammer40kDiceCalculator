package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.HashMap;

public class CompareActivity extends AppCompatActivity {

    private  FileHandler  fileHandler;
    private Matchup matchup;

    private int UNIT_ALLEGIANCE = R.string.UNIT_ALLEGIANCE;
    private int UNIT_MODIFIER = R.string.UNIT_MODIFIER;
    private int UNIT_NUMBER = R.string.UNIT_NUMBER;

    private String DECREASE_WEAPONSKILL = "decreaseWeaponSkill";
    private String INCREASE_WEAPONSKILL = "increaseWeaponSkill";


    private String FRIENDLY = "friendly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        LayoutInflater inflater =  getLayoutInflater();


        fileHandler = new FileHandler(getBaseContext());

        //Log.d("testar intetion", "onCreate: " + getIntent().getStringExtra("SourceFile"));

        matchup = fileHandler.getMatchup( getIntent().getStringExtra("SourceFile"));


        createArmies(matchup,inflater);


    //    Log.d("vad är texten " + )



      //  View viewToModify = inflater.inflate(R.layout.unitviewprefab, findViewById(R.id.VerticalLayout));

        //View viewIgen =  viewToModify.findViewById(R.id.TestId);











    }

    private HashMap<String, DataSheet> datasheetMap = new HashMap<>();


    private void createArmies(Matchup matchup, LayoutInflater inflater)
    {
        ViewGroup verticalLayout = null;
        for(int i = 0; i < matchup.friendlyArmy.units.size();i++)
        {




            verticalLayout = (ViewGroup) inflater.inflate(R.layout.unitviewprefab, ((ViewGroup)findViewById(R.id.VerticalLayout)));


            instaniateUnitButton(verticalLayout.getChildAt(i +1),matchup.friendlyArmy.units.get(i),i, FRIENDLY);
            //Log.d("grejer",""+viewToModify.getParent().toString());



        }



        Log.d("längd", ""+matchup.friendlyArmy.units.size());


    }

    private  void instaniateUnitButton(View buttonToModify, Unit unit, int unitNumber, String friendlyOrEnemy)
    {
        Button topButton = (Button)buttonToModify.findViewById(R.id.UnitTopButton);



        topButton.setText(unit.unitName);

      //  topButton.setId(R.id.noId);




        ImageButton decreaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.DecreaseWeaponSkill));
        ImageButton increaseWeaponSkill = ((ImageButton)buttonToModify.findViewById(R.id.IncreaseWeaponSkill));
       // TextView weaponSkillIndicator = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicator);

       // weaponSkillIndicator.setTag(UNIT_ALLEGIANCE,friendlyOrEnemy);
       // weaponSkillIndicator.setTag(UNIT_NUMBER,unitNumber);
       // TextView hej = (TextView)buttonToModify.findViewById(R.id.WeaponSkillIndicatorr);

        int test = R.id.WeaponSkillIndicatorr;

        decreaseWeaponSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        decreaseWeaponSkill.setTag(UNIT_MODIFIER, DECREASE_WEAPONSKILL);
        decreaseWeaponSkill.setTag(UNIT_NUMBER, unitNumber);

        increaseWeaponSkill.setTag(UNIT_ALLEGIANCE, friendlyOrEnemy);
        increaseWeaponSkill.setTag(UNIT_MODIFIER, INCREASE_WEAPONSKILL);
        increaseWeaponSkill.setTag(UNIT_NUMBER, unitNumber);





        decreaseWeaponSkill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeUnitModifer(v);
            }
        });

        increaseWeaponSkill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeUnitModifer(v);
            }
        });





    }

    private void ChangeUnitModifer(View buttonClicked)
    {
        if(((String)buttonClicked.getTag(UNIT_ALLEGIANCE)).equals("friendly"))
        {
            //matchup.friendlyArmy.units.get( ((int)buttonClicked.getTag(UNIT_NUMBER)) );

            if(buttonClicked.getTag(UNIT_MODIFIER).equals(DECREASE_WEAPONSKILL))
            {
                matchup.friendlyArmy.units.get(((int)buttonClicked.getTag(UNIT_NUMBER)) ).weaponSkillModifier -=1;

                ViewGroup parent = (ViewGroup) buttonClicked.getParent();

                TextView textView = parent.findViewById(R.id.WeaponSkillIndicatorr);
                textView.setText(matchup.friendlyArmy.units.get(((int)buttonClicked.getTag(UNIT_NUMBER)) ).weaponSkillModifier);
                //((TextView)parent.findViewById(R.id.WeaponSkillIndicator)).setText(matchup.friendlyArmy.units.get(((int)buttonClicked.getTag(UNIT_NUMBER)) ).weaponSkillModifier);
                //buttonClicked.getParent().

               Log.d("unit knappar","unit name " +matchup.friendlyArmy.units.get((int)buttonClicked.getTag(UNIT_NUMBER)).unitName);
            }
        }
        else
        {

        }
    }



    public void DropDownClick(View v)
    {
        Context context = v.getContext();


        ConstraintLayout constraintLayout = (ConstraintLayout) v.getParent();

        Log.d("Ui grejer", "Vad är höjden innan" +  constraintLayout.getMeasuredHeight());


        ViewGroup viewGroup = (ViewGroup) constraintLayout;

        for(int i = 0; i < viewGroup.getChildCount(); i++)
        {
            if(viewGroup.getChildAt(i) != v)
            {
                if(viewGroup.getChildAt(i).getVisibility() == View.VISIBLE)
                {
                    viewGroup.getChildAt(i).setVisibility(View.GONE);
                }
                else
                {
                    viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }

        Log.d("Ui grejer", "Vad är höjden efter" +  constraintLayout.getMeasuredHeight());



        Log.d("Ui grejer", "" +  v.getParent());
    }
}
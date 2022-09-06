package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Visibility;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

    }


    public void printTest(View v)
    {
        Log.d("aboo", "printTest: ");
        TestLasaCsv readCsvTest = new TestLasaCsv(context);
       // readCsvTest.testReadFile();
        ArrayList<ArrayList<String>> dataSheetData = readCsvTest.ReadCsvFile("Datasheets.csv");
        Log.d("Csv tester", "" + dataSheetData.get(5).get(1));

        RollingLogic hej = new RollingLogic();
        getAssets();
        hej.TestOmFungerar();
        DataSheet conscript = new DataSheet(5,3,90,1,1,0,3,5,-1);
        DataSheet spaceMarine = new DataSheet(3,4,1,2,1,1,4,3,-1);
    //    conscript.hammerOfEmperor = true;
    //     hej.CalculateDamage(conscript,spaceMarine);

        RangedWeapon lasgun = new RangedWeapon(3,0,new DamageAmount(1,0,0),new RangedAttackAmount(1,0,0));

        Unit conscripts = new Unit();
        lasgun.ap = 0;
        Model guardsman = new Model();
        guardsman.ballisticSkill = 5;
        guardsman.listOfRangedWeapons.add(lasgun);

        conscripts.listOfAbilitys.add(new HammerOfTheEmperor());

        for(int i = 0; i < 100; i++)
        {
            conscripts.listOfModels.add(new Model(guardsman));
        }


        Unit spaceMarineIntercessorUnit = new Unit();
        Model intercessor = new Model();
        intercessor.wounds = 2;
        intercessor.armorSave = 3;
        intercessor.toughness = 4;

        for(int i =0; i <10; i ++)
        {
            spaceMarineIntercessorUnit.listOfModels.add(new Model(intercessor));
        }

        Unit LemanRuss = new Unit();
        Model lemanRussTurret = new Model();
        LemanRuss.listOfModels.add(lemanRussTurret);

        LemanRuss.listOfAbilitys.add(new HammerOfTheEmperor());

        lemanRussTurret.ballisticSkill = 4;

        RangedWeapon demolisherCannon = new RangedWeapon(10,-3, new DamageAmount(0,0,1),new RangedAttackAmount(0,0,1));

        lemanRussTurret.listOfRangedWeapons.add(demolisherCannon);
        lemanRussTurret.listOfRangedWeapons.add(demolisherCannon);
        Unit manticore = new Unit();
        Model manticoreHunterKillerMissile = new Model();
        manticoreHunterKillerMissile.ballisticSkill = 4;
        RangedWeapon hunterKillerMissile = new RangedWeapon(10,-2,new DamageAmount(3,0,0),new RangedAttackAmount(0,0,2));
        manticoreHunterKillerMissile.listOfRangedWeapons.add(hunterKillerMissile);
        manticore.listOfModels.add(manticoreHunterKillerMissile);

        manticore.listOfAbilitys.add(new HammerOfTheEmperor());

      //  hej.newCalculateDamage(conscripts,spaceMarineIntercessorUnit);

        lemanRussTurret.listOfAbilites.add(new HammerOfTheEmperor());
        lemanRussTurret.listOfAbilites.add(new ReRollAmountOfHits());

        hej.newCalculateDamage(LemanRuss,spaceMarineIntercessorUnit);

    }
}
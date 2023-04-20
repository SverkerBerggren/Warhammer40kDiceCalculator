package com.example.warhammer40kdicecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Function;

class Runnable_Function<FunctionArgument,FunctionReturnValue> implements Runnable
{
    Function<FunctionArgument,FunctionReturnValue> m_FunctionToRun = null;
    FunctionArgument m_ArgumentToUse = null;
    Runnable_Function(Function<FunctionArgument,FunctionReturnValue> FunctionToRun,FunctionArgument ArgumentToUse)
    {
        m_ArgumentToUse = ArgumentToUse;
        m_FunctionToRun = FunctionToRun;
    }
    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run()
    {
        m_FunctionToRun.apply(m_ArgumentToUse);
    }
}
class Callback_Runner <RunArgumentType,RunResultType,CallbackResultType> implements Runnable
{
    Context m_AssociatedContext = null;
    private Function<RunResultType,CallbackResultType> m_FunctionCallback = null;
    private Function<RunArgumentType,RunResultType> m_CodeToRun = null;
    private RunArgumentType m_RunArgument = null;
    Callback_Runner(Context AssociatedContext, Function<RunResultType,CallbackResultType> Callback, Function<RunArgumentType,RunResultType> CodeToRun, RunArgumentType RunArgument)
    {
        m_AssociatedContext = AssociatedContext;
        m_FunctionCallback = Callback;
        m_CodeToRun = CodeToRun;
        m_RunArgument = RunArgument;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run()
    {
        RunResultType RunReturnValue = m_CodeToRun.apply(m_RunArgument);
        new Handler(m_AssociatedContext.getMainLooper()).post(new Runnable_Function<RunResultType,CallbackResultType>(m_FunctionCallback,RunReturnValue));
    }
}

public class  MainActivity extends AppCompatActivity {





    private Context context;
    private ArrayList<Unit> firstPlayerArmy;
    private ArrayList<ArrayList<String>> parsedDatasheetList = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsedWeaponList = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsedModelList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();





        try {
            InputStream Input = this.getAssets().open("TestParing.txt");
            BattlescribeParser Parser = new BattlescribeParser();
            ArrayList<BattlescribeUnit> Models = Parser.ParseUnits(Input);

            int Hej2 = 0;
        }
        catch (Exception e)
        {
            String Error = e.getMessage();
            int hej = 2;
        }



        EXAMPLEUPDATE();


        TestLasaCsv csvParser = new TestLasaCsv(this.context);
        parsedDatasheetList =   csvParser.ReadCsvFile("Datasheets.csv");
        parsedWeaponList = csvParser.ReadCsvFile("Wargear_list.csv");
        parsedModelList = csvParser.ReadCsvFile("Datasheets_models.csv");

    }



    Integer p_UpdateCallback(String Result)
    {
        Toast.makeText(this,"Update result: "+Result,Toast.LENGTH_SHORT).show();
        return(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void EXAMPLEUPDATE()
    {
        WahapediaUpdate Updater = new WahapediaUpdate();
        UpdateArgumentStruct Arguments = new UpdateArgumentStruct();
        Arguments.URLPrefix = "https://wahapedia.ru/wh40k9ed/factions/aeldari/";
        Arguments.OutputPrefix = this.getDataDir().toString();
        Arguments.FilesToDownload.add("Asurmen");
        Arguments.FilesToDownload.add("Baharroth");

        Thread UploadDataThread = new Thread(new Callback_Runner<UpdateArgumentStruct,String,Integer>(this,this::p_UpdateCallback,Updater::UpdateFiles,Arguments));
        UploadDataThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void testNedladdning(View v)
    {
        WahapediaUpdate wahapediaUpdate = new WahapediaUpdate();
        UpdateArgumentStruct updateArgumentStruct = new UpdateArgumentStruct();

        updateArgumentStruct.FilesToDownload.add("Datasheets.csv");
        updateArgumentStruct.FilesToDownload.add("Wargear_list.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_models.csv");
        updateArgumentStruct.URLPrefix = "https://wahapedia.ru/wh40k9ed/";
        updateArgumentStruct.OutputPrefix = this.getDataDir().toString();

        Thread UploadDataThread = new Thread(new Callback_Runner<UpdateArgumentStruct,String,Integer>(this,this::p_UpdateCallback,wahapediaUpdate::UpdateFiles,updateArgumentStruct));
        UploadDataThread.start();


    }

    public void testParseDownloadedCsv(View v)
    {
        TestLasaCsv parser = new TestLasaCsv(v.getContext());


        ArrayList<ArrayList<String>> testLastDatasheets = new ArrayList<ArrayList<String>>();


        testLastDatasheets = parser.ReadCsvFile("Datasheets.csv");

        Log.d("lasa datasheets", "langden " + testLastDatasheets.size());

    }

    public ArrayList<Unit> armyFromBattleScribe(ArrayList<BattlescribeUnit> inputArmy)
    {
        ArrayList<Unit> armyToReturn = new ArrayList<>();



        for(int i =0; i < inputArmy.size(); i++)
        {
            Unit unitToAdd = new Unit();
            BattlescribeUnit battlescribeUnit = inputArmy.get(i);
            unitToAdd.unitName = battlescribeUnit.Name;




        }

        return null;
    }

    public  void testBattleScribeParse(View v)
    {
        BattlescribeParser parser = new BattlescribeParser();

        ArrayList<BattlescribeUnit> unitsInList = new ArrayList<>();




        try
        {
            //  ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();

            InputStreamReader input = new InputStreamReader(context.getAssets().open("TestParing.txt"));

           // InputStream inputStream = new InputStream();
            try {

                unitsInList = parser.ParseUnits(context.getAssets().open("TestParing.txt"));

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Log.d("parsa battle", "lyckades " +unitsInList.size());

            for(int i = 0; i < unitsInList.size(); i++)
            {
                Log.d("Unit list ", " " + unitsInList.get(i).Name);
            }

            Log.d("har uniten vapen " , " " + unitsInList.get(2).Attributes.size());
            for(int i = 0; i < unitsInList.get(7).Attributes.size(); i++)
            {
                Log.d("Vilka vapen har unitse", "Vapen  " + unitsInList.get(7).Attributes);
            }
            Log.d("Vilka vapen har unitse", "Vapen " + unitsInList.get(2).Name);
            for(int i = 0; i < unitsInList.get(2).Models.size(); i++)
            {
                for(int z = 0; z < unitsInList.get(2).Models.get(i).Weapons.size(); z++)
                {
                    Log.d("Vilka vapen har unitse", "Vilken model: " +unitsInList.get(2).Models.get(i).ModelName + " " + unitsInList.get(2).Models.get(i).Weapons.get(z));
                }
               // Log.d("Vilka vapen har unitse", "Vapen  " + unitsInList.get(7).Models);
            }



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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
        manticoreHunterKillerMissile.listOfAbilites.add(new HammerOfTheEmperor());
      //  hej.newCalculateDamage(conscripts,spaceMarineIntercessorUnit);
        manticore.listOfAbilitys.add(new ReRollAmountOfHits());
      //  manticoreHunterKillerMissile.listOfAbilites.add(new ReRollAmountOfHits());
        lemanRussTurret.listOfAbilites.add(new HammerOfTheEmperor());
        lemanRussTurret.listOfAbilites.add(new ReRollAmountOfHits());


        ArrayList<Unit> listToCompare = new ArrayList<>();

        listToCompare.add(manticore);
   //     manticoreHunterKillerMissile.listOfAbilites.add(new HammerOfTheEmperor());
        hej.newCalculateDamage(listToCompare,spaceMarineIntercessorUnit);

    }


    public void OpenSavedMatchups(View v)
    {
        Intent intent = new Intent(this, SavedMatchupsActivity.class);

        startActivity(intent);
    }
    public void OpenPopup(View v)
    {
        Intent intenten = new Intent(this,activity_popup.class);

        startActivity(intenten);
    }

    public void OpenWeaponPopup(View v)
    {
        Intent intenten = new Intent(this, Weapon_Popup.class);

        startActivity(intenten);
    }

    public void OpenUnitSelection(View v)
    {
        Intent intenten = new Intent(this, UnitSelection.class);

        startActivity(intenten);
    }

    public void CreateNewMatchup(View v)
    {

        ArrayList<Ability> listOfAbilities = new ArrayList<Ability>();
        listOfAbilities.add(new ReRollAmountOfHits());

        ArrayList<Model> listOfModels = new ArrayList<>();

        ArrayList<RangedWeapon> bolters = new ArrayList<>();

        RangedWeapon bolter = new RangedWeapon("Bolter",4,0,new DamageAmount(1,0,0), new RangedAttackAmount(2,0,0),listOfAbilities);

        bolters.add(bolter);
        bolters.add(bolter);

        ArrayList<MeleeWeapons> meleeWeapons = new ArrayList<>();
        meleeWeapons.add(new MeleeWeapons());

        Model intercessor = new Model("Intercessor",4,4,4,0,2,3,3,2,listOfAbilities,bolters,meleeWeapons);
        intercessor.listOfAbilites.add(new ReRollAmountOfHits());
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);
        listOfModels.add(intercessor);

        Unit blackTemplar = new Unit("Ny Blacktemplar",100,0,0,0,0,0,0,0,0,listOfModels,listOfAbilities);

        Model guardsman = new Model("Guardsman",3,3,5,0,1,4,4,1,listOfAbilities,bolters,meleeWeapons);

        ArrayList<Model> guardsmen = new ArrayList<>();

        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);
        guardsmen.add(guardsman);

        ArrayList<Ability> abilitiesGuard = new ArrayList<>();

        abilitiesGuard.add(new HammerOfTheEmperor());
        abilitiesGuard.add(new HammerOfTheEmperor());

        guardsman.listOfAbilites.add(new HammerOfTheEmperor());
        guardsman.listOfAbilites.add(new HammerOfTheEmperor());

        Unit infantrySquad = new Unit("Cadian infantry squad",50,0,0,0,0,0,0,0,0,guardsmen,abilitiesGuard);


        ArrayList<Unit> listOfFriendlyUnits = new ArrayList<>();

        listOfFriendlyUnits.add(blackTemplar);
        listOfFriendlyUnits.add(infantrySquad);
        listOfFriendlyUnits.add(blackTemplar);

        ArrayList<Unit> listOfEnemyUnits = new ArrayList<>();

        listOfEnemyUnits.add(infantrySquad);
        listOfEnemyUnits.add(blackTemplar);
        listOfEnemyUnits.add(infantrySquad);

        Army friendlyArmy = new Army("Min arme",0,0,0,0,0,0,0,0,listOfFriendlyUnits,listOfAbilities);
        Army enemyArmy = new Army("Min arme",0,0,0,0,0,0,0,0,listOfEnemyUnits,listOfAbilities);

        Matchup matchup = new Matchup("Matchu fran ny",friendlyArmy,enemyArmy);


        FileHandler handler = new FileHandler(context);

        handler.saveMatchup(matchup);
        //(matchup);




    }

}
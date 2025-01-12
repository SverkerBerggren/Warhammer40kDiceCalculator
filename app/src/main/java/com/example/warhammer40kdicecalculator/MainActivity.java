package com.example.warhammer40kdicecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.FeelNoPain5;
import com.example.warhammer40kdicecalculator.Abilities.FeelNoPain6;
import com.example.warhammer40kdicecalculator.Abilities.HammerOfTheEmperor;
import com.example.warhammer40kdicecalculator.Abilities.IncreaseAp1;
import com.example.warhammer40kdicecalculator.Abilities.MinusOneDamage;
import com.example.warhammer40kdicecalculator.Abilities.MinusOneToHit;
import com.example.warhammer40kdicecalculator.Abilities.MinusOneToWound;
import com.example.warhammer40kdicecalculator.Abilities.ReRollAmountOfHits;
import com.example.warhammer40kdicecalculator.Abilities.ReRollHits;
import com.example.warhammer40kdicecalculator.Abilities.ReRollOnes;
import com.example.warhammer40kdicecalculator.Abilities.ReRollOnesWound;
import com.example.warhammer40kdicecalculator.Abilities.ReRollWoundRoll;
import com.example.warhammer40kdicecalculator.Abilities.TransHuman4;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import     com.example.warhammer40kdicecalculator.DatasheetModeling.Army;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
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



    public static HashMap<String, Ability> abilityMap = new HashMap<>();

    private Context context;
    private ArrayList<Unit> firstPlayerArmy;
    private ArrayList<ArrayList<String>> parsedDatasheetList = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsedWeaponList = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsedModelList = new ArrayList<>();
    private void InstantiateAbilities()
    {
        abilityMap.put("ReRollAmountOfHits", new ReRollAmountOfHits());
        abilityMap.put("HammerOfTheEmperor", new HammerOfTheEmperor());
        abilityMap.put("ReRollOnes", new ReRollOnes());
        abilityMap.put("ReRollHits",new ReRollHits());
        abilityMap.put("FeelNoPain6",new FeelNoPain6());
        abilityMap.put("IncreaseAp1", new IncreaseAp1());
        abilityMap.put("MinusOneToHit", new MinusOneToHit());
        abilityMap.put("ReRollOnesWound", new ReRollOnesWound());
        abilityMap.put("ReRollWounds", new ReRollWoundRoll());
        abilityMap.put("MinusOneToWound", new MinusOneToWound());
        abilityMap.put("MinusOneDamage", new MinusOneDamage());
        abilityMap.put("FeelNoPain5", new FeelNoPain5());
        abilityMap.put("TransHuman4", new TransHuman4());
    }

    public  MainActivity()
    {
        InstantiateAbilities();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        InstantiateAbilities();
        FileHandler.InitializeFileHandler(context);

        DownloadAndCreateDatabases(getWindow().getCurrentFocus());


        try
        {
            Scanner s = new Scanner(context.getAssets().open("TestRos.ros")).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            ROSParser Parser = new ROSParser();
            Army TestArmy = Parser.ParseArmy(result);
        }
        catch(Exception e)
        {

        }
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
    public  void DownloadAndCreateDatabases(View v)
    {
        UpdateArgumentStruct updateArgumentStruct = new UpdateArgumentStruct();

        updateArgumentStruct.FilesToDownload.add("Datasheets.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_wargear.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_models.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_abilities.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_keywords.csv");
        updateArgumentStruct.FilesToDownload.add("Factions.csv");
        updateArgumentStruct.URLPrefix = "https://wahapedia.ru/wh40k10ed/";
        updateArgumentStruct.OutputPrefix = this.getDataDir().toString();
        updateArgumentStruct.context = context;

        Thread UploadDataThread = new Thread(new Callback_Runner<UpdateArgumentStruct,String,Integer>(this,this::p_UpdateCallback, FileHandler.GetInstance()::UpdateFiles,updateArgumentStruct));
        UploadDataThread.start();
    }

    public void testParseDownloadedCsv(View v)
    {
        DatabaseManager parser = DatabaseManager.getInstance();


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


    public class AbilitySearchPopup implements View.OnClickListener
    {
        private Identifier identifier;
        public AbilitySearchPopup(Weapon weapon)
        {
            this.identifier = identifier;
        }
        @Override
        public void onClick(View view) {

        }
    }

    private void InflateSearch(ViewGroup baseView, Context context)
    {
        Log.d("infalter grejer", "inflatar den??");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        if (baseView.findViewById(R.id.SearchLayout) == null)
        {
            inflater.inflate(R.layout.search_prefab, baseView);
        }
        else
        {
            View searchLayout = baseView.findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }


    public void SearchAbility(AbilityHolder abilityHolder, Matchup matchup, ViewGroup baseView, Context context, AbilityUIHolder abilityUIHolder)
    {
        InflateSearch(baseView,context);
        SearchView searchView = baseView.findViewById(R.id.searchView);
        ListView listView = baseView.findViewById(R.id.listView);
        ArrayList<String> searchList = new ArrayList<>();

        for (String abilityName : abilityMap.keySet())
        {
            searchList.add(abilityName);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,searchList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new OnClickAbilityItem(abilityHolder,baseView,matchup,context, abilityUIHolder));
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


        //View searchGroup = findViewById(R.id.SearchGroup);
        //searchGroup.setVisibility(View.VISIBLE);
    }

    private class OnClickAbilityItem implements AdapterView.OnItemClickListener
    {
        private Army army;
        private Unit unit;
        private Model model;
        private Weapon weapon;
        private ViewGroup baseView;
        private Matchup matchup;
        private Context context;
        private AbilityUIHolder abilityUIHolder;

        public OnClickAbilityItem(AbilityHolder abilityHolder, ViewGroup baseView, Matchup matchup, Context context, AbilityUIHolder abilityUIHolder)
        {
            this.baseView = baseView;
            this.matchup = matchup;
            this.context = context;
            this.abilityUIHolder = abilityUIHolder;
            if(abilityHolder instanceof Army)
            {
                army = (Army) abilityHolder;
            }
            if(abilityHolder instanceof Model)
            {
                model = (Model) abilityHolder;
            }
            if(abilityHolder instanceof Unit)
            {
                unit = (Unit) abilityHolder;
            }
            if(abilityHolder instanceof Weapon)
            {
                weapon = (Weapon) abilityHolder;
            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String item = ((TextView)view).getText().toString();
            Ability ability = Ability.getAbilityType( abilityMap.get(item).name);

            if (weapon != null)
            {
                if (!weapon.weaponRules.contains(ability))
                {
                    weapon.weaponRules.add(ability);
                    abilityUIHolder.AbilityAdded(ability,weapon);
                }
            }
            if (model != null)
            {
                if (!model.listOfAbilites.contains(ability))
                {
                    model.listOfAbilites.add(ability);
                    abilityUIHolder.AbilityAdded(ability,model);
                }
            }
            if (unit != null)
            {
                if (!unit.listOfAbilitys.contains(ability))
                {
                    unit.listOfAbilitys.add(ability);
                    abilityUIHolder.AbilityAdded(ability,unit);
                }
            }
            if (army != null)
            {
                if (!army.abilities.contains(ability))
                {
                    army.abilities.add(ability);
                    abilityUIHolder.AbilityAdded(ability,army);
                }
            }

            View searchLayout = baseView.findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.GONE);
            FileHandler.GetInstance().saveMatchup(matchup);
        }
    }

    public void StartParseActivity(View view)
    {
        Intent intent = new Intent(this, ParseActivity.class);

        startActivity(intent);
    }
}
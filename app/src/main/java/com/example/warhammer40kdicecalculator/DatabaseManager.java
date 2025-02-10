package com.example.warhammer40kdicecalculator;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Context;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.AbilityStub;
import com.example.warhammer40kdicecalculator.Abilities.MinusOneDamage;
import com.example.warhammer40kdicecalculator.Abilities.MinusOneToWound;
import com.example.warhammer40kdicecalculator.Abilities.ReRollHits;
import com.example.warhammer40kdicecalculator.Abilities.ReRollOnes;
import com.example.warhammer40kdicecalculator.DatasheetModeling.DiceAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.Faction;
import com.example.warhammer40kdicecalculator.FileHandling.FileHandler;
import com.example.warhammer40kdicecalculator.FileHandling.UpdateArgumentStruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class DatabaseManager {

    private final android.content.res.AssetManager assetManager;
    private final String dataDirectory;
    private final HashMap<IdNameKey, Weapon> DatasheetWargear = new HashMap<>();
    private final HashMap<IdNameKey, ArrayList<Weapon>> MultiModeWeaponDatabase = new HashMap<>();
    private final HashMap<NameFactionKey, Unit> Datasheets = new HashMap<>();
    private final HashMap<IdNameKey, Model> modelsDatasheet = new HashMap<>();
    private final ArrayList<Ability> abilityEnumDatabase = new ArrayList<>();
    private final HashMap<String,Ability> stringAbilityDatabase = new HashMap<>();

    //TODO: bor lowkey tas bort lite sus men skit samma
    private final HashMap<String,AbilityEnum> stringAbilityEnumDatabase = new HashMap<>();

    // Assumes that all ability names have the same description
    private final HashMap<String,AbilityEnum> wahapediaUniqueNamedAbilityDatabase = new HashMap<>();
    public static volatile DatabaseManager instance;

    public static final Object onlineDatabaseLock = new Object();
    private final Object localAbilitiesLock = new Object();

    public HashMap<IdNameKey, Weapon> GetDatasheetWargearDatabase() {return  DatasheetWargear;}
    public HashMap<NameFactionKey, Unit> GetDatasheetsDatabase() {return  Datasheets;}
    public HashMap<IdNameKey, Model> GetModelsDatasheetDatabase() {return  modelsDatasheet;}
    public static class IdNameKey
    {
        private String name;
        private String wahapediaId;

        public IdNameKey(String wahapediaId, String name)
        {
            this.name = name;
            this.wahapediaId = wahapediaId;
        }

        @Override
        public int hashCode()
        {
            //Bajtad af don't @ me
            final int prime = 31;
            int result = 1;
            result = prime * result * + name.hashCode();
            result = prime * result * + wahapediaId.hashCode();
            return  result;
        }

        @Override
        public boolean equals(Object other)
        {
            if(this == other)
            {
                return true;
            }
            if (!(other instanceof IdNameKey))
            {
             return false;
            }
            IdNameKey otherKey = (IdNameKey)other;
            return name.equals(otherKey.name) && wahapediaId.equals(otherKey.wahapediaId);
        }
    }

    public static class NameFactionKey
    {
        private final String name;
        private final Faction faction;

        public NameFactionKey( String name, Faction faction)
        {
            this.name = name;
            this.faction = faction;
        }

        @Override
        public int hashCode()
        {
            //Bajtad af don't @ me
            final int prime = 31;
            int result = 1;
            result = prime * result * + name.hashCode();
            result = prime * result * + faction.hashCode();
            return  result;
        }

        @Override
        public boolean equals(Object other)
        {
            if(this == other)
            {
                return true;
            }
            if (!(other instanceof NameFactionKey))
            {
                return false;
            }
            NameFactionKey otherKey = (NameFactionKey)other;
            return name.equals(otherKey.name) && faction.equals(otherKey.faction) ;
        }
    }

    private Faction ParseFaction(String factionString)
    {
        // TODO: add all factions
        switch (factionString)
        {
            case "am":
                return Faction.AstraMilitarum;
            case "cd":
                return Faction.ChaosDemons;
            case "sm":
                return Faction.SpaceMarines;
            default:
                return Faction.Unidentified;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void InitializeDatabaseManager(Context context)
    {
        if(instance != null)
        {
            Log.d("Database manager"," Database manager is already initialized");
            return;
        }
        instance = new DatabaseManager(context);
        instance.InitializeLocalDatabases();
        instance.DownloadWahapediaData(context);
    }

    private void InitializeLocalDatabases()
    {
        CreateImplementedAbilities();
    }

    //Because of inconsistent char used to separated weapon modes some weapons are interpreted as modal when they are not
    private void CleanUpWeaponDatabase()
    {
        ArrayList<IdNameKey> weaponsToRemove = new ArrayList<>();
        for(IdNameKey idNameKey : MultiModeWeaponDatabase.keySet())
        {
            ArrayList<Weapon> weaponList = MultiModeWeaponDatabase.get(idNameKey);
            if(weaponList.size() < 2)
            {
                weaponsToRemove.add(idNameKey);
                DatasheetWargear.put(new IdNameKey(weaponList.get(0).wahapediaDataId,weaponList.get(0).name),weaponList.get(0));
            }
        }
        for(IdNameKey weaponToRemove : weaponsToRemove)
        {
            MultiModeWeaponDatabase.remove(weaponToRemove);
        }
    }

    private void InitializeInternetDatabases()
    {
        CreateWeaponDatabase();
        CleanUpWeaponDatabase();
        CreateDatasheetDatabase();
        CreateModelsDatasheet();
        CreateWahapediaAbilityDatabase();
    }

    private  void DownloadWahapediaData(Context context)
    {
        UpdateArgumentStruct updateArgumentStruct = new UpdateArgumentStruct();

        updateArgumentStruct.FilesToDownload.add("Datasheets.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_wargear.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_models.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_abilities.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_keywords.csv");
        updateArgumentStruct.FilesToDownload.add("Datasheets_abilities.csv");
        updateArgumentStruct.FilesToDownload.add("Factions.csv");
        updateArgumentStruct.LastUpdateURL = "Last_update.csv";
        updateArgumentStruct.OutputPrefix = context.getDataDir().toString();
        updateArgumentStruct.context = context;

        Thread DownloadThread = new Thread(new Callback_Runner<UpdateArgumentStruct,Pair<String,Context>,Integer>(context,this::p_UpdateCallback, FileHandler.GetInstance()::UpdateWahapediaData,updateArgumentStruct));
        DownloadThread.start();
    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

        @RequiresApi(api = Build.VERSION_CODES.N)
    private DatabaseManager(Context context)
    {
        assetManager = context.getAssets();
        dataDirectory = context.getDataDir().toString() + "/";
    }

    private void CreateWahapediaAbilityDatabase()
    {
        synchronized (localAbilitiesLock)
        {
            ArrayList<ArrayList<String>> abilityData = FileHandler.instance.GetWahapediaDataCSV("Datasheets_abilities.csv");
            for(ArrayList<String> abilityEntry : abilityData)
            {
                // abilities are not seperated in the csv files in a consistent manner
                if(abilityEntry.size() < 7)
                {
                    Log.d("Wahapedia ability database", "Invalid ability entry length");
                    continue;
                }
                if(abilityEntry.get(4).isEmpty())
                {
                    continue;
                }

                AbilityEnum ability;
                ability = stringAbilityEnumDatabase.get(abilityEntry.get(4));
                if (ability == null)
                {
                    ability = AbilityEnum.Unimplemented;
                }

                wahapediaUniqueNamedAbilityDatabase.put(abilityEntry.get(4),ability);
            }
        }
    }

    private void CreateImplementedAbilities()
    {
        synchronized (localAbilitiesLock)
        {
            for(AbilityEnum abilityEnum : AbilityEnum.values())
            {
                Ability ability = null;
                switch (abilityEnum)
                {
                    case MinusOneDamage:
                        ability = new MinusOneDamage();
                        break;
                    case MinusOneToWound:
                        ability = new MinusOneToWound();
                        break;
                    case ReRollHits:
                        ability = new ReRollHits();
                        break;
                    case ReRollOnes:
                        ability = new ReRollOnes();
                        break;
                    case Unimplemented:
                        ability = new AbilityStub();
                    default:
                        Log.d("Ability database","Enum without corresponding ability found");
                }
                if(ability != null)
                {
                    abilityEnumDatabase.add(ability);
                    stringAbilityDatabase.put(ability.name,ability);
                    stringAbilityEnumDatabase.put(ability.name,abilityEnum);
                }
            }
        }
    }

    public AbilityEnum GetAbilityEnumFromWahapediaName(String name)
    {
        return wahapediaUniqueNamedAbilityDatabase.get(name);
    }

    public Ability GetAbility(AbilityEnum abilityEnum)
    {
        return abilityEnumDatabase.get(abilityEnum.ordinal());
    }

    // Name of the ability
    public Ability GetAbility(String name)
    {
        return stringAbilityDatabase.get(name);
    }

    private  void CreateModelsDatasheet()
    {
        ArrayList<ArrayList<String>> modelsData = FileHandler.instance.GetWahapediaDataCSV("Datasheets_models.csv");

        for( ArrayList<String> modelEntry : modelsData)
        {
            if(modelEntry.size() < 12)
            {
                Log.d("Models database", "Invalid model entry length");
                continue;
            }
            Model model = new Model();
            model.wahapediaDataId = modelEntry.get(0);
            model.name = modelEntry.get(2);

            try {
                model.toughness = Integer.parseInt( modelEntry.get(4));
                model.armorSave = Integer.parseInt( modelEntry.get(5).split("\\+")[0]);
                if(!modelEntry.get(6).equals("-"))
                {
                    model.invulnerableSave = Integer.parseInt( modelEntry.get(6));
                }
                model.wounds = Integer.parseInt( modelEntry.get(8));
            }
            catch (Exception e)
            {
                Log.d("Model database",e.getMessage());
                continue;
            }

            modelsDatasheet.put(new IdNameKey(model.wahapediaDataId,model.name),model);
        }
    }
    private  void CreateDatasheetDatabase()
    {
        ArrayList<ArrayList<String>> datasheetsData = FileHandler.instance.GetWahapediaDataCSV("Datasheets.csv");

        for( ArrayList<String> datasheetEntry : datasheetsData)
        {
            if(datasheetEntry.size() != 14)
            {
                Log.d("Datasheet database", "Invalid datasheet entry length");
                continue;
            }
            if(datasheetEntry.get(1).equals("azrael"))
            {
                Log.d("azreal","hitta han");
            }
            Unit unitDatasheet = new Unit();
            unitDatasheet.wahapediaDataId = datasheetEntry.get(0);
            unitDatasheet.unitName = datasheetEntry.get(1);
            Faction faction = ParseFaction(datasheetEntry.get(2));

            Datasheets.put(new NameFactionKey(unitDatasheet.unitName, faction),unitDatasheet);
        }
    }

    public ArrayList<Weapon> GetMultiModeWeapons(IdNameKey idNameKey)
    {
        return MultiModeWeaponDatabase.get(idNameKey);
    }

    private void CreateWeaponDatabase()
    {
        ArrayList<ArrayList<String>> DatasheetWeapons = FileHandler.instance.GetWahapediaDataCSV("Datasheets_wargear.csv");

        for( ArrayList<String> weaponEntry : DatasheetWeapons)
        {
            if(weaponEntry.size() != 13)
            {
                continue;
            }
            Weapon weaponToConstruct = new Weapon();
            weaponToConstruct.wahapediaDataId = weaponEntry.get(0);
            weaponToConstruct.name = weaponEntry.get(4);
            weaponToConstruct.isMelee =  weaponEntry.get(7).equals("melee");
            weaponToConstruct.amountOfAttacks = ParseDiceAmount(weaponEntry.get(8));
            try {
                if (!weaponEntry.get(9).equals("n/a"))
                {
                    // TODO: Is only needed because wahapedia data is wrong. A smarter solution would be good
                    weaponToConstruct.ballisticSkill = Integer.parseInt(weaponEntry.get(9).split("\\+")[0]);
                }
                weaponToConstruct.strength = Integer.parseInt(weaponEntry.get(10));
                weaponToConstruct.ap = Integer.parseInt(weaponEntry.get(11));
                weaponToConstruct.damageAmount = ParseDiceAmount(weaponEntry.get(12));
            }
            catch (Exception e)
            {
                Log.d("Weapon parsing",e.getMessage());
            }

            // Disaster, not consistent
            boolean containsFirstChar = weaponToConstruct.name.contains("–");
            boolean containsSecondChar = weaponToConstruct.name.contains("-");
            if(containsFirstChar || containsSecondChar)
            {
                String stringToSplitBy = (containsFirstChar) ? ("–"):("-");
                String queryName = weaponEntry.get(4).split(stringToSplitBy)[0].trim();
                IdNameKey idNameKey = new IdNameKey(weaponToConstruct.wahapediaDataId,queryName);

                ArrayList<Weapon> weaponVariants = MultiModeWeaponDatabase.computeIfAbsent(idNameKey, key -> new ArrayList<>());
                weaponVariants.add(weaponToConstruct);
            }
            else
            {
                DatasheetWargear.put(new IdNameKey(weaponToConstruct.wahapediaDataId,weaponToConstruct.name),weaponToConstruct);
            }
        }
    }

    private DiceAmount ParseDiceAmount(String string)
    {
        DiceAmount returnValue = new DiceAmount();
        String[] components = string.split("\\+");
        for(String component : components)
        {
            component = component.trim();
            // Doubtful if there is a case in the game where there are more than 9 D3/D6 but it covers that case
            StringBuilder dicePrefix = new StringBuilder();
            boolean isDiceValue = false;
            char diceSuffix = '0';

            for(int i = 0; i < component.length(); i++ )
            {
                if(component.charAt(i) == 'd')
                {
                    isDiceValue = true;
                    continue;
                }

                if(Character.isDigit(component.charAt(i)) )
                {
                    if(!isDiceValue)
                    {
                        dicePrefix.append(component.charAt(i));
                    }
                    else
                    {
                        if(component.charAt(i) == '3' || component.charAt(i) == '6')
                        {
                            diceSuffix = component.charAt(i);
                        }
                        else
                        {
                            Log.d("Dice parsing", "Invalid dice suffix, only D3 and D6 exists");
                        }
                    }
                    continue;
                }
                Log.d("Dice parsing", "Unexpected character found in dice component");
            }
            try {
                if(!isDiceValue)
                {
                    returnValue.baseAmount = Integer.parseInt(component);
                }
                else
                {
                    int diceAmount = 1;
                    if(dicePrefix.length() != 0)
                    {
                        diceAmount = Integer.parseInt(dicePrefix.toString());
                    }
                    if(diceSuffix == '3')
                    {
                        returnValue.numberOfD3 = diceAmount;
                    }
                    else
                    {
                        returnValue.numberOfD6 = diceAmount;
                    }
                }
            }
            catch (Exception exception)
            {
                Log.d("Dice parsing", "Failed to convert dice representation");
            }
        }
        return returnValue;
    }

    public ArrayList<ArrayList<String>> ReadCsvFile(String fileName )
    {   synchronized (onlineDatabaseLock)
        {
            ArrayList<ArrayList<String>> arrayListToReturn = new ArrayList<>();
            try {
                InputStreamReader is = new InputStreamReader(assetManager.open(dataDirectory + fileName));

                BufferedReader reader = new BufferedReader(is);
                String readString = "";
                String[] tempStringArray;

                while ((readString = reader.readLine()) != null) {
                    tempStringArray = readString.split("\\|");
                    ArrayList<String> tempArrayList = new ArrayList<String>();
                    for (int i = 0; i < tempStringArray.length; i++) {
                        tempArrayList.add(tempStringArray[i]);
                    }

                    arrayListToReturn.add(tempArrayList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arrayListToReturn;
        }
    }

    // TODO: Do not know why these are constructed the way they are, completelty bajted
    private static class Runnable_Function<FunctionArgument,FunctionReturnValue> implements Runnable
    {
        Function<FunctionArgument,FunctionReturnValue> m_FunctionToRun = null;
        FunctionArgument m_ArgumentToUse = null;
        Runnable_Function(Function<FunctionArgument,FunctionReturnValue> FunctionToRun,FunctionArgument ArgumentToUse)
        {
            m_ArgumentToUse = ArgumentToUse;
            m_FunctionToRun = FunctionToRun;
        }
        public void run()
        {
            m_FunctionToRun.apply(m_ArgumentToUse);
        }
    }

    private Integer p_UpdateCallback(Pair<String,Context> ResultPair)
    {
        makeText(ResultPair.second,"Update result: "+ResultPair.first, LENGTH_SHORT).show();
        InitializeInternetDatabases();
        return(0);
    }
    private class Callback_Runner <RunArgumentType,RunResultType,CallbackResultType> implements Runnable
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
        public void run()
        {
            synchronized (onlineDatabaseLock)
            {
                RunResultType RunReturnValue = m_CodeToRun.apply(m_RunArgument);
                onlineDatabaseLock.notifyAll();
                new Handler(m_AssociatedContext.getMainLooper()).post(new Runnable_Function<RunResultType,CallbackResultType>(m_FunctionCallback,RunReturnValue));
            }
        }
    }
}

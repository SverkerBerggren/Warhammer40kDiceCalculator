package com.app.DamageCalculator40k;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Context;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.MortalWoundOnHit;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.ReRollHits;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.ReRollOnes;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.ReRollOnesWound;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.ReRollWoundRoll;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.AntiKeyword;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.Blast;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.DevastatingWounds;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.ExtraAttacks;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.Heavy;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.IgnoresCover;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.IndirectFire;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.LethalHits;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.Melta;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.RapidFire;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.SustainedHits;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.Torrent;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.TwinLinked;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.Enums.Faction;
import com.app.DamageCalculator40k.Enums.Keyword;
import com.app.DamageCalculator40k.FileHandling.FileHandler;
import com.app.DamageCalculator40k.Parsing.XmlParser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class DatabaseManager {

    private final HashMap<String,Ability> stringAbilityDatabase = new HashMap<>();

    //TODO: bor lowkey tas bort lite sus men skit samma

    // Assumes that all ability names have the same description
    public static volatile DatabaseManager instance;
    private final XmlParser xmlParser = new XmlParser();

    public static final Object onlineDatabaseLock = new Object();
    private final Object localAbilitiesLock = new Object();
    private  HashMap<NameFactionKey,Model> modelDatabase;
    private  HashMap<NameFactionKey,Unit> unitDatabase;
    private  HashMap<NameFactionUnitKey,ArrayList<Weapon>> nameFactionUnitToWeapon;
    private  HashMap<NameFactionKey,ArrayList<Weapon>> nameToWeapon;

    public static class NameFactionKey
    {
        private final String name;
        private final Faction faction;

        public NameFactionKey( String name, Faction faction)
        {
            this.name = name.toLowerCase();
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
            return name.equalsIgnoreCase(otherKey.name) && faction.equals(otherKey.faction) ;
        }
    }
    public static class NameFactionUnitKey
    {
        private final String name;
        private final Faction faction;
        private final String unitName;

        public NameFactionUnitKey( String name, Faction faction,String unitName)
        {
            this.name = name.toLowerCase();
            this.faction = faction;
            this.unitName = unitName.toLowerCase();
        }

        @Override
        public int hashCode()
        {
            //Bajtad af don't @ me
            final int prime = 31;
            int result = 1;
            result = prime * result * + name.hashCode();
            result = prime * result * + faction.hashCode();
            result = prime * result * + unitName.hashCode();
            return  result;
        }

        @Override
        public boolean equals(Object other)
        {
            if(this == other)
            {
                return true;
            }
            if (!(other instanceof NameFactionUnitKey))
            {
                return false;
            }
            NameFactionUnitKey otherKey = (NameFactionUnitKey)other;
            return name.equalsIgnoreCase(otherKey.name) && faction.equals(otherKey.faction) && unitName.equalsIgnoreCase(otherKey.unitName) ;
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
        instance = new DatabaseManager();

        Log.d("Databas","Updaterar databasen");
        FileHandler.UpdateCallbackBsData updateCallback = new FileHandler.UpdateCallbackBsData() {
            @Override
            public void onProgress(int current, int total, String filename) {

            }

            @Override
            public void onComplete(boolean didUpdate) {
                Log.d("Trådar","hej");
                instance.xmlParser.FillDatabase(FileHandler.GetInstance().GetXMLData());
            }

            @Override
            public void onError(Exception e) {

            }
        };

        instance.modelDatabase = instance.xmlParser.nameToModel;
        instance.unitDatabase = instance.xmlParser.nameToUnit;
        instance.nameFactionUnitToWeapon = instance.xmlParser.nameUnitToWeapon;
        instance.nameToWeapon = instance.xmlParser.nameToWeapon;
        FileHandler.GetInstance().UpdateBattlesScribeData(updateCallback);
    }

    private void InitializeLocalDatabases()
    {
        CreateImplementedAbilities();
    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    public Pair<ItemType,Object> GetItem(String itemName, Unit unit,Faction faction)
    {
        NameFactionKey nameFactionKey = new NameFactionKey(itemName,faction);
        if(modelDatabase.containsKey(nameFactionKey))
        {
            Model model = modelDatabase.get(nameFactionKey).Copy();
            //Lowkey ghetto, but the models maintain their weapons in the xml parsing, maybe should be cleared after the parsing is done.
            model.weapons.clear();
            return new Pair<>(ItemType.MODEL,model);
        }
        NameFactionUnitKey nameFactionUnitKey = new NameFactionUnitKey(itemName,faction,unit.unitName);
        if(  nameFactionUnitToWeapon.containsKey(nameFactionUnitKey))
        {
            ArrayList<Weapon> retList = new ArrayList<>();
            ArrayList<Weapon> databaseWeapons = nameFactionUnitToWeapon.get(nameFactionUnitKey);
            if(databaseWeapons != null)
            {
                for(Weapon weapon : databaseWeapons)
                {
                    retList.add(weapon.Copy());
                }
            }
            return new Pair<>(ItemType.WEAPON, retList);
        }
        if(  nameToWeapon.containsKey(nameFactionKey))
        {
            ArrayList<Weapon> retList = new ArrayList<>();
            ArrayList<Weapon> databaseWeapons = nameToWeapon.get(nameFactionKey);
            if(databaseWeapons != null)
            {
                for(Weapon weapon : databaseWeapons)
                {
                    retList.add(weapon.Copy());
                }
            }
            return new Pair<>(ItemType.WEAPON, retList);
        }

        DatabaseManager.NameFactionKey idNameFaction =  new DatabaseManager.NameFactionKey(itemName.split("\\(")[0].trim(),faction);
        //ghetto af
        if(unitDatabase.containsKey( idNameFaction))
        {
            return new Pair<>(ItemType.UNIT,unitDatabase.get(idNameFaction));
        }
        Ability ability = DatabaseManager.getInstance().GetAbility(itemName);
        if(ability != null)
        {
            return new Pair<>(ItemType.ABILITY,ability);
        }
        //Needs to be the last check before testing if it is a model
        if(unit.singleModelUnit)
        {
            return new Pair<>(ItemType.UNIDENTIFIED,null);
        }
        //Wack case needed for single model units
        // Certain models do not exist in the datasheets_model.csv so this sussy case is needed
        DatabaseManager.NameFactionKey modelKey = new DatabaseManager.NameFactionKey(itemName,faction);
        if(modelDatabase.containsKey(modelKey))
        {
            // Set their name to the parsed string which looks more intuitive
            Model retModel = modelDatabase.get(modelKey).Copy();
            retModel.name = itemName;
            return  new Pair<>(ItemType.MODEL,retModel);
        }
        // TODO: Abilities and stats such as warlord vox caster etc maybe

        return new Pair<>(ItemType.UNIDENTIFIED,null);
    }

    public Model GetModel(NameFactionKey key)
    {
        Model model = modelDatabase.get(key);
        if(model != null)
        {
            //TODO: Ghetto needs a better solution
            model.weapons.clear();
        }
        return modelDatabase.get(key);
    }
    public Unit GetUnit(NameFactionKey key)
    {
        return unitDatabase.get(key);
    }
    private void CreateImplementedAbilities()
    {
        synchronized (localAbilitiesLock)
        {
            stringAbilityDatabase.put(Blast.baseName, new Blast());
            stringAbilityDatabase.put(DevastatingWounds.baseName, new DevastatingWounds());
            stringAbilityDatabase.put(Heavy.baseName, new Heavy());
            stringAbilityDatabase.put(ExtraAttacks.baseName, new ExtraAttacks());
            stringAbilityDatabase.put(IgnoresCover.baseName, new IgnoresCover());
            stringAbilityDatabase.put(IndirectFire.baseName, new IndirectFire());
            stringAbilityDatabase.put(LethalHits.baseName, new LethalHits());
            stringAbilityDatabase.put(Torrent.baseName, new Torrent());
            stringAbilityDatabase.put(TwinLinked.baseName, new TwinLinked());
            stringAbilityDatabase.put(ReRollHits.baseName, new ReRollHits());
            stringAbilityDatabase.put(ReRollOnes.baseName, new ReRollOnes());
            stringAbilityDatabase.put(ReRollOnesWound.baseName, new ReRollOnesWound());
            stringAbilityDatabase.put(ReRollWoundRoll.baseName, new ReRollWoundRoll());
            MortalWoundOnHit mortalWoundOnHit = new MortalWoundOnHit(6);
            mortalWoundOnHit.name = mortalWoundOnHit.name + " 6";
            stringAbilityDatabase.put(mortalWoundOnHit.name, mortalWoundOnHit);
            // TODO: Not quite sure how to handle these
            stringAbilityDatabase.put(RapidFire.baseName, new RapidFire(new DiceAmount()));
            stringAbilityDatabase.put(SustainedHits.baseName, new SustainedHits(0));
            stringAbilityDatabase.put(AntiKeyword.baseName, new AntiKeyword(Keyword.infantry,0));
            stringAbilityDatabase.put(Melta.baseName, new Melta(new DiceAmount()));
        }
    }

    public ArrayList<Ability> GetAbilities()
    {
        return new ArrayList<>(stringAbilityDatabase.values());
    }

    // Name of the ability
    public Ability GetAbility(String name)
    {
        return stringAbilityDatabase.get(name);
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
    public enum ItemType
    {
        MODEL,
        UNIT,
        WEAPON,
        ABILITY,
        UNIMPLEMENTED,
        UNIDENTIFIED
    }
}

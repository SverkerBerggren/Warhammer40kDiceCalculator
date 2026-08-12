package core;

import com.google.gson.JsonObject;

import core.Abilities.Ability;
import core.Abilities.AbilityDefinition;
import core.Abilities.AbilityKind;
import core.Abilities.GenericAbilities.MortalWoundOnHit;
import core.Abilities.GenericAbilities.ReRollHits;
import core.Abilities.GenericAbilities.ReRollOnes;
import core.Abilities.GenericAbilities.ReRollOnesWound;
import core.Abilities.GenericAbilities.ReRollWoundRoll;
import core.Abilities.ParamSpec;
import core.Abilities.ParamType;
import core.Abilities.UnimplementedAbility;
import core.Abilities.WeaponAbilities.AntiKeyword;
import core.Abilities.WeaponAbilities.Blast;
import core.Abilities.WeaponAbilities.Cleave;
import core.Abilities.WeaponAbilities.DevastatingWounds;
import core.Abilities.WeaponAbilities.ExtraAttacks;
import core.Abilities.WeaponAbilities.Heavy;
import core.Abilities.WeaponAbilities.IgnoresCover;
import core.Abilities.WeaponAbilities.IndirectFire;
import core.Abilities.WeaponAbilities.LethalHits;
import core.Abilities.WeaponAbilities.Melta;
import core.Abilities.WeaponAbilities.RapidFire;
import core.Abilities.WeaponAbilities.SustainedHits;
import core.Abilities.WeaponAbilities.Torrent;
import core.Abilities.WeaponAbilities.TwinLinked;
import core.DatasheetModeling.DiceAmount;
import core.DatasheetModeling.Model;
import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Weapon;
import core.Enums.Faction;
import core.Enums.Keyword;
import core.FileHandling.FileHandler;

import core.Logging.Logging;
import core.Parsing.JsonParser;
import core.Parsing.XmlParser;
import core.Util.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.spec.OAEPParameterSpec;

public class DatabaseManager {

    private final HashMap<String,Ability> nameToImplementedAbility = new HashMap<>();

    //TODO: bor lowkey tas bort lite sus men skit samma

    // Assumes that all ability names have the same description
    public static volatile DatabaseManager instance;
    private final XmlParser xmlParser = new XmlParser();
    private final JsonParser jsonParser = new JsonParser();

    public static final Object onlineDatabaseLock = new Object();
    public static boolean isInitialized = false;
    private final Object localAbilitiesLock = new Object();
    private  HashMap<NameFactionKey,Model> modelDatabase;
    private  HashMap<NameFactionKey,Unit> unitDatabase;
    private  HashMap<NameFactionUnitKey,ArrayList<Weapon>> nameFactionUnitToWeapon;
    private  HashMap<String,ArrayList<Weapon>> nameToWeapon;
    private  HashMap<String,Ability> nameToParsedAbility;
    private HashMap<String, Model> nameToModel;
    private HashMap<String, Unit> nameToUnit;

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


    public static void InitializeDatabaseManager( )
    {
        if(instance != null)
        {
            Logging.d("Database manager"," Database manager is already initialized");
            return;
        }
        instance = new DatabaseManager();

        Logging.d("Databas","Updaterar databasen");



        instance.modelDatabase = instance.jsonParser.nameFactionToModel;
        instance.nameToModel = instance.jsonParser.nameToModel;
        instance.unitDatabase = instance.jsonParser.nameFactionToUnit;
        instance.nameToUnit = instance.jsonParser.nameToUnit;
        instance.nameFactionUnitToWeapon = instance.jsonParser.nameFactionUnitToWeapon;
        instance.nameToWeapon = instance.jsonParser.nameToWeapon;
        instance.nameToParsedAbility = instance.jsonParser.nameToAbility;


        synchronized (onlineDatabaseLock) {
            Logging.d("Trådar", "hej");
            //instance.xmlParser.FillDatabase(FileHandler.GetInstance().GetXMLData());
            instance.jsonParser.FillDatabase(FileHandler.GetInstance().GetJsonData());
            isInitialized = true;
        }
        instance.InitializeLocalDatabases();
    }

    private void InitializeLocalDatabases()
    {
        CreateImplementedAbilities();
    }

    public static DatabaseManager getInstance()
    {
        return instance;
    }

    public Pair<ItemType,Object> GetItem(String itemName, Unit unit, Faction faction)
    {
        NameFactionKey nameFactionKey = new NameFactionKey(itemName,faction);
        if(modelDatabase.containsKey(nameFactionKey))
        {
            Model model = modelDatabase.get(nameFactionKey).Copy();
            //Lowkey ghetto, but the models maintain their weapons in the xml parsing, maybe should be cleared after the parsing is done.
            model.weapons.clear();
            return new Pair<>(ItemType.MODEL,model);
        }
        if(nameToModel.containsKey(itemName))
        {
            Model model = nameToModel.get(itemName).Copy();
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

        // Lowkey cap
        if(  nameToWeapon.containsKey(itemName))
        {
            ArrayList<Weapon> retList = new ArrayList<>();
            ArrayList<Weapon> databaseWeapons = nameToWeapon.get(itemName);
            if(databaseWeapons != null)
            {
                for(Weapon weapon : databaseWeapons)
                {
                    retList.add(weapon.Copy());
                }
            }
            return new Pair<>(ItemType.WEAPON, retList);
        }

        DatabaseManager.NameFactionKey idNameFaction =  new DatabaseManager.NameFactionKey(itemName,faction);
        //ghetto af
        if(unitDatabase.containsKey( idNameFaction))
        {

            return new Pair<>(ItemType.UNIT,unitDatabase.get(idNameFaction));
        }
        if(nameToUnit.containsKey(itemName))
        {
            return new Pair<>(ItemType.UNIT,nameToUnit.get(itemName));
        }
        Optional<Ability> ability = DatabaseManager.getAbilityForDisplay(itemName);
        if(ability.isPresent())
        {
            return new Pair<>(ItemType.ABILITY,ability);
        }

        if(itemName.equalsIgnoreCase("warlord"))
        {
            return new Pair<>(ItemType.KEYWORD,Keyword.Warlord);
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
            nameToImplementedAbility.put(Blast.baseName, new Blast(0));
            nameToImplementedAbility.put(DevastatingWounds.baseName, new DevastatingWounds());
            nameToImplementedAbility.put(Heavy.baseName, new Heavy());
            nameToImplementedAbility.put(ExtraAttacks.baseName, new ExtraAttacks());
            nameToImplementedAbility.put(IgnoresCover.baseName, new IgnoresCover());
            nameToImplementedAbility.put(IndirectFire.baseName, new IndirectFire());
            nameToImplementedAbility.put(LethalHits.baseName, new LethalHits());
            nameToImplementedAbility.put(Torrent.baseName, new Torrent());
            nameToImplementedAbility.put(TwinLinked.baseName, new TwinLinked());
            nameToImplementedAbility.put(ReRollHits.baseName, new ReRollHits());
            nameToImplementedAbility.put(ReRollOnes.baseName, new ReRollOnes());
            nameToImplementedAbility.put(ReRollOnesWound.baseName, new ReRollOnesWound());
            nameToImplementedAbility.put(ReRollWoundRoll.baseName, new ReRollWoundRoll());
            MortalWoundOnHit mortalWoundOnHit = new MortalWoundOnHit(6);
            mortalWoundOnHit.name = mortalWoundOnHit.name + " 6";
            nameToImplementedAbility.put(mortalWoundOnHit.name, mortalWoundOnHit);
            // TODO: Not quite sure how to handle these
            nameToImplementedAbility.put(RapidFire.baseName, new RapidFire(new DiceAmount()));
            nameToImplementedAbility.put(SustainedHits.baseName, new SustainedHits(new DiceAmount()));
            nameToImplementedAbility.put(AntiKeyword.baseName, new AntiKeyword(Keyword.Infantry,0));
            nameToImplementedAbility.put(Melta.baseName, new Melta(0));
        }
    }

    public ArrayList<Ability> GetAbilities()
    {
        return new ArrayList<>(nameToImplementedAbility.values());
    }

    private static final Pattern ABILITY_TOKEN_PATTERN = Pattern.compile("^((?:[A-Za-z]+(?=[\\s-]|$)\\s*)+)(?:-([A-Za-z]+(?=\\s|$)))?\\s*(.*)$");

    public static Optional<Ability> getAbility(String abilityText) {
        Matcher m = ABILITY_TOKEN_PATTERN.matcher(abilityText);
        if (m.matches()) {
            String alias = m.group(1);
            alias = alias.strip();

            String keyword = m.group(2); // may be null
            if(keyword != null)
            {
                keyword = keyword.strip();
            }

            String diceAmount = m.group(3);

            diceAmount = diceAmount.strip();
            //Cleans trailing +:es
            diceAmount = diceAmount.replaceAll("\\+$", "");

            List<String> params = new ArrayList<>();
            if(keyword != null)
            {
                params.add(keyword);
            }
            if( !diceAmount.isEmpty())
            {
                params.add(diceAmount);
            }

            return AbilityKind.getAbility(alias, params);
        }
        else
        {
            return Optional.empty();
        }
    }
    public static Optional<Ability> getAbilityForDisplay(String abilityText) {
        Optional<Ability> implementedAbility = getAbility(abilityText);
        if(implementedAbility.isEmpty())
        {

            Ability parsedAbility = DatabaseManager.getInstance().nameToParsedAbility.get(abilityText);
            if(parsedAbility != null)
            {
                return Optional.of( parsedAbility);            }
            else
            {
                return Optional.empty();
            }
        }
        else
        {
            return implementedAbility;
        }
    }

    public enum ItemType
    {
        MODEL,
        UNIT,
        WEAPON,
        ABILITY,
        // Lowkey only used for warlords
        KEYWORD,
        UNIMPLEMENTED,
        UNIDENTIFIED
    }
}

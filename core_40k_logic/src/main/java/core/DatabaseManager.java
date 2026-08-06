package core;

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
import java.util.HashMap;
import java.util.List;

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
        Ability ability = DatabaseManager.getInstance().GetAbility(itemName);
        if(ability != null)
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

    public static List<AbilityDefinition> GetCatalog()
    {
        return  CATALOG;
    }
    private static final List<AbilityDefinition> CATALOG = List.of(
            new AbilityDefinition(AbilityKind.ANTI_KEYWORD, "Anti-Keyword",
                    "Wounds on a fixed roll against a specific keyword.",
                    List.of(new ParamSpec("keyword", ParamType.KEYWORD, "Keyword"),
                            new ParamSpec("woundThreshold", ParamType.INT, "Threshold")),false),
            new AbilityDefinition(AbilityKind.LETHAL_HITS, "Lethal Hits",
                    "Critical hits auto-wound.", List.of(),false),
            new AbilityDefinition(AbilityKind.DEVASTATING_WOUNDS,"Devastating wounds","Mortal wounds on critical hits",List.of(),false),
            new AbilityDefinition(AbilityKind.BLAST,"Blast","Extra hits depending on size of defending unit",
                    List.of(new ParamSpec("extra attacks", ParamType.INT, "extra attacks")),false),
            new AbilityDefinition(AbilityKind.CLEAVE,"Cleave","Extra hits depending on size of defending unit",
                            List.of(new ParamSpec("extra attacks", ParamType.INT, "extra attacks")),false),
            new AbilityDefinition(AbilityKind.MELTA,"Melta","Extra wounds depending on range",
                    List.of(new ParamSpec("extraWounds", ParamType.INT, "extra wounds")),true),
            new AbilityDefinition(AbilityKind.SUSTAINED_HITS,"Sustained hits","Extra hits on critical hits",
                    List.of(new ParamSpec("extraHits", ParamType.DICE_AMOUNT, "extra hits")),false),
            new AbilityDefinition(AbilityKind.RAPID_FIRE,"Rapid fire","Extra hits when in half range",
                    List.of(new ParamSpec("extra attacks", ParamType.DICE_AMOUNT, "extra attacks")),true),
            new AbilityDefinition(AbilityKind.TWIN_LINKED,"Twin-linked","Reroll wound roll",
                    List.of(),false)
    );
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

    // Name of the ability
    public Ability GetAbility(String name)
    {
        Ability ability = nameToImplementedAbility.get(name);
        if(ability == null)
        {
            ability = nameToParsedAbility.get(name);

        }
        return ability;
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

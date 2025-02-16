package com.app.DamageCalculator40k.Parsing;

import android.util.Log;
import android.util.Pair;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.AbilityStub;
import com.app.DamageCalculator40k.DatabaseManager;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.DatasheetModeling.WahapediaIdHolder;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.Faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


enum ItemType
{
    MODEL,
    UNIT,
    WEAPON,
    ABILITY,
    UNIMPLEMENTED,
    UNIDENTIFIED
}

public class Parsing
{

    private final String BATTLELINE = "battleline";
    private final String CHARACTER = "character";
    private final String DEDICATED_TRANSPORTS = "dedicated transports";
    private final String OTHER_DATASHEETS = "other datasheet";
    private HashMap<DatabaseManager.IdNameKey, Model> modelDatasheetDatabase;
    private HashMap<DatabaseManager.IdNameKey, Weapon> wargearDatabase;
    private HashMap<DatabaseManager.NameFactionKey, Unit> datasheetDatabase;
    // Kanske lite ghetto
    private Army armyToBuild;
    private Faction armyFaction;

    //
    private String ConvertArmyListToWahapediaStandard(String armyList)
    {
        return armyList.replace('\'','’').toLowerCase();
    }

    //TODO: add all factions
    private Faction ParseArmyFaction(String armyList)
    {
        if(armyList.contains("astra militarum"))
        {
            return Faction.AstraMilitarum;
        }
        if(armyList.contains("chaos daemons"))
        {
            return Faction.ChaosDemons;
        }
        if(armyList.contains("space marines"))
        {
            return Faction.SpaceMarines;
        }

        return Faction.Unidentified;
    }

    private boolean IsItemUnimplementedValue(String itemName)
    {
        if(itemName.contains("warlord"))
        {
            return true;
        }
        if(itemName.contains("Enhancement"))
        {
            return true;
        }
        if(itemName.contains("daemonic allegiance"))
        {
            return true;
        }
        return itemName.contains(BATTLELINE) || itemName.contains(DEDICATED_TRANSPORTS) || itemName.contains(CHARACTER) || itemName.contains(OTHER_DATASHEETS);
    }

    public Army ParseGWListFormat(String armyListString)
    {
        // Waits for the database to be initialized
        synchronized (DatabaseManager.onlineDatabaseLock) {
            while (DatabaseManager.getInstance() == null) {
                try { DatabaseManager.onlineDatabaseLock.wait(); }
                catch (Exception e) {
                    Log.d("Lock knas arme parsing",e.getMessage());
                }
            }
        }
        modelDatasheetDatabase = DatabaseManager.getInstance().GetModelsDatasheetDatabase();
        datasheetDatabase = DatabaseManager.getInstance().GetDatasheetsDatabase();
        wargearDatabase = DatabaseManager.getInstance().GetDatasheetWargearDatabase();

        armyListString = ConvertArmyListToWahapediaStandard(armyListString);
        armyFaction = ParseArmyFaction(armyListString);

        int stringOffset = 0;
        int armyListStringLength = armyListString.length();
        armyToBuild = new Army();

        stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        while (stringOffset < armyListStringLength)
        {
            stringOffset = ParseFirstUnitAfterDemarcation(stringOffset,armyListString);

            stringOffset = ParseUnit(stringOffset,armyListString,armyToBuild);

        }
        return  armyToBuild;
    }

    private int ParseFirstUnitAfterDemarcation(int offset, String armyListString)
    {
        while(offset < armyListString.length())
        {
            Pair<Integer,String> offsetAndLine = ParseUntilLineBreak(offset,armyListString);
            offset = offsetAndLine.first;
            //TODO: lite ghetto
            if(IsDemarcation(offsetAndLine.second) && !offsetAndLine.second.contains("+") )
            {
                return RemoveWhiteSpaces(offset +1,armyListString);
            }
            offset++;
        }
        return offset;
    }

    private int RemoveWhiteSpaces(int stringOffset, String armyListString)
    {
        int armyListStringLength = armyListString.length();
        while (stringOffset < armyListStringLength)
        {
            if(!IsWhiteSpace(armyListString.charAt(stringOffset)))
            {
                return stringOffset;
            }
            stringOffset += 1;
        }
        return armyListStringLength;
    }

    private boolean IsWhiteSpace(char charToCompare)
    {
        switch (charToCompare)
        {
            case '\n':
            case '\t':
            case '\r':
            case ' ':
                return  true;
        }
        return  false;
    }

    private Pair<Integer,String> ParseUntilLineBreak(int offset, String armyList)
    {
        StringBuilder parsedString = new StringBuilder();
        while(offset < armyList.length())
        {
            if(armyList.charAt(offset) == '\r' || armyList.charAt(offset) == '\n' )
            {
                return new Pair<>(offset,parsedString.toString());
            }
            parsedString.append(armyList.charAt(offset));
            offset += 1;
        }

        return new Pair<>(offset,parsedString.toString());
    }

    private boolean IsDemarcation(String subString)
    {
        return subString.contains(BATTLELINE) || subString.contains(DEDICATED_TRANSPORTS) || subString.contains(CHARACTER) || subString.contains(OTHER_DATASHEETS);
    }

    // GetItem can be called with wahapediaIdHolder as null. That means that it will only search the databases where no id is needed
    // Unit is a bit of a wack parameter
    private Pair<ItemType,Object> GetItem(String itemName, WahapediaIdHolder wahapediaIdHolder, Unit unit)
    {
        if(wahapediaIdHolder != null)
        {
            DatabaseManager.IdNameKey idNameKey = new DatabaseManager.IdNameKey(wahapediaIdHolder.GetWahapediaId(),itemName);
            if(modelDatasheetDatabase.containsKey(idNameKey))
            {
                return new Pair<>(ItemType.MODEL,modelDatasheetDatabase.get(idNameKey).Copy());
            }
            if(  wargearDatabase.containsKey(idNameKey))
            {
                ArrayList<Weapon> retList = new ArrayList<>();
                retList.add(wargearDatabase.get(idNameKey).Copy());
                return new Pair<>(ItemType.WEAPON, retList);
            }

            ArrayList<Weapon> multiModeWeapons = DatabaseManager.instance.GetMultiModeWeapons(idNameKey);
            if(multiModeWeapons != null)
            {
                ArrayList<Weapon> retList = new ArrayList<>(multiModeWeapons);
                // Make the standard so that only 1 weapon mode is active at the same time
                retList.replaceAll(weapon -> {weapon.active = false; return  weapon.Copy();});
                retList.get(0).active = true;
                return new Pair<>(ItemType.WEAPON,retList);
            }

            DatabaseManager.NameFactionKey idNameFaction =  new DatabaseManager.NameFactionKey(itemName.split("\\(")[0].trim(),armyFaction);
            //ghetto af
            if(datasheetDatabase.containsKey( idNameFaction))
            {
                return new Pair<>(ItemType.UNIT,datasheetDatabase.get(idNameFaction));
            }

            Ability ability = DatabaseManager.getInstance().GetAbility(itemName);
            if(ability != null)
            {
                return new Pair<>(ItemType.ABILITY,ability);
            }

            if(IsItemUnimplementedValue(itemName))
            {
                return new Pair<>(ItemType.UNIMPLEMENTED,null);
            }

            //Needs to be the last check before testing if it is a model
            if(unit.singleModelUnit)
            {
                return new Pair<>(ItemType.UNIDENTIFIED,null);
            }

            //Wack case needed for single model units
            // Certain models do not exist in the datasheets_model.csv so this sussy case is needed
            DatabaseManager.IdNameKey modelKey = new DatabaseManager.IdNameKey(wahapediaIdHolder.GetWahapediaId(),unit.unitName);
            if(modelDatasheetDatabase.containsKey(modelKey))
            {
                // Set their name to the parsed string which looks more intuitive
                Model retModel = modelDatasheetDatabase.get(modelKey).Copy();
                retModel.name = itemName;
                return  new Pair<>(ItemType.MODEL,retModel);
            }
            // TODO: Abilities and stats such as warlord vox caster etc maybe
        }


        return new Pair<>(ItemType.UNIDENTIFIED,null);
    }

    private int ParseModelEquipment(int offset, String armyList, Unit unit, Model modelType, int modelCount)
    {
        int amount = 1;
        int armyLength = armyList.length();
        int startIndex = (unit.listOfModels.isEmpty()) ? (0):(unit.listOfModels.size());
        // Lite cap langsamt af men lite snyggare
        AtomicInteger modelsRangeWeaponIndex = new AtomicInteger(startIndex);
        AtomicInteger modelsMeleeWeaponIndex = new AtomicInteger(startIndex);

        for(int i = 0; i < modelCount; i++)
        {
            unit.listOfModels.add(modelType.Copy());
        }

        while (offset < armyLength)
        {
            offset = RemoveWhiteSpaces(offset,armyList);
            if(IsItemAmountSignifier(armyList.charAt(offset)))
            {
                Pair<Integer,Integer> offsetAndAmount = ParseItemAmount(armyList,offset);
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
            }

            Pair<Integer,String> offsetAndItem = ParseUntilLineBreak(offset,armyList);
            offset = offsetAndItem.first;
            Pair<ItemType,Object> parsedItem = GetItem(offsetAndItem.second,unit,unit);
            if(parsedItem.first.equals(ItemType.WEAPON))
            {
                ArrayList<Weapon> weaponToGive = (ArrayList<Weapon>)parsedItem.second;
                // Se ovan
                // Assumes that weapon modes are always of the same range type
                AtomicInteger modelIndexStart = (weaponToGive.get(0).isMelee) ? ( modelsMeleeWeaponIndex):(  modelsRangeWeaponIndex);
                for(int i = 0; i < amount; i++ )
                {
                    unit.listOfModels.get(modelIndexStart.get()).weapons.addAll(weaponToGive);
                    if(modelIndexStart.get() >= unit.listOfModels.size() -1)
                    {
                        modelIndexStart.set( startIndex);
                    }
                    else
                    {
                        modelIndexStart.incrementAndGet();
                    }
                }
            }
            if(parsedItem.first.equals(ItemType.MODEL))
            {
                return ParseModelEquipment(offset +1, armyList,unit, (Model)parsedItem.second,amount);
            }
            if(parsedItem.first.equals(ItemType.UNIT))
            {
                return ParseUnit(offset - offsetAndItem.second.length(),armyList, armyToBuild);
            }

            offset++;
        }
        return offset;
    }

    private int ParseUnitItem(int offset, String armyList, Unit unit)
    {
        int armyLength = armyList.length();
        int amount = 1;
        while(offset < armyLength)
        {
            offset = RemoveWhiteSpaces(offset,armyList);
            if(IsItemAmountSignifier(armyList.charAt(offset)))
            {
                Pair<Integer,Integer> offsetAndAmount  = ParseItemAmount(armyList,offset );
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
            }

            Pair<Integer,String> offsetAndParsedString = ParseUntilLineBreak(offset,armyList);
            offset = offsetAndParsedString.first;
            Pair<ItemType, Object> parsedItem = GetItem(offsetAndParsedString.second,unit,unit);
            if(parsedItem.first.equals(ItemType.UNIDENTIFIED) || parsedItem.first.equals(ItemType.UNIMPLEMENTED))
            {
                Log.d("Unit item parsing","Unidentified item found");
                continue;
            }
            // If the first item is a weapon it is assumed that the unit is a single model unit
            if( parsedItem.first.equals(ItemType.WEAPON) && unit.listOfModels.isEmpty())
            {
                unit.singleModelUnit = true;
                // Assumes that a single model units models names corresponds with the unit name
                Model modelToCopy = modelDatasheetDatabase.get( new DatabaseManager.IdNameKey( unit.GetWahapediaId() , unit.unitName));
                if(modelToCopy != null)
                {
                    unit.listOfModels.add( modelDatasheetDatabase.get( new DatabaseManager.IdNameKey( unit.GetWahapediaId() , unit.unitName)).Copy());
                    unit.listOfModels.get(0).weapons.addAll((ArrayList<Weapon>) parsedItem.second);
                }
                else
                {
                    Log.d("Unit parsing","Single model unit without corresponding model found");
                }
                offset+=1;

                continue;
            }
            if(parsedItem.first.equals(ItemType.ABILITY))
            {
                unit.GetAbilities().add((Ability) parsedItem.second);
            }
            if(unit.singleModelUnit && parsedItem.first.equals(ItemType.WEAPON))
            {
                ArrayList<Weapon> weapons = (ArrayList<Weapon>)parsedItem.second;
                unit.listOfModels.get(0).weapons.addAll(weapons);
            }
            if(parsedItem.first.equals(ItemType.UNIT))
            {
                return ParseUnit(offset - offsetAndParsedString.second.length(),armyList,armyToBuild);
            }
            if(parsedItem.first.equals(ItemType.MODEL))
            {
                offset = ParseModelEquipment(offset +1,armyList,unit,(Model) parsedItem.second,amount);
                continue;
            }
            offset +=1;
        }

        return offset;
    }

    private int ParseUnit(int offset, String armyListString, Army armyToBuild )
    {
        int armyLength = armyListString.length();

        StringBuilder unitName = new StringBuilder();
        Unit unitToAdd = new Unit();

        while (offset < armyLength)
        {
            if(IsItemAmountSignifier(armyListString.charAt(offset)))
            {
                offset = ParseItemAmount(armyListString,offset).first;
            }
            if(!SkipCharacter(armyListString.charAt(offset)))
            {
                if(armyListString.charAt(offset) == '(')
                {
                    Pair<Integer,Integer> pointValue = ParsePointValue(armyListString,offset+1);
                    unitToAdd.pointCost = pointValue.second;
                    unitToAdd.unitName = unitName.toString().trim();
                    offset = pointValue.first;

                    DatabaseManager.NameFactionKey key =  new DatabaseManager.NameFactionKey(unitToAdd.unitName,armyFaction);
                    unitToAdd.wahapediaDataId = datasheetDatabase.get(key).wahapediaDataId;
                    offset = ParseUnitItem(offset,armyListString,unitToAdd);
                    armyToBuild.units.add(unitToAdd);
                    return  offset;
                }
                else
                {
                    unitName.append(armyListString.charAt(offset));
                }
            }
            offset +=1;
        }
        return  offset;
    }

    private Pair<Integer,Integer> ParseItemAmount(String armyList, int stringOffset)
    {
        StringBuilder itemAmountString = new StringBuilder();

        // Yikes
        int spaceOccurrences = 0;
        while (stringOffset < armyList.length())
        {
            if(armyList.charAt(stringOffset) == ' ')
            {
                spaceOccurrences += 1;
                // These are lowkey equivalent
                if(spaceOccurrences == 2 || itemAmountString.length() != 0)
                {
                    try
                    {
                        if(itemAmountString.length() != 0)
                        {
                            Integer amount = Integer.parseInt(itemAmountString.toString());
                            return new Pair<>(stringOffset +1 ,amount);
                        }
                        else
                        {
                            return  new Pair<>(stringOffset+1,0);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Item amount",e.getMessage());
                    }
                }
            }
            if(Character.isDigit(armyList.charAt(stringOffset)))
            {
                itemAmountString.append(armyList.charAt(stringOffset));
            }
            if(Character.isAlphabetic(armyList.charAt(stringOffset) ) && armyList.charAt(stringOffset) != 'x'  )
            {
                char hej = armyList.charAt(stringOffset);
                return new Pair<>(stringOffset,0);
            }
            stringOffset +=1;
        }
        return new Pair<>(stringOffset,0);
    }

    private Pair<Integer,Integer> ParsePointValue(String ArmyList, int StringOffset)
    {
        int newOffset = StringOffset;
        StringBuilder pointValue = new StringBuilder();
        while (newOffset <= ArmyList.length())
        {
            char CharToExamine = ArmyList.charAt(newOffset);
            if(Character.isDigit(CharToExamine) || CharToExamine == ')')
            {
                if(CharToExamine == ')')
                {
                    int pointInteger = Integer.parseInt(pointValue.toString());
                    return  new Pair<Integer,Integer>(newOffset +1, pointInteger);
                }
                pointValue.append( ArmyList.charAt(newOffset));
            }
            newOffset +=1;
        }

        return new Pair<>(-1,-1);
    }

    private boolean IsItemAmountSignifier(char charToExamine)
    {
        return charToExamine == '•' || Character.isDigit(charToExamine);
    }

    private boolean SkipCharacter(char charToExamine)
    {
        switch (charToExamine) {
            case '•':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                return true;
        }
        return false;
    }

}

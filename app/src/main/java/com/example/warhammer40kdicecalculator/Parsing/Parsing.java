package com.example.warhammer40kdicecalculator.Parsing;

import android.util.Log;
import android.util.Pair;

import com.example.warhammer40kdicecalculator.DatabaseManager;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.DatasheetModeling.WahapediaIdHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.Enums.Faction;

import java.util.HashMap;


enum ItemType
{
    MODEL,
    UNIT,
    WEAPON,
    UNIDENTIFIED
}

public class Parsing
{

    private final String BATTLELINE = "battleline";
    private final String CHARACTER = "character";
    private final String DEDICATED_TRANSPORTS = "dedicated transports";
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

    private Faction ParseArmyFaction(String armyList)
    {
        if(armyList.contains("astra militarum"))
        {
            return Faction.AstraMilitarum;
        }
        return Faction.Unidentified;
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
            String newItem = GetNewLineSubString(stringOffset,armyListString);
            if(!IsDemarcation(newItem))
            {
                stringOffset = ParseUnit(stringOffset,armyListString,armyToBuild);
            }
            else
            {
                stringOffset += newItem.length();
            }
            stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        }
        SaveArmy("gogogagaa");
        return  armyToBuild;
    }


    private String GetNewLineSubString(int stringOffset, String armyListString)
    {
        int endOffset = stringOffset;

        while (endOffset < armyListString.length())
        {
            if(armyListString.charAt(endOffset)== '\n' || armyListString.charAt(endOffset) == '\r' )
            {
                return  armyListString.substring(stringOffset,endOffset);
            }
            else
            {
                endOffset += 1;
            }
        }
        return  "knas";
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
        if(subString.contains(BATTLELINE) ||subString.contains(DEDICATED_TRANSPORTS) || subString.contains(CHARACTER) || subString.contains("+"))
        {
            return  true;
        }

        return  false;
    }

    private void SaveArmy(String armyToSave)
    {

    }

    // GetItem can be called with wahapediaIdHolder as null. That means that it will only search the databases where no id is needed
    // Unit is a bit of a wack parameter
    private Pair<ItemType,Object> GetItem(String itemName, WahapediaIdHolder wahapediaIdHolder, Unit unit)
    {
        if(wahapediaIdHolder != null)
        {
            DatabaseManager.IdNameKey nameKey = new DatabaseManager.IdNameKey(wahapediaIdHolder.GetWahapediaId(),itemName);
            if(modelDatasheetDatabase.containsKey(nameKey))
            {
                // TODO: Should it always copy?
                return new Pair<>(ItemType.MODEL,modelDatasheetDatabase.get(nameKey).Copy());
            }
            if(  wargearDatabase.containsKey(nameKey))
            {
                return new Pair<>(ItemType.WEAPON,wargearDatabase.get(nameKey));
            }
            DatabaseManager.NameFactionKey idNameFaction =  new DatabaseManager.NameFactionKey(itemName.split("\\(")[0].trim(),armyFaction);
            //ghetto af
            if(datasheetDatabase.containsKey( idNameFaction))
            {
                return new Pair<>(ItemType.UNIT,datasheetDatabase.get(idNameFaction));
            }
            // Certain models do not exist in the datasheets_model.csv so this sussy case is needed
            DatabaseManager.IdNameKey modelKey = new DatabaseManager.IdNameKey(wahapediaIdHolder.GetWahapediaId(),unit.unitName);
            if(modelDatasheetDatabase.containsKey(modelKey))
            {
                // Set their name to the parsed string which makes it more clear
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
        int amount = 0;
        int armyLength = armyList.length();
        int startIndex = (unit.listOfModels.isEmpty()) ? (0):(unit.listOfModels.size());
        // Lite cap langsamt af men lite snyggare
        Integer modelsRangeWeaponIndex = startIndex;
        Integer modelsMeleeWeaponIndex = startIndex;

        for(int i = 0; i < modelCount; i++)
        {
            unit.listOfModels.add(modelType.Copy());
        }

        while (offset < armyLength)
        {
            RemoveWhiteSpaces(offset,armyList);
            if(IsItemAmountSignifier(armyList.charAt(offset)))
            {
                Pair<Integer,Integer> offsetAndAmount = ParseItemAmount(armyList,offset);
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
                Pair<Integer,String> offsetAndItem = ParseUntilLineBreak(offset,armyList);
                offset = offsetAndItem.first;
                Pair<ItemType,Object> parsedItem = GetItem(offsetAndItem.second,unit,unit);
                if(parsedItem.first.equals(ItemType.WEAPON))
                {
                    Weapon weaponToGive = (Weapon)parsedItem.second;
                    // Se ovan
                    Integer modelIndexStart = (weaponToGive.isMelee) ? ( modelsMeleeWeaponIndex):(  modelsRangeWeaponIndex);
                    for(int i = 0; i < amount; i++ )
                    {
                        unit.listOfModels.get(modelIndexStart).weapons.add(weaponToGive.Copy());
                        if(modelIndexStart >= unit.listOfModels.size() -1)
                        {
                            modelIndexStart = 0;
                        }
                        else
                        {
                            modelIndexStart +=1;
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
            }
            offset++;
        }
        return offset;
    }

    private int ParseUnitItem(int offset, String armyList, Unit unit)
    {
         int armyLength = armyList.length();
        int amount = 0;
        while(offset < armyLength)
        {
            offset = RemoveWhiteSpaces(offset,armyList);
            if(IsItemAmountSignifier(armyList.charAt(offset)))
            {
                Pair<Integer,Integer> offsetAndAmount  = ParseItemAmount(armyList,offset );
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
                Pair<Integer,String> offsetAndParsedString = ParseUntilLineBreak(offset,armyList);
                offset = offsetAndParsedString.first;

                Pair<ItemType, Object> parsedItem = GetItem(offsetAndParsedString.second,unit,unit);
                if(parsedItem.first.equals(ItemType.UNIDENTIFIED))
                {
                    Log.d("Unit item parsing","Unidentified item found");
                    continue;
                }
                // If the first item is a weapon it is assumed that the unit is a single model unit
                if( parsedItem.first.equals(ItemType.WEAPON) && unit.listOfModels.isEmpty())
                {
                    unit.singleModelUnit = true;
                    // Assumes that a single model units models names corresponds with the unit name
                    unit.listOfModels.add( modelDatasheetDatabase.get( new DatabaseManager.IdNameKey( unit.GetWahapediaId() , unit.unitName)).Copy());
                    unit.listOfModels.get(0).weapons.add((Weapon) parsedItem.second);
                    offset+=1;
                    continue;
                }
                if(unit.singleModelUnit && parsedItem.first.equals(ItemType.WEAPON))
                {
                    unit.listOfModels.get(0).weapons.add((Weapon) parsedItem.second);
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
                            // Works for stuff without amount?
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

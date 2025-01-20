package com.example.warhammer40kdicecalculator.Parsing;

import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;

import com.example.warhammer40kdicecalculator.DatabaseManager;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;

import java.util.HashMap;

public class Parsing
{

    private final String BATTLELINE = "BATTLELINE";
    private final String CHARACTER = "CHARACTER";
    private final String DEDICATED_TRANSPORTS = "DEDICATED TRANSPORTS";

    private final Object DatabaseLock = new Object();

    public Army ParseGWListFormat(String armyListString)
    {
        // Waits for the database to be initialized
        synchronized (DatabaseLock) {
            while (DatabaseManager.getInstance() == null) {
                try { DatabaseLock.wait(); }
                catch (Exception e) {
                    Log.d("Lock knas arme parsing",e.getMessage());
                }
            }
        }

        int stringOffset = 0;
        int armyListStringLength = armyListString.length();
        Army ParsedArmy = new Army();

        stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        while (stringOffset < armyListStringLength)
        {
            String newItem = GetNewLineSubString(stringOffset,armyListString);
            if(!IsDemarcation(newItem))
            {
                Pair<Integer,Unit> UnitToAdd = ParseUnit(stringOffset,armyListString,ParsedArmy);
                ParsedArmy.units.add(UnitToAdd.second);
                stringOffset = UnitToAdd.first;
            }
            else
            {
                stringOffset += newItem.length();
            }
            stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        }
        SaveArmy("gogogagaa");
        return  ParsedArmy;
    }


    private String GetNewLineSubString(int stringOffset, String armyListString)
    {
        int endOffset = stringOffset;

        while (endOffset < armyListString.length())
        {
            if(armyListString.charAt(endOffset)== '\n')
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

    private Pair<Integer,String> ParseUnitLineBreak(int offset, String armyList)
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
        return new Pair<>(offset,"");
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

    private int ParseModelEquipment(int offset, String armyList, Unit unit, Model modelType, int modelCount)
    {
        int amount = 0;
        int armyLength = armyList.length();
        while (offset < armyLength)
        {
            RemoveWhiteSpaces(offset,armyList);
            if(IsItemAmountSignifier(armyList.charAt(offset)))
            {
                Pair<Integer,Integer> offsetAndAmount = ParseItemAmount(armyList,offset);
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
            }
            offset++;
        }
        return offset;
    }

    private void AddWeaponToModel(DatabaseManager.IdNameKey key, Model model )
    {
        Weapon weapon = DatabaseManager.instance.GetDatasheetWargearDatabase().get(key);
        if(weapon.isMelee)
        {
          //  model.listOfMeleeWeapons.add(weapon);
        }
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
                Pair<Integer,Integer> offsetAndAmount  = ParseItemAmount(armyList,offset +1);
                offset = offsetAndAmount.first;
                amount = offsetAndAmount.second;
                Pair<Integer,String> parsedStringAndOffset = ParseUnitLineBreak(offset +1,armyList);
                offset = parsedStringAndOffset.first;

                String itemName = parsedStringAndOffset.second;
                boolean isModel = false;

                DatabaseManager.IdNameKey idNameKey = new DatabaseManager.IdNameKey(unit.wahapediaDataId,itemName);
                HashMap<DatabaseManager.IdNameKey, Model> modelDatasheetDatabase = DatabaseManager.getInstance().GetModelsDatasheetDatabase();

                isModel = modelDatasheetDatabase.containsKey( new DatabaseManager.IdNameKey(unit.wahapediaDataId,itemName));

                // If the first item is a weapon it is assumed that the unit is a single model unit
                if( !isModel && unit.listOfModels.isEmpty())
                {
                    unit.singleModelUnit = true;
                    unit.listOfModels.add( modelDatasheetDatabase.get( idNameKey) );
                    continue;
                }
                if(isModel)
                {
                    offset = ParseModelEquipment(offset +1,armyList,unit,modelDatasheetDatabase.get(idNameKey),amount);
                    continue;
                }
            }
            offset +=1;
        }

        return offset;
    }

    private Pair<Integer,Unit> ParseUnit(int offset, String armyListString, Army armyToBuild )
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

                    unitToAdd.wahapediaDataId = DatabaseManager.getInstance().GetDatasheetsDatabase().get(unitToAdd.unitName).wahapediaDataId;
                    offset = ParseUnitItem(offset,armyListString,unitToAdd);

                    return  new Pair<Integer,Unit>(offset,unitToAdd);
                }
                else
                {
                    unitName.append(armyListString.charAt(offset));
                }
            }
            offset +=1;
        }
        return  new Pair<Integer,Unit>(offset,unitToAdd);
    }

    private Pair<Integer,Integer> ParseItemAmount(String armyList, int stringOffset)
    {
        StringBuilder itemAmountString = new StringBuilder();
        while (stringOffset < armyList.length())
        {
            if(armyList.charAt(stringOffset) == ' ')
            {
                try
                {
                    Integer amount = Integer.parseInt(itemAmountString.toString());
                    return new Pair<>(stringOffset +1 ,amount);
                }
                catch (Exception e)
                {
                    Log.d("Item amount",e.getMessage());
                }
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

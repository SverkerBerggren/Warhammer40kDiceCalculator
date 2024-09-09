package com.example.warhammer40kdicecalculator.Parsing;

import android.accessibilityservice.FingerprintGestureController;
import android.util.Pair;

import java.time.OffsetTime;
import java.util.ArrayList;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;

public class Parsing
{

    private final String BATTLELINE = "BATTLELINE";
    private final String CHARACTER = "CHARACTER";
    private final String DEDICATED_TRANSPORTS = "DEDICATED TRANSPORTS";


    public Army ParseGWListFormat(String armyListString)
    {
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

    private boolean IsDemarcation(String subString)
    {
        if(subString.contains(BATTLELINE) ||subString.contains(DEDICATED_TRANSPORTS) || subString.contains(CHARACTER))
        {
            return  true;
        }

        return  false;
    }

    private void SaveArmy(String armyToSave)
    {

    }

    private Pair<Integer,Unit> ParseUnit(int offset, String armyListString, Army armyToBuild )
    {
        int newOffset = offset;
        int armyLength = armyListString.length();

        String unitName = "";
        Unit unitToAdd = new Unit();

        while (newOffset < armyLength)
        {
            if(!SkipCharacter(armyListString.charAt(newOffset)))
            {
                if(armyListString.charAt(newOffset) == '(')
                {
                    unitName.trim();
                    Pair<Integer,Integer> pointValue = ParsePointValue(armyListString,newOffset+1);
                    newOffset = pointValue.first;
                    unitToAdd.pointCost = pointValue.second;
                    unitToAdd.unitName = unitName;
                    armyToBuild.units.add(unitToAdd);
                    newOffset +=1;
                    return  new Pair<Integer,Unit>(newOffset,unitToAdd);
                }
                else
                {
                    unitName += armyListString.charAt(newOffset);
                }
            }
            newOffset +=1;
        }
        return  new Pair<Integer,Unit>(newOffset,unitToAdd);
    }

    private Pair<Integer,Integer> ParsePointValue(String ArmyList, int StringOffset)
    {
        int newOffset = StringOffset;
        String pointValue = "";
        while (newOffset <= ArmyList.length())
        {
            char CharToExamine = ArmyList.charAt(newOffset);
            if(Character.isDigit(CharToExamine) || CharToExamine == ')')
            {
                if(CharToExamine == ')')
                {
                    int pointInteger = Integer.parseInt(pointValue);
                    return  new Pair<Integer,Integer>(newOffset +1, pointInteger);
                }
                pointValue += ArmyList.charAt(newOffset);
            }
            newOffset +=1;
        }

        return new Pair<>(-1,-1);
    }

 //   private Pair<Integer,Unit> ParseUnitValues(Unit unit)
 //   {
 //       return Pair<Integer,Unit>(5,unit);
 //   }

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

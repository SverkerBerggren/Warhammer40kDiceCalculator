package com.example.warhammer40kdicecalculator.Parsing;

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
        Army armyToSaveKindOfBeat = new Army();

        stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        while (stringOffset < armyListStringLength)
        {
            String newItem = GetNewLineSubString(stringOffset,armyListString);
            if(!IsDemarcation(newItem))
            {
                stringOffset = ParseUnit(stringOffset,armyListString,armyToSaveKindOfBeat);
            }
            else
            {
                stringOffset += newItem.length();
            }
            stringOffset = RemoveWhiteSpaces(stringOffset,armyListString);
        }
        SaveArmy("gogogagaa");
        return  armyToSaveKindOfBeat;
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
        if( charToCompare == '\n' || charToCompare == '\t' || charToCompare == '\r')
        {
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

    private int ParseUnit(int offset, String armyListString, Army armyToBuild )
    {
        int newOffset = offset;
        int armyLength = armyListString.length();

        String unitName = "";

        while (newOffset < armyLength)
        {
            if(!SkipCharacter(armyListString.charAt(newOffset)))
            {
                if(armyListString.charAt(newOffset) != ')')
                {
                    unitName += armyListString.charAt(newOffset);
                }
                else
                {
                    Unit unitToAdd = new Unit();
                    unitToAdd.unitName = unitName;
                    armyToBuild.units.add(new Unit());
                    return newOffset;
                }
            }
            newOffset +=1;
        }

        return  newOffset;
    }

    private boolean SkipCharacter(char charToExamine)
    {
        if(charToExamine == '•' || charToExamine =='1' || charToExamine =='2' || charToExamine =='3' || charToExamine =='4' || charToExamine =='5'
                || charToExamine =='6'|| charToExamine =='7'|| charToExamine =='8'|| charToExamine =='9' || charToExamine =='(')
        {
            return  true;
        }
        return false;
    }

}

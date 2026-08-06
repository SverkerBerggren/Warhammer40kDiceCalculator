package core.Parsing;



import com.google.gson.annotations.Until;

import core.Abilities.Ability;
import core.DatabaseManager;
import core.DatasheetModeling.Army;
import core.DatasheetModeling.DiceAmount;
import core.DatasheetModeling.Model;
import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Weapon;
import core.Enums.Faction;
import core.Enums.Keyword;
import core.Logging.Logging;
import core.Util.Pair;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Parsing
{

    private Faction armyFaction;
    private DatabaseManager databaseManager;
    //
    private String ConvertArmyListToWahapediaStandard(String armyList)
    {
        return armyList.replace('\'','’').toLowerCase();
    }

    //TODO: add all factions
    private Faction ParseArmyFaction(String armyList)
    {
        String enumArmyList = toEnumName(armyList);
        for( Faction faction : Faction.values())
        {
            String factionName = toEnumName( faction.name());
            if(enumArmyList.contains(factionName))
            {
                return faction;
            }
        }
        return Faction.Unidentified;
    }

    public Army ParseGWListFormat(String armyListString)
    {
        // Waits for the database to be initialized
        synchronized (DatabaseManager.onlineDatabaseLock) {
            while (!DatabaseManager.isInitialized) {
                try {
                    Logging.d("Lock", "Waiting for database...");
                    DatabaseManager.onlineDatabaseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        databaseManager = DatabaseManager.getInstance();
        // armyListString = ConvertArmyListToWahapediaStandard(armyListString);
        armyFaction = ParseArmyFaction(armyListString);

        int stringOffset = 0;
        int armyListStringLength = armyListString.length();
        Army armyToBuild = new Army();


        Pair<Integer,Boolean> offsetAndAttachedBool = ParseFirstUnitAfterDemarcation(stringOffset,armyListString);
        stringOffset = offsetAndAttachedBool.first;
        boolean isAttached = offsetAndAttachedBool.second;

        while (stringOffset < armyListStringLength)
        {
            Pair<Integer,String> offsetAndLine = ParseUntilLineBreak(stringOffset,armyListString);
            if(offsetAndLine.second.contains("Attached Unit "))
            {
                isAttached = true;
                stringOffset = offsetAndLine.first;
            }
            else if(IsDemarcation(offsetAndLine.second))
            {
                stringOffset = offsetAndLine.first;
                continue;
            }
            if(!isAttached)
            {
                Pair<Integer,Unit> returnPair = ParseUnit(stringOffset,armyListString);
                stringOffset = returnPair.first;
                armyToBuild.units.add(returnPair.second);
            }
            else
            {
                Pair<Integer,Unit> returnPair = ParseAttachedUnit(stringOffset,armyListString);
                armyToBuild.units.add(returnPair.second);
                stringOffset = returnPair.first;
                isAttached = false;
            }
        }
        return  armyToBuild;
    }

    private Pair<Integer,Boolean> ParseFirstUnitAfterDemarcation(int offset, String armyListString )
    {
        while(offset < armyListString.length())
        {
            Pair<Integer,String> offsetAndLine = ParseUntilLineBreak(offset,armyListString);
            offset = offsetAndLine.first;
            //TODO: lite ghetto
            // The boolean denotes if it is a attached unit or not
            if(offsetAndLine.second.contains("Attached Unit "))
            {
                return new Pair<>( RemoveWhiteSpaces(offset +1,armyListString),true);
            }
            if(IsDemarcation(offsetAndLine.second) && !offsetAndLine.second.contains("+") )
            {
                return new Pair<>( RemoveWhiteSpaces(offset +1,armyListString),false);
            }
            offset++;
        }
        return new Pair<>( offset ,false);
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

    private Pair<Integer,String> ParseUntilBreak(int offset, String armyList)
    {
        StringBuilder parsedString = new StringBuilder();
        while(offset < armyList.length())
        {
            if(armyList.charAt(offset) == '\r' || armyList.charAt(offset) == '\n'  )
            {
                return new Pair<>(offset,parsedString.toString());
            }
            parsedString.append(armyList.charAt(offset));
            offset += 1;
        }

        return new Pair<>(offset,parsedString.toString());
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
    private boolean IsLogicLessAttribute(String subString)
    {
        return subString.contains(ATTACHED_INDICATOR);
    }
    private final String ATTACHED_INDICATOR = "Attached as: ";
    private boolean IsDemarcation(String subString)
    {
        return subString.contains(BATTLELINE) || subString.contains(ATTACHED_UNIT) || subString.contains(DEDICATED_TRANSPORTS) || subString.contains(CHARACTER) || subString.contains(OTHER_DATASHEETS) || subString.contains(ALLIED_UNITS);
    }
    //TODO: bruh mega ghetto
    private final String BATTLELINE = "BATTLELINE";
    private final String CHARACTER = "CHARACTER";
    private final String DEDICATED_TRANSPORTS = "DEDICATED TRANSPORTS";
    private final String OTHER_DATASHEETS = "OTHER DATASHEETS";
    private final String ALLIED_UNITS = "ALLIED UNITS";
    private final String ATTACHED_UNIT = "Attached Unit";
    private Faction ParseFaction(String factionString)
    {
        // TODO: add all factions
        switch (factionString)
        {
            case "am":
                return Faction.AstraMilitarum;
            case "cd":
                return Faction.ChaosDaemons;
            case "sm":
                return Faction.SpaceMarines;
            default:
                return Faction.Unidentified;
        }
    }
    // GetItem can be called with wahapediaIdHolder as null. That means that it will only search the databases where no id is needed
    // Unit is a bit of a wack parameter
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

    /**
     * Claude shit
     * Sanitizes a string to be a valid Java enum name.
     * Removes "Faction: " prefix, then strips all characters not allowed in enum names.
     */
    public static String toEnumName(String raw) {
        return raw
                .replace("Faction: ", "")
                .replaceAll("[^\\p{L}0-9_]", "");
    }

    /**
     * Attempts to look up a Keyword enum by the given raw string.
     * Returns the matching enum constant, or null if none found.
     */
    public static Keyword keywordFromString(String raw) {
        String enumName = toEnumName(raw);
        try {
            return Keyword.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            return null; // No matching enum constant
        }
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

            Pair<Integer,String> offsetAndItem = ParseUntilBreak(offset,armyList);
            String[] splitString = offsetAndItem.second.split(" \\(");
            String parsedString = splitString[0];
            boolean hasPointValue = splitString.length > 1;
            offset = offsetAndItem.first;
            Pair<DatabaseManager.ItemType,Object> parsedItem = databaseManager.GetItem(parsedString,unit,armyFaction);
            if(parsedItem.first.equals(DatabaseManager.ItemType.WEAPON))
            {
                ArrayList<Weapon> weaponToGive = (ArrayList<Weapon>)parsedItem.second;
                weaponToGive.get(0).active = true;
                for(int i = 1; i < weaponToGive.size();i++)
                {
                    weaponToGive.get(i).active = false;
                }

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
            if(parsedItem.first.equals(DatabaseManager.ItemType.ABILITY))
            {
                //TODO: A open question is how abilities for models should be handled. Right now it is added to both the unit and the model
                unit.GetAbilities().add((Ability)parsedItem.second);
                modelType.GetAbilities().add((Ability)parsedItem.second);
            }
            if(parsedItem.first.equals(DatabaseManager.ItemType.UNIT) || (parsedItem.first.equals(DatabaseManager.ItemType.MODEL) && hasPointValue))
            {
                return offset - offsetAndItem.second.length();
            }
            if(parsedItem.first.equals(DatabaseManager.ItemType.MODEL))
            {
                return ParseModelEquipment(offset +1, armyList,unit, (Model)parsedItem.second,amount);
            }
            if(parsedItem.first.equals(DatabaseManager.ItemType.UNIDENTIFIED) || parsedItem.first.equals(DatabaseManager.ItemType.UNIMPLEMENTED))
            {

                if(!IsDemarcation(parsedString))
                {
                    if(!IsLogicLessAttribute(parsedString))
                    {
                        Logging.d("Unit item parsing","Unidentified item found " + parsedString);
                    }
                }
                else
                {
                    return offset - offsetAndItem.second.length();
                }
                continue;
            }

            offset++;
        }
        return offset;
    }


    private int ParseUnitItems(int offset, String armyList, Unit unit)
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
            // Remove points indicators found in enhancements
            String[] splitString = offsetAndParsedString.second.split(" \\(");
            String parsedString = splitString[0];
            boolean hasPointValue = splitString.length > 1;
            offset = offsetAndParsedString.first;
            Pair<DatabaseManager.ItemType, Object> parsedItem = databaseManager.GetItem(parsedString,unit,armyFaction);
            if(parsedItem.first.equals(DatabaseManager.ItemType.UNIDENTIFIED) || parsedItem.first.equals(DatabaseManager.ItemType.UNIMPLEMENTED))
            {
                if(!IsDemarcation( parsedString))
                {
                    if(!IsLogicLessAttribute(parsedString))
                    {
                        Logging.d("Unit item parsing","Unidentified item found " + parsedString);
                    }
                }
                else
                {
                    return offset - offsetAndParsedString.second.length();
                }
                continue;
            }
            // If the first item is a weapon it is assumed that the unit is a single model unit
            if( parsedItem.first.equals(DatabaseManager.ItemType.WEAPON) && unit.listOfModels.isEmpty())
            {
                unit.singleModelUnit = true;
                // Assumes that a single model units models names corresponds with the unit name
                Model modelToCopy = databaseManager.GetModel( new DatabaseManager.NameFactionKey( unit.unitName , armyFaction));
                // Weird case where units are named in plural despite being a single model unit. Armoured sentinels are an example
                if(modelToCopy == null && unit.unitName.charAt(unit.unitName.length()-1) == 's' )
                {
                    modelToCopy =  databaseManager.GetModel( new DatabaseManager.NameFactionKey(  unit.unitName.substring(0,unit.unitName.length()-1) , armyFaction));
                }
                if(modelToCopy != null)
                {
                    unit.listOfModels.add( modelToCopy.Copy());
                    unit.listOfModels.get(0).weapons.addAll((ArrayList<Weapon>) parsedItem.second);
                }
                else
                {
                    Logging.d("Unit parsing","Single model unit without corresponding model found " + parsedString);
                }

                offset+=1;

                continue;
            }
            if(parsedItem.first.equals(DatabaseManager.ItemType.ABILITY))
            {
                unit.GetAbilities().add((Ability) parsedItem.second);
            }
            if(unit.singleModelUnit && parsedItem.first.equals(DatabaseManager.ItemType.WEAPON))
            {
                // TODO: does not deep copy yikes
                ArrayList<Weapon> weapons = (ArrayList<Weapon>)parsedItem.second;
                weapons.get(0).active = true;
                for( int i = 1; i < weapons.size(); i++)
                {
                    weapons.get(i).active = false;
                }
                for( int i = 0; i < amount; i++ )
                {
                    unit.listOfModels.get(0).weapons.addAll(weapons);
                }
            }
            // Models and units being able to share name causes trouble
            if(parsedItem.first.equals(DatabaseManager.ItemType.UNIT) || (parsedItem.first.equals(DatabaseManager.ItemType.MODEL) && hasPointValue))
            {
                //return ParseUnit(offset - offsetAndParsedString.second.length(),armyList,armyToBuild);
                return offset - offsetAndParsedString.second.length();
            }
            if(parsedItem.first.equals(DatabaseManager.ItemType.MODEL))
            {
                offset = ParseModelEquipment(offset +1,armyList,unit,(Model) parsedItem.second,amount);
                continue;
            }
            // Only warlord seems to be a keyword that can be dynamically added
            if(parsedItem.first.equals(DatabaseManager.ItemType.KEYWORD))
            {
                unit.keywords.add(Keyword.Warlord);
            }
            offset +=1;
        }

        return offset;
    }
    private Pair<Integer,Unit> ParseAttachedUnit(int offset, String armyListString )
    {
        int armyLength = armyListString.length();
        Unit unitToAdd = new Unit();

        while (offset < armyLength)
        {
            Pair<Integer,String> offsetAndParsedString = ParseUntilLineBreak(offset,armyListString);
            if(IsDemarcation( offsetAndParsedString.second))
            {
                return new Pair<>(offset,unitToAdd);
            }
            Pair<Integer,Unit> offsetAndUnit =  ParseUnit(offset,armyListString);
            offset = offsetAndUnit.first;
            unitToAdd = offsetAndUnit.second;
            unitToAdd.unitName = "Attached unit: " + offsetAndUnit.second.unitName;
            int pointCost = offsetAndUnit.second.pointCost;
            while (offset < armyLength )
            {
                int newOffset = offset;
                newOffset = RemoveWhiteSpaces(newOffset,armyListString);
                Pair<Integer,String> offsetAndLine = ParseUntilLineBreak(newOffset,armyListString);
                if(IsDemarcation(offsetAndLine.second) || offsetAndLine.second.contains("Attached Unit "))
                {
                    unitToAdd.pointCost = pointCost;

                    return new Pair<>(newOffset,unitToAdd);
                }
                else
                {
                    Pair<Integer,Unit> unitPair = ParseUnit(newOffset,armyListString);
                    unitToAdd.GetAbilities().addAll(unitPair.second.GetAbilities());
                    unitToAdd.keywords.addAll(unitPair.second.keywords);
                    unitToAdd.listOfModels.addAll(unitPair.second.listOfModels);
                    pointCost += unitPair.second.pointCost;
                    offset = unitPair.first;
                }
            }
        }
        return  new Pair<>(offset,unitToAdd);
    }
    private Pair<Integer,Unit> ParseUnit(int offset, String armyListString )
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

                    offset = ParseUnitItems(offset,armyListString,unitToAdd);

                    Unit databaseUnit = databaseManager.GetUnit(new DatabaseManager.NameFactionKey(unitToAdd.unitName,armyFaction));
                    //This is a bit sus
                    if( databaseUnit != null)
                    {
                        unitToAdd.GetAbilities().addAll(databaseUnit.GetAbilities());
                        unitToAdd.keywords.addAll(databaseUnit.keywords);
                    }

                    return  new Pair<>(offset,unitToAdd);
                }
                else
                {
                    unitName.append(armyListString.charAt(offset));
                }
            }
            offset +=1;
        }
        return  new Pair<>(offset,unitToAdd);
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
                        Logging.d("Item amount",e.getMessage());
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


    public static DiceAmount ParseDiceAmount(String string)
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
                if(component.charAt(i) == 'd' || component.charAt(i) == 'D'  )
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
                            Logging.d("Dice parsing", "Invalid dice suffix, only D3 and D6 exists");
                        }
                    }
                    continue;
                }
                Logging.d("Dice parsing", "Unexpected character found in dice component");
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
                Logging.d("Dice parsing", "Failed to convert dice representation");
            }
        }
        return returnValue;
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

package core.DatasheetModeling;

import core.Logging.Logging;

public class DiceAmount {

    public int baseAmount = 0;

    public int numberOfD6 = 0;

    public  int numberOfD3 = 0;

    public DiceAmount()
    {

    }

    public DiceAmount(int baseAmount, int numberOfD3, int numberOfD6)
    {
        this.baseAmount = baseAmount;
        this.numberOfD3 = numberOfD3;
        this.numberOfD6 = numberOfD6;
    }

    public DiceAmount(DiceAmount other)
    {
        this.baseAmount = other.baseAmount;
        this.numberOfD6 = other.numberOfD6;
        this.numberOfD3 = other.numberOfD3;
    }

    public static DiceAmount Parse(String string)
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

    @Override
    public String toString()
    {
        StringBuilder retValue = new StringBuilder();
        if(baseAmount > 0)
        {
            retValue.append(baseAmount);
        }
        if(numberOfD3 > 0)
        {
            retValue.append(numberOfD3).append(" D3");
        }
        if(numberOfD6 > 0 )
        {
            retValue.append(numberOfD6).append(" D6");
        }
        return  retValue.toString();
    }

    public DiceAmount Copy()
    {
        return new DiceAmount(baseAmount,numberOfD3,numberOfD6);
    }
}

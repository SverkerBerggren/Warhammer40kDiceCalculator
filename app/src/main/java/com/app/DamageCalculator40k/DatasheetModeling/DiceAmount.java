package com.app.DamageCalculator40k.DatasheetModeling;

import androidx.annotation.NonNull;

import com.app.DamageCalculator40k.DamageAmount;

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

    @NonNull
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
            retValue.append(" D").append(numberOfD3);
        }
        if(numberOfD6 > 0 )
        {
            retValue.append(" D").append(numberOfD6);
        }
        return  retValue.toString();
    }

    public DiceAmount Copy()
    {
        return new DiceAmount(baseAmount,numberOfD3,numberOfD6);
    }
}

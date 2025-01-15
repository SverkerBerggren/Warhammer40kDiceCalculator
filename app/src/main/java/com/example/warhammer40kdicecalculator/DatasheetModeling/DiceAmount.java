package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.DamageAmount;

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

    public DiceAmount Copy()
    {
        return new DiceAmount(baseAmount,numberOfD3,numberOfD6);
    }
}

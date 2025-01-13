package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.DamageAmount;

public class AttackAmount {



    public int rawNumberOfAttacks = 0;

    public int numberOfD6 = 0;

    public  int numberOfD3 = 0;

    public AttackAmount()
    {

    }

    public AttackAmount(DamageAmount Damage)
    {
        rawNumberOfAttacks = Damage.rawDamageAmount;
        numberOfD3 = Damage.d3DamageAmount;
        numberOfD6 = Damage.d6DamageAmount;
    }
    public AttackAmount(int rawNumberOfAttacks, int numberOfD3, int numberOfD6)
    {
        this.rawNumberOfAttacks = rawNumberOfAttacks;
        this.numberOfD3 = numberOfD3;
        this.numberOfD6 = numberOfD6;
    }

    AttackAmount(AttackAmount other)
    {
        this.rawNumberOfAttacks = other.rawNumberOfAttacks;
        this.numberOfD6 = other.numberOfD6;
        this.numberOfD3 = other.numberOfD3;
    }

    public AttackAmount Copy()
    {
        return new AttackAmount(rawNumberOfAttacks,numberOfD3,numberOfD6);
    }
}

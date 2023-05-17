package com.egenapp.warhammer40kdicecalculator.DatasheetModeling;

import com.egenapp.warhammer40kdicecalculator.DamageAmount;

public class RangedAttackAmount {



    public int rawNumberOfAttacks;

    public int numberOfD6;

    public  int numberOfD3;

    public  RangedAttackAmount(DamageAmount Damage)
    {

        rawNumberOfAttacks = Damage.rawDamageAmount;
        numberOfD3 = Damage.d3DamageAmount;
        numberOfD6 = Damage.d6DamageAmount;
    }
    public RangedAttackAmount(int rawNumberOfAttacks, int numberOfD3, int numberOfD6)
    {
        this.rawNumberOfAttacks = rawNumberOfAttacks;
        this.numberOfD3 = numberOfD3;
        this.numberOfD6 = numberOfD6;
    }

    RangedAttackAmount(RangedAttackAmount other)
    {
        this.rawNumberOfAttacks = other.rawNumberOfAttacks;
        this.numberOfD6 = other.numberOfD6;
        this.numberOfD3 = other.numberOfD3;
    }
}

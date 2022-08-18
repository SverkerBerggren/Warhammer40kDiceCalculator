package com.example.warhammer40kdicecalculator;

public class DamageAmount {

    int rawDamageAmount = 0;

    int d3DamageAmount;
    int d6DamageAmount;



    DamageAmount(int rawDamageAmount, int d3DamageAmount, int d6DamageAmount)
    {
        this.rawDamageAmount = rawDamageAmount;
        this.d3DamageAmount = d3DamageAmount;
        this.d6DamageAmount = d6DamageAmount;
    }

    DamageAmount(DamageAmount other)
    {
        this.rawDamageAmount = other.rawDamageAmount;
        this.d3DamageAmount = other.d3DamageAmount;
        this.d6DamageAmount = other.d6DamageAmount;
    }
}

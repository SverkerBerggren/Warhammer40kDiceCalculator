package com.example.warhammer40kdicecalculator;

public class RangedAttackAmount {



    public int rawNumberOfAttacks;

    public int numberOfD6;

    public  int numberOfD3;


    RangedAttackAmount(int rawNumberOfAttacks, int numberOfD3, int numberOfD6)
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

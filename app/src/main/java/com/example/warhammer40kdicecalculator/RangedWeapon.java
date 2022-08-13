package com.example.warhammer40kdicecalculator;

public class RangedWeapon {

    public int damage;
    public int ap;
    public int strength;
    public RangedAttackAmount amountOfAttacks;


    public RangedWeapon(int strength, int ap, int damage, RangedAttackAmount amountOfAttacks)
    {
        this.damage = damage;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
    }

}

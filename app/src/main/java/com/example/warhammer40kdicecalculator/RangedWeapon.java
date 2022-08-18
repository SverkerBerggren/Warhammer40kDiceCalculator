package com.example.warhammer40kdicecalculator;

public class RangedWeapon {

    public DamageAmount damageAmount;
    public int ap;
    public int strength;
    public RangedAttackAmount amountOfAttacks;


    public RangedWeapon(int strength, int ap, DamageAmount damageAmount, RangedAttackAmount amountOfAttacks)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
    }

}

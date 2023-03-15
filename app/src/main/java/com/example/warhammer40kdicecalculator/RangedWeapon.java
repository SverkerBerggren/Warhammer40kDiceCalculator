package com.example.warhammer40kdicecalculator;

import java.util.ArrayList;

public class RangedWeapon {
    public String name;
    public DamageAmount damageAmount;
    public int ap;
    public int strength;
    public RangedAttackAmount amountOfAttacks;
    public ArrayList<Ability> weaponRules = new ArrayList<>();


    public RangedWeapon(int strength, int ap, DamageAmount damageAmount, RangedAttackAmount amountOfAttacks)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
    }



    public RangedWeapon(int strength, int ap, DamageAmount damageAmount, RangedAttackAmount amountOfAttacks, ArrayList<Ability> weaponRules)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules;
    }
}

package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DamageAmount;

import java.util.ArrayList;

public class RangedWeapon implements AbilityHolder {
    public String name;
    public DamageAmount damageAmount;
    public int ap;
    public int strength;
    public RangedAttackAmount amountOfAttacks;
    public ArrayList<Ability> weaponRules = new ArrayList<>();


    @Override
    public Ability GetAbility(int index) {

        return weaponRules.get(index);
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return weaponRules.remove(ability);
    }

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


    public RangedWeapon(String name, int strength, int ap, DamageAmount damageAmount, RangedAttackAmount amountOfAttacks, ArrayList<Ability> weaponRules)
    {
        this.name = name;
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules;
    }
}


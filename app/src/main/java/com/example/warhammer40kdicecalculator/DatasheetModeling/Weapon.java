package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DamageAmount;

import java.util.ArrayList;

public class Weapon implements AbilityHolder, DeactivatableInterface {
    public String name;
    public AttackAmount amountOfAttacks = new AttackAmount();
    public DamageAmount damageAmount;
    public int ballisticSkill;
    public int ap;
    public int strength;
    public boolean isMelee = false;
    public boolean active = true;
    public ArrayList<Ability> weaponRules = new ArrayList<>();

    public Weapon()
    {

    }

    @Override
    public Ability GetAbility(int index) {

        return weaponRules.get(index);
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return weaponRules.remove(ability);
    }

    public Weapon(int strength, int ap, DamageAmount damageAmount, AttackAmount amountOfAttacks)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new AttackAmount(amountOfAttacks) ;
    }


    public Weapon Copy( )
    {
        Weapon weaponToReturn = new Weapon();
        weaponToReturn.name = name;
        weaponToReturn.damageAmount = damageAmount.Copy();
        weaponToReturn.ap = ap;
        weaponToReturn.strength = strength;
        weaponToReturn.isMelee = isMelee;
        weaponToReturn.active = active;
        weaponToReturn.amountOfAttacks = amountOfAttacks.Copy();

        weaponToReturn.weaponRules = new ArrayList<>(weaponRules);

        return weaponToReturn;
    }


    public Weapon(int strength, int ap, DamageAmount damageAmount, AttackAmount amountOfAttacks, ArrayList<Ability> weaponRules)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new AttackAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules;
    }


    public Weapon(String name, int strength, int ap, DamageAmount damageAmount, AttackAmount amountOfAttacks, ArrayList<Ability> weaponRules)
    {
        this.name = name;
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new AttackAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules;
    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}


package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DamageAmount;
import com.example.warhammer40kdicecalculator.R;

import java.util.ArrayList;

public class RangedWeapon implements AbilityHolder, DeactivatableInterface {
    public String name;
    public DamageAmount damageAmount;
    public int ap;
    public int strength;
    public boolean IsMelee = false;
    public boolean active = true;
    public RangedAttackAmount amountOfAttacks = new RangedAttackAmount();
    public ArrayList<Ability> weaponRules = new ArrayList<>();

    public RangedWeapon()
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

    public RangedWeapon(int strength, int ap, DamageAmount damageAmount, RangedAttackAmount amountOfAttacks)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new RangedAttackAmount(amountOfAttacks) ;
    }


    public RangedWeapon Copy( )
    {
        RangedWeapon weaponToReturn = new RangedWeapon();
        weaponToReturn.name = name;
        weaponToReturn.damageAmount = damageAmount.Copy();
        weaponToReturn.ap = ap;
        weaponToReturn.strength = strength;
        weaponToReturn.IsMelee = IsMelee;
        weaponToReturn.active = active;
        weaponToReturn.amountOfAttacks = amountOfAttacks.Copy();

        weaponToReturn.weaponRules = new ArrayList<>(weaponRules);

        return weaponToReturn;
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

    @Override
    public void FlipActive() {
        active = !active;
    }
}


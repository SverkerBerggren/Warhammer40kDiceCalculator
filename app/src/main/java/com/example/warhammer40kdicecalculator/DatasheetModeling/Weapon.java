package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;

public class Weapon extends GamePiece implements DeactivatableInterface, WahapediaIdHolder{
    public String name;
    public DiceAmount amountOfAttacks;
    public String wahapediaDataId;
    public DiceAmount damageAmount;
    // Does not take to account N/A yet
    public int ballisticSkill;
    public int ap;
    public int strength;
    public boolean isMelee = false;
    public boolean active = true;
    private AbilityBitField weaponRules = new AbilityBitField(AbilityEnum.MinusOneDamage);

    public Weapon()
    {

    }

    @Override
    public String GetWahapediaId() {
        return wahapediaDataId;
    }

    @Override
    public AbilityBitField GetAbilityBitField() {
        return weaponRules;
    }

    @Override
    public boolean IsActive(AbilityEnum abilityEnum) {
        return weaponRules.IsActive(abilityEnum);
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.WEAPON;
    }

    public Weapon(int strength, int ap, DiceAmount damageAmount, DiceAmount amountOfAttacks)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new DiceAmount(amountOfAttacks) ;
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
        weaponToReturn.wahapediaDataId = wahapediaDataId;

        weaponToReturn.weaponRules = weaponRules.Copy();

        return weaponToReturn;
    }


    public Weapon(int strength, int ap, DiceAmount damageAmount, DiceAmount amountOfAttacks, AbilityBitField weaponRules)
    {
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new DiceAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules.Copy();
    }


    public Weapon(String name, int strength, int ap, DiceAmount damageAmount, DiceAmount amountOfAttacks, AbilityBitField weaponRules)
    {
        this.name = name;
        this.damageAmount = damageAmount;
        this.strength = strength;
        this.ap = ap;
        this.amountOfAttacks = new DiceAmount(amountOfAttacks) ;
        this.weaponRules = weaponRules.Copy();
    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}


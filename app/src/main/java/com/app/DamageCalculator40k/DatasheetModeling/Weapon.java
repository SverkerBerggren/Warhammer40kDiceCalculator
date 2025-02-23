package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;

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
        weaponToReturn.ballisticSkill = ballisticSkill;
        weaponToReturn.amountOfAttacks = amountOfAttacks.Copy();
        weaponToReturn.wahapediaDataId = wahapediaDataId;

        //TODO: not sure if abilities should be copied or not
        weaponToReturn.GetAbilities().addAll( GetAbilities());
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


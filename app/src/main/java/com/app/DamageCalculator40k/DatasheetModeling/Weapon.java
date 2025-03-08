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

    @Override
    public String GetWahapediaId() {
        return wahapediaDataId;
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.WEAPON;
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

        return weaponToReturn;
    }
    @Override
    public void FlipActive() {
        active = !active;
    }
}


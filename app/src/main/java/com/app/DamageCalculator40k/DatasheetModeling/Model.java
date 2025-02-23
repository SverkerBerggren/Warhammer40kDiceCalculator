package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.ModifierHolder;

import java.util.ArrayList;

public class Model extends GamePiece implements ModifierHolder, DeactivatableInterface, WahapediaIdHolder{

    public String wahapediaDataId;
    public String name;
    public int toughness = -1;;
    public int strength = -1;
    public int armorSave = -1;
    //Todo: Fix invulnerable save condition
    public int invulnerableSave = 7;
    public int wounds = -1;
    public int attacks=  -1;

    public boolean active = true;

    private AbilityBitField abilityFlags = new AbilityBitField(AbilityEnum.Blast);

    public ArrayList<Weapon> weapons = new ArrayList<>();

    @Override
    public String GetWahapediaId() {
        return wahapediaDataId;
    }


    @Override
    public boolean IsActive(AbilityEnum abilityEnum) {
        return abilityFlags.IsActive(abilityEnum);
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.MODEL;
    }

    public Model(Model other)
    {
        wahapediaDataId = other.wahapediaDataId;
        toughness = other.toughness;
        strength = other.strength;
        armorSave = other.armorSave;
        invulnerableSave = other.invulnerableSave;
        wounds = other.wounds;
        attacks = other.attacks;
        name = other.name;
        active = other.active;

        abilityFlags = other.abilityFlags.Copy();
        weapons = new ArrayList<>(other.weapons);
     }

    public Model Copy()
    {
        Model modelToReturn = new Model();
        modelToReturn.toughness = toughness;
        modelToReturn.strength = strength;
        modelToReturn.armorSave = armorSave;
        modelToReturn.invulnerableSave = invulnerableSave;
        modelToReturn.wounds = wounds;
        modelToReturn.attacks=  attacks;
        modelToReturn.wahapediaDataId = wahapediaDataId;
        modelToReturn.active = active;
        modelToReturn.name = name;
        modelToReturn.abilityFlags = abilityFlags.Copy();

        ArrayList<Weapon> newList = new ArrayList<>();

        for(Weapon weapon : weapons)
        {
            newList.add(weapon.Copy());
        }

        modelToReturn.weapons = newList;
        return modelToReturn;

    }

    public Model()
    {

    }

    public Model(String name, int toughness, int strength, int armorSave, int invulnerableSave, int wounds, int attacks,
                 AbilityBitField abilityFlags, ArrayList<Weapon> Weapons)
    {
        this.name = name;
        this.toughness = toughness;
        this.strength = strength;
        this.armorSave = armorSave;
        this.invulnerableSave = invulnerableSave;
        this.wounds = wounds;
        this.attacks = attacks;

        this.abilityFlags = abilityFlags;

        this.weapons = Weapons;
    }

    @Override
    public int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount)
    {
        int valueToReturn = 0;
        switch (whatToChange)
        {
            case Strength:
                strength += amount;
                valueToReturn = strength;
                break;
            case Toughness:
                toughness += amount;
                valueToReturn = toughness;
                break;
            case Wounds:
                wounds += amount;
                valueToReturn = wounds;
                break;
            case Attacks:
                attacks += amount;
                valueToReturn = attacks;
                break;
            case ArmorSaves:
                armorSave += amount;
                valueToReturn = armorSave;
                break;
            case InvulnerableSaves:
                invulnerableSave += amount;
                valueToReturn = invulnerableSave;
                break;
        }
        return valueToReturn;
    }

    @Override
    public int GetModifierValue(CompareActivity.UnitAndModelSkill mod)
    {
        int valueToReturn = 0;
        switch (mod)
        {
            case Strength:
                valueToReturn = strength;
                break;
            case Toughness:
                valueToReturn = toughness;
                break;
            case Wounds:
                valueToReturn = wounds;
                break;
            case Attacks:
                valueToReturn = attacks;
                break;
            case ArmorSaves:
                valueToReturn = armorSave;
                break;
            case InvulnerableSaves:
                valueToReturn = invulnerableSave;
                break;
        }
        return valueToReturn;
    }

    @Override
    public void SetModifierValue(CompareActivity.UnitAndModelSkill mod, int amount) {
        switch (mod)
        {
            case Strength:
                strength = amount;
                break;
            case Toughness:
                toughness = amount;
                break;
            case Wounds:
                wounds = amount;
                break;
            case Attacks:
                attacks = amount;
                break;
            case ArmorSaves:
                armorSave = amount;
                break;
            case InvulnerableSaves:
                invulnerableSave = amount;
                break;
        }
    }


    @Override
    public void FlipActive() {
        active = !active;
    }
}

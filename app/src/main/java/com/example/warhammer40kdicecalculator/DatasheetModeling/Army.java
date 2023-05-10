package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.CompareActivity;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.ModifierHolder;

import java.util.ArrayList;

public class Army implements AbilityHolder, ModifierHolder {
    public String name;

    public ArrayList<Unit> units;

    public ArrayList<Ability> abilities;

    public int toughnessModifier;
    public int strengthModifier;
    public int armorSaveModifier;
    public int invulnerableSaveModifier;
    public int woundsModifier;
    public int ballisticSkillModifier;
    public int weaponSkillModifier;
    public int attacksModifier;

    public  Army( String  name,int toughnessModifier, int strengthModifier, int armorSaveModifier,int invulnerableSaveModifier,int woundsModifier, int ballisticSkillModifier, int weaponSkillModifier,
                  int attacksModifier, ArrayList<Unit> units,ArrayList<Ability> abilities )
    {
        this.name = name;

        this.toughnessModifier = toughnessModifier;
        this.strengthModifier = strengthModifier;
        this.armorSaveModifier = armorSaveModifier;
        this.invulnerableSaveModifier = invulnerableSaveModifier;
        this.woundsModifier = woundsModifier;
        this.ballisticSkillModifier = ballisticSkillModifier;
        this.weaponSkillModifier = weaponSkillModifier;
        this.attacksModifier = weaponSkillModifier;

        this.units = units;

        this.abilities = abilities;
    }

    @Override
    public int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount) {
        int valueToReturn = 0;
        switch (whatToChange)
        {
            case WeaponSkill:
                weaponSkillModifier += amount;
                valueToReturn = weaponSkillModifier;
                break;
            case BallisticSkill:
                ballisticSkillModifier += amount;
                valueToReturn = ballisticSkillModifier;
                break;
            case Strength:
                strengthModifier += amount;
                valueToReturn = strengthModifier;
                break;
            case Toughness:
                toughnessModifier += amount;
                valueToReturn = toughnessModifier;
                break;
            case Wounds:
                woundsModifier += amount;
                valueToReturn = woundsModifier;
                break;
            case Attacks:
                attacksModifier += amount;
                valueToReturn = attacksModifier;
                break;
            case ArmorSaves:
                armorSaveModifier += amount;
                valueToReturn = armorSaveModifier;
                break;
            case InvulnerableSaves:
                invulnerableSaveModifier += amount;
                valueToReturn = invulnerableSaveModifier;
                break;
        }
        return valueToReturn;
    }

    @Override
    public int GetModifierValue(CompareActivity.UnitAndModelSkill mod) {
        int valueToReturn = 0;
        switch (mod)
        {
            case WeaponSkill:
                valueToReturn = weaponSkillModifier;
                break;
            case BallisticSkill:
                valueToReturn = ballisticSkillModifier;
                break;
            case Strength:
                valueToReturn = strengthModifier;
                break;
            case Toughness:
                valueToReturn = toughnessModifier;
                break;
            case Wounds:
                valueToReturn = woundsModifier;
                break;
            case Attacks:
                valueToReturn = attacksModifier;
                break;
            case ArmorSaves:
                valueToReturn = armorSaveModifier;
                break;
            case InvulnerableSaves:
                valueToReturn = invulnerableSaveModifier;
                break;
        }
        return valueToReturn;
    }

    @Override
    public void SetModifierValue(CompareActivity.UnitAndModelSkill mod, int amount) {
        switch (mod)
        {
            case WeaponSkill:
                weaponSkillModifier = amount;
                break;
            case BallisticSkill:
                ballisticSkillModifier = amount;
                break;
            case Strength:
                strengthModifier = amount;
                break;
            case Toughness:
                toughnessModifier = amount;
                break;
            case Wounds:
                woundsModifier = amount;
                break;
            case Attacks:
                attacksModifier = amount;
                break;
            case ArmorSaves:
                armorSaveModifier = amount;
                break;
            case InvulnerableSaves:
                invulnerableSaveModifier = amount;
                break;
        }
    }

    @Override
    public Ability GetAbility(int index) {
        return null;
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return false;
    }
}

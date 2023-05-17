package com.egenapp.warhammer40kdicecalculator.DatasheetModeling;

import com.egenapp.warhammer40kdicecalculator.Abilities.Ability;
import com.egenapp.warhammer40kdicecalculator.CompareActivity;
import com.egenapp.warhammer40kdicecalculator.MeleeWeapons;
import com.egenapp.warhammer40kdicecalculator.ModifierHolder;

import java.util.ArrayList;

public class Model implements AbilityHolder, ModifierHolder, DeactivatableInterface {
    //public int ap;
    public String name;
    public int toughness;
    public int strength;
    public int armorSave;
    public int invulnerableSave;
    public int wounds;
    public int ballisticSkill;
    public int weaponSkill;
    public int attacks;

    public boolean active = true;

    public ArrayList<Ability> listOfAbilites = new ArrayList<>();

    public ArrayList<RangedWeapon> listOfRangedWeapons = new ArrayList<>();

    public ArrayList<MeleeWeapons> listOfMeleeWeapons = new ArrayList<>();

    @Override
    public Ability GetAbility(int index) {
        return listOfAbilites.get(index);
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return listOfAbilites.remove(ability);
    }

    public Model(Model other)
    {
        toughness = other.toughness;
        strength = other.strength;
        armorSave = other.armorSave;
        invulnerableSave = other.invulnerableSave;
        wounds = other.wounds;
        ballisticSkill = other.ballisticSkill;
        weaponSkill = other.weaponSkill;
        attacks = other.attacks;

        listOfAbilites = new ArrayList<>(other.listOfAbilites);

        listOfRangedWeapons = new ArrayList<>(other.listOfRangedWeapons);

        listOfMeleeWeapons = new ArrayList<>(other.listOfMeleeWeapons);

     }
    public Model()
    {

    }

    public Model(String name, int toughness, int strength, int armorSave, int invulnerableSave, int wounds, int ballisticSkill, int weaponSkill, int attacks,
                 ArrayList<Ability> listOfAbilites,ArrayList<RangedWeapon> listOfRangedWeapons, ArrayList<MeleeWeapons> listOfMeleeWeapons)
    {
        this.name = name;
        this.toughness = toughness;
        this.strength = strength;
        this.armorSave = armorSave;
        this.invulnerableSave = invulnerableSave;
        this.wounds = wounds;
        this.ballisticSkill = ballisticSkill;
        this.weaponSkill = weaponSkill;
        this.attacks = attacks;

        this.listOfAbilites = listOfAbilites;

        this.listOfRangedWeapons = listOfRangedWeapons;
        this.listOfMeleeWeapons = listOfMeleeWeapons;
    }

    @Override
    public int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount)
    {
        int valueToReturn = 0;
        switch (whatToChange)
        {
            case WeaponSkill:
                weaponSkill += amount;
                valueToReturn = weaponSkill;
                break;
            case BallisticSkill:
                ballisticSkill += amount;
                valueToReturn = ballisticSkill;
                break;
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
            case WeaponSkill:
                valueToReturn = weaponSkill;
                break;
            case BallisticSkill:
                valueToReturn = ballisticSkill;
                break;
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
            case WeaponSkill:
                weaponSkill = amount;
                break;
            case BallisticSkill:
                 ballisticSkill = amount;
                break;
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

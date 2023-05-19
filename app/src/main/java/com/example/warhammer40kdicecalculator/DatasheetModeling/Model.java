package com.example.warhammer40kdicecalculator.DatasheetModeling;

import android.widget.EditText;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.CompareActivity;
import com.example.warhammer40kdicecalculator.MeleeWeapons;
import com.example.warhammer40kdicecalculator.ModifierHolder;

import java.util.ArrayList;

public class Model implements AbilityHolder, ModifierHolder, DeactivatableInterface {
    //public int ap;
    public String name;
    public int toughness = -1;;
    public int strength = -1;
    public int armorSave = -1;
    public int invulnerableSave = 7;
    public int wounds = -1;
    public int ballisticSkill = -1;
    public int weaponSkill = -1;
    public int attacks=  -1;

    public boolean active = true;

    public ArrayList<Ability> listOfAbilites = new ArrayList<>();

    public ArrayList<RangedWeapon> listOfRangedWeapons = new ArrayList<>();

    public ArrayList<MeleeWeapons> listOfMeleeWeapons = new ArrayList<>();

    public void CopyStats(Model OtherModel)
    {
        OtherModel.toughness = toughness;
        OtherModel.strength = strength;
        OtherModel.armorSave = armorSave;
        OtherModel.invulnerableSave = invulnerableSave;
        OtherModel.wounds = wounds;
        OtherModel.ballisticSkill = ballisticSkill;
        OtherModel.weaponSkill = weaponSkill;
        OtherModel.attacks = attacks;

    }

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
        name = other.name;
        active = other.active;

        listOfAbilites = new ArrayList<>(other.listOfAbilites);

        listOfRangedWeapons = new ArrayList<>(other.listOfRangedWeapons);

        listOfMeleeWeapons = new ArrayList<>(other.listOfMeleeWeapons);

     }

    public Model Copy()
    {
        Model modelToReturn = new Model();
        modelToReturn.toughness = toughness;
        modelToReturn.strength = strength;
        modelToReturn.armorSave = armorSave;
        modelToReturn.invulnerableSave = invulnerableSave;
        modelToReturn.wounds = wounds;
        modelToReturn.ballisticSkill = ballisticSkill;
        modelToReturn.weaponSkill = weaponSkill;
        modelToReturn.attacks=  attacks;

        modelToReturn.listOfAbilites = new ArrayList<>(listOfAbilites);

        ArrayList<RangedWeapon> newList = new ArrayList<>();

        for(RangedWeapon weapon : listOfRangedWeapons)
        {
            newList.add(weapon.Copy());
        }

        modelToReturn.listOfRangedWeapons = newList;

        modelToReturn.listOfMeleeWeapons = new ArrayList<>();

        return modelToReturn;

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

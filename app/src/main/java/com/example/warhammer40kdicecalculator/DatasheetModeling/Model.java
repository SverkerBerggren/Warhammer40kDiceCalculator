package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.MeleeWeapons;

import java.util.ArrayList;

public class Model implements AbilityHolder{
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

}

package com.example.warhammer40kdicecalculator;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Model {
    //public int ap;
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



    public Model( Model other)
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
}

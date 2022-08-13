package com.example.warhammer40kdicecalculator;

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


}

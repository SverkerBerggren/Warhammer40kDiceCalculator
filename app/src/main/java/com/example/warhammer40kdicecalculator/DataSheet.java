package com.example.warhammer40kdicecalculator;

public class DataSheet {

    public int ballisticSkill;

    public int NumberOfAttacks;

    public int damage;

    public int ap;

    public int Strength;

    public int armorSave;

    public int invulnerableSave = -1;

    public int numberOfWounds;

    public int toughness;

    public  boolean hammerOfEmperor = false;

    public DataSheet(int ballisticSkill,int toughness, int numberOfAttacks,int numberOfWounds, int damage, int ap, int strength, int armorSave, int invulnerableSave)
    {
        this.ballisticSkill = ballisticSkill;
        this.NumberOfAttacks = numberOfAttacks;
        this.damage = damage;
        this.ap = ap;
        this.Strength = strength;

        this.armorSave = armorSave;
        this.toughness = toughness;
        this.invulnerableSave = invulnerableSave;
        this.numberOfWounds = numberOfWounds;
    }

}

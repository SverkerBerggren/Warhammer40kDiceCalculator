package com.app.DamageCalculator40k.Enums;

public enum StatModifier {
    HitRoll("BS"),
    WoundRoll("Wr"),
    ArmorSave("Sv"),
    InvulnerableSave("ISV"),
    Toughness("T"),
    Strength("S"),
    WoundAmount("W"),
    Attacks("A");

    private String abbreviation;
    StatModifier(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }
    public String GetAbbreviation()
    {
        return  abbreviation;
    }
}

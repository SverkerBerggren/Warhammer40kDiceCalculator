package com.example.warhammer40kdicecalculator.Enums;


import com.example.warhammer40kdicecalculator.BitFunctionality.BitEnum;
import com.example.warhammer40kdicecalculator.Parsing.Parsing;

// TODO: add all factions
public enum Faction implements BitEnum<Faction> {
    AstraMilitarum,

    Unidentified;

    private final static Faction[] factions = Faction.values();

    @Override
    public Faction[] GetValues() {
        return factions;
    }
}

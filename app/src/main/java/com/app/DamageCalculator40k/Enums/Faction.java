package com.app.DamageCalculator40k.Enums;


import com.app.DamageCalculator40k.BitFunctionality.BitEnum;
import com.app.DamageCalculator40k.Parsing.Parsing;

// TODO: add all factions
public enum Faction implements BitEnum<Faction> {
    AstraMilitarum,
    ChaosDemons,
    SpaceMarines,

    Unidentified;

    private final static Faction[] factions = Faction.values();

    @Override
    public Faction[] GetValues() {
        return factions;
    }
}

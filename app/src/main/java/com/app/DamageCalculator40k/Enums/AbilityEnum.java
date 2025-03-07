package com.app.DamageCalculator40k.Enums;

import com.app.DamageCalculator40k.BitFunctionality.BitEnum;

// TODO: tjockt wack namn
public enum AbilityEnum implements BitEnum<AbilityEnum> {
    MinusOneDamage,
    MinusOneToWound,
    ReRollHits,
    ReRollOnes,
    AntiKeyword,
    Blast,
    DevastatingWounds,
    Heavy,
    IgnoresCover,
    LethalHits,
    RapidFire,
    SustainedHits,
    Torrent,
    TwinLinked,
    Unimplemented;

    private final static AbilityEnum[] enums = values();
    @Override
    public AbilityEnum[] GetValues() {
        return enums;
    }
}

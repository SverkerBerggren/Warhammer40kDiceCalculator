package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;

public class SustainedHits extends Ability {

    public static String baseName = "sustained hits";
    private int extraHits = 0;

    public SustainedHits(int extraHits) {
        super(baseName + " " + extraHits);
        this.extraHits = extraHits;
    }
}

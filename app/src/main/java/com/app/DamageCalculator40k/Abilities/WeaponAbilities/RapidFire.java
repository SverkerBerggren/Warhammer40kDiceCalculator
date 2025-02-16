package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;

public class RapidFire extends Ability {
    public static String baseName = "rapid fire";
    private DiceAmount extraShots = new DiceAmount();

    public RapidFire(DiceAmount amount) {
        super(baseName + " " + amount);
        this.extraShots = amount;
    }
}

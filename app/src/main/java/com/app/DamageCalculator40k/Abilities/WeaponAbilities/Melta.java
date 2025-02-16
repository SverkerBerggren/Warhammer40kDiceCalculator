package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;

public class Melta extends Ability {
    public static String baseName = "melta";
    private DiceAmount diceAmount = new DiceAmount();
    public Melta(DiceAmount diceAmount) {
        super(baseName);
        this.diceAmount = diceAmount;
    }
}

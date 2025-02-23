package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

public class RapidFire extends Ability {
    public static String baseName = "rapid fire";
    private DiceAmount extraShots = new DiceAmount();

    public RapidFire(DiceAmount amount) {
        super(baseName + " " + amount, AbilityTiming.IncreaseAttacks);
        this.extraShots = amount;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {

    }
}

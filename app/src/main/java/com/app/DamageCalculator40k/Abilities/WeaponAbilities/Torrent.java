package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

public class Torrent extends Ability {
    public static String baseName = "torrent";

    public Torrent() {
        super(baseName, AbilityTiming.Torrent);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {

    }
}

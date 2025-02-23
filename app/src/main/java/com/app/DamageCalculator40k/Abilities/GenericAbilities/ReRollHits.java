package com.app.DamageCalculator40k.Abilities.GenericAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class ReRollHits extends Ability {

    public static String baseName = "ReRollHits";
    public ReRollHits()
    {
        super(baseName, AbilityTiming.ReRollHits);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if (diceResult.result < requiredRoll && !diceResult.hasBeenReRolled) {
            diceResult.result = ThreadLocalRandom.current().nextInt(1,7);
            diceResult.hasBeenReRolled = true;
        }
    }
}

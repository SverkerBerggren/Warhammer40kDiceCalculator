package com.app.DamageCalculator40k.Abilities.GenericAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class ReRollOnes extends Ability {

    public static String baseName = "ReRollOnes";

    public ReRollOnes()
    {
        super(baseName,AbilityTiming.ReRollHits);
    }

    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result == 1 && !diceResult.hasBeenReRolled)
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            diceResult.hasBeenReRolled = true;
        }
    }
}

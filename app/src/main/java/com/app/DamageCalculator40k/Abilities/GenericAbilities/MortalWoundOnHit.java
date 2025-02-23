package com.app.DamageCalculator40k.Abilities.GenericAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class MortalWoundOnHit extends Ability {
    public static String baseName = "MortalWoundOnHit";
    private int resultToTriggerOn = 0;
    public MortalWoundOnHit(int resultToTriggerOn)
    {
        super(baseName, AbilityTiming.TriggerOnHitRoll);
        this.resultToTriggerOn = resultToTriggerOn;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result >= resultToTriggerOn)
        {
            attackResults.mortalWounds +=1;
        }
    }
}

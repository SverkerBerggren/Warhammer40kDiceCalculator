package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.MetricsOfAttacking;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollHits extends Ability{

    public static String baseName = "ReRollHits";
    public ReRollHits()
    {
        super(baseName);
    }

    @Override
    public void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

        if (diceResult.result < requiredResult.get()) {
            DiceResult newDiceRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

            diceResult.result = newDiceRoll.result;

        }
    }
}

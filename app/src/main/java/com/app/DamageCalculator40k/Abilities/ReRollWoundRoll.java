package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.MetricsOfAttacking;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollWoundRoll extends Ability{

    public static String baseName = "ReRollWoundRoll";
    public ReRollWoundRoll()
    {
        super(baseName);
    }
    @Override
    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        if(diceResult.result < requiredResult.get())
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);

        }
    }
}

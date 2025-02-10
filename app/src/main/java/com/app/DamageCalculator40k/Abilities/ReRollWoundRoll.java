package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollWoundRoll extends Ability{

    public ReRollWoundRoll()
    {
        super("ReRollWoundRoll");
    }
    @Override
    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        if(diceResult.result < requiredResult.get())
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);

        }
    }
}

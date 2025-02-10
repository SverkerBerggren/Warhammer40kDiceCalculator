package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.atomic.AtomicInteger;

public class TransHuman4 extends Ability{

    public TransHuman4()
    {
        super("TransHuman4");
    }

    @Override
    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {



        if(requiredResult.get() < 4)
        {
            requiredResult.set(4);
        }
    }
}

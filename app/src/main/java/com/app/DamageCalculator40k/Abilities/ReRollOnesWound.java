package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollOnesWound extends Ability{

    public ReRollOnesWound()
    {
        super("ReRollOnesWound");
    }

    @Override
    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        if(diceResult.result == 1
        )
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);

        }
    }
}

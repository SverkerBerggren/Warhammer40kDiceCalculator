package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.atomic.AtomicInteger;

public class IncreaseAp1 extends Ability{

    public IncreaseAp1()
    {
        super("IncreaseAp1");
    };
    @Override
    public void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {
        //metricsOfAttacking.ap -=1;
    }
}

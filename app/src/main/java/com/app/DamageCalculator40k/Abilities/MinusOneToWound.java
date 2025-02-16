package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.MetricsOfAttacking;

import java.util.concurrent.atomic.AtomicInteger;

public class MinusOneToWound extends Ability{

    public static String baseName = "MinusOneToWound";
    public MinusOneToWound()
    {
        super("MinusOneToWound");
    }

    @Override
    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        requiredResult.set(requiredResult.get()+1);
    }
}

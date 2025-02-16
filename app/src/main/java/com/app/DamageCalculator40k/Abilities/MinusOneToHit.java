package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.MetricsOfAttacking;

import java.util.concurrent.atomic.AtomicInteger;

public class MinusOneToHit extends Ability{
    
    public static String baseName = "MinusOneToHit";
    public MinusOneToHit()
    {
        super(baseName );
    }

    @Override
    public void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        requiredResult.set(requiredResult.get()+1);
    }
}

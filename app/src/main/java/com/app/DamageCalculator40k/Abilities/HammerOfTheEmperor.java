package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.atomic.AtomicInteger;

public class HammerOfTheEmperor extends Ability{

    public HammerOfTheEmperor()
    {
        super("Hammer of the Emperor");
    }

    public  void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult)
    {
        if(diceResult.result == 6)
        {
            diceResult.result = 0;
            metricsOfAttacking.wounds +=1;
        }
    }
}

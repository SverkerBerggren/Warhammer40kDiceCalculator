package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

public class MinusOneDamage extends Ability{

    public MinusOneDamage()
    {
        super("MinusOneDamage");
    }

    @Override
    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken) {

        if(damageToBeTaken > 1)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}

package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.MetricsOfAttacking;

public class MinusOneDamage extends Ability{

    public static String baseName = "MinusOneDamage";
    public MinusOneDamage()
    {
        super(baseName);
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

package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.concurrent.ThreadLocalRandom;

public class FeelNoPain6 extends Ability {


    public FeelNoPain6()
    {
        super("FeelNoPain6");
    }

    @Override
    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken) {
       int damageToReduce = 0;
       for(int i = 0; i < damageToBeTaken; i++)
       {
           DiceResult feelNoPainDice = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

           if(feelNoPainDice.result== 6)
           {
               damageToReduce += 1;
           }
       }
       return damageToReduce;
    }
}

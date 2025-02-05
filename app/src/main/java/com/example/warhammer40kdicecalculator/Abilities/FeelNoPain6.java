package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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

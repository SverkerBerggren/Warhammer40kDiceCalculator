package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollOnes extends Ability{


    //ej klar fet stub

    public ReRollOnes()
    {
        super("ReRollOnes");
    }

    public  void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult)
    {


        if(diceResult.result == 1)
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        }


    }

    @Override
    public void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    public   void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult)
    {

    }

    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken)
    {
        return 0;
    }

    public   void rollNumberOfShots(List<DiceResult> diceResultList, MetricsOfAttacking metricsOfAttacking)
    {
        int currentAmountOfAttacks = 0;

        for(int i = 0; i < diceResultList.size(); i++)
        {
            currentAmountOfAttacks += diceResultList.get(i).result;
        }
        if(diceResultList.get(0).isD3Roll)
        {
            int expectedAmount = 2 * diceResultList.size();

            if( currentAmountOfAttacks < expectedAmount)
            {
                for(int k = 0; k < diceResultList.size(); k++)
                {
                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 3 +1 ));

                    diceResultList.get(k).result = diceResult.result;
                }
            }
        }

        if(diceResultList.get(0).isD6Roll)
        {
            double expectedAmount = 3.5 * diceResultList.size();

            if( currentAmountOfAttacks < expectedAmount)
            {
                for(int k = 0; k < diceResultList.size(); k++)
                {
                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));

                    diceResultList.get(k).result = diceResult.result;
                }
            }
        }

    }

}

package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollAmountOfHits extends Ability {

    public ReRollAmountOfHits()
    {
        super("ReRollAmountOfHits");
    }

    public   void rollNumberOfShots( List<DiceResult>  diceResultList, MetricsOfAttacking metricsOfAttacking)
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

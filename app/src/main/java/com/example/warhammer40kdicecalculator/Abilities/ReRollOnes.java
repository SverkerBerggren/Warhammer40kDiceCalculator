package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReRollOnes extends Ability{


    //ej klar fet stub

    public ReRollOnes()
    {
        super("ReRollOnes");
    }

    public  void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {


        if(diceResult.result == 1)
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        }


    }
    public   void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void rollNumberOfShots(List<DiceResult> diceResultList, MetricsOfAttacking metricsOfAttacking)
    {
        //    if(diceResult.isD3Roll)
        //    {
        //        if(diceResult.result <3)
        //        {
        //            DiceResult reRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));
//
        //            diceResult.result = reRoll.result;
        //        }
        //    }
        //    if(diceResult.isD6Roll)
        //    {
        //        if(diceResult.result <4)
        //        {
        //            DiceResult reRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));
//
        //            diceResult.result = reRoll.result;
        //        }
        //    }

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

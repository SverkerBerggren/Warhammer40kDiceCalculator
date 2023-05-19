package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;

public class HammerOfTheEmperor extends Ability{

    public HammerOfTheEmperor()
    {
        super("Hammer of the Emperor");
    }

    public  void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {
        if(diceResult.result == 6)
        {
            diceResult.result = 1;
            metricsOfAttacking.wounds +=1;
        }


    }
    public   void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

}

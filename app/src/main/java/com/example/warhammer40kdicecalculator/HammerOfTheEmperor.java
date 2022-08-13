package com.example.warhammer40kdicecalculator;

public class HammerOfTheEmperor extends Ability{

    public  void hitRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking)
    {
        if(diceResult == 6)
        {
            diceResult = 1;
        }
        metricsOfAttacking.wounds +=1;

    }
    public   void woundRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void saveRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void rollNumberOfShots(Integer diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

}

package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
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

    @Override
    public void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    public   void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult)
    {

    }

    @Override
    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken)
    {
        return 0;
    }

    public   void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

}

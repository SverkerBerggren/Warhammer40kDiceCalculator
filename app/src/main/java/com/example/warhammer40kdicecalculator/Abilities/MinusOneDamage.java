package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MinusOneDamage extends Ability{

    public MinusOneDamage()
    {
        super("MinusOneDamage");
    }

    @Override
    public void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    @Override
    public void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    @Override
    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

    }

    @Override
    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

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

    @Override
    public void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking) {

    }
}

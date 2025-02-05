package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TransHuman4 extends Ability{

    public TransHuman4()
    {
        super("TransHuman4");
    }

    @Override
    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {



        if(requiredResult.get() < 4)
        {
            requiredResult.set(4);
        }
    }
}

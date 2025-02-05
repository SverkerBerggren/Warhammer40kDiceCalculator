package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollOnesWound extends Ability{

    public ReRollOnesWound()
    {
        super("ReRollOnesWound");
    }

    @Override
    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {


        if(diceResult.result == 1
        )
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);

        }
    }
}

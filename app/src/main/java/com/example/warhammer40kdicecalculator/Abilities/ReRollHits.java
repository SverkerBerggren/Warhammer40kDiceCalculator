package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReRollHits extends Ability{
    public ReRollHits()
    {
        super("ReRollHits");
    }

    @Override
    public void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult) {

        if (diceResult.result < requiredResult.get()) {
            DiceResult newDiceRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

            diceResult.result = newDiceRoll.result;

        }
    }
}

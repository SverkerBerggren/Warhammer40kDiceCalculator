package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;

public class Blast extends Ability{


    public Blast()
    {
        super("Blast");
    }

    @Override
    public void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int requiredResult) {

    }

    @Override
    public void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

    }

    @Override
    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken) {
        return 0;
    }

    @Override
    public void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking) {

    }
}

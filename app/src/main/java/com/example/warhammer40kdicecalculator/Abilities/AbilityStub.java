package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;

public class AbilityStub extends Ability{
    @Override
    public void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

    }

    @Override
    public void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

    }

    @Override
    public void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

    }

    @Override
    public void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking) {

    }
    public AbilityStub(String name)
    {
        super("");

        if(name.contains(": no behaviour"))
        {

            this.name = name;
        }
        else
        {
            this.name = name + ": no behaviour";
        }


    }

}
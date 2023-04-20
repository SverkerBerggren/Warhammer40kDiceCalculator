package com.example.warhammer40kdicecalculator;

import com.google.gson.InstanceCreator;

import java.util.List;

public abstract class Ability  {

    public String name;

    public abstract void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking);

    Ability(String name)
    {
        this.name = name;
    }


    public static Ability getAbilityType(String name)
    {
        if(name.equals("HammerOfTheEmperor"))
        {
            return new HammerOfTheEmperor();
        }

        if(name.equals("ReRollAmountOfHits"))
        {
            return new ReRollAmountOfHits();
        }

        return null;
    }
}

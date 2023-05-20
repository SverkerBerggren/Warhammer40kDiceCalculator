package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Dakka extends Ability {
    public int dakkaAmount = -1;
    public Dakka()
    {
        super("Dakka");
    }
    public  void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {


    }
    public   void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }

    public   void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking)
    {

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
        return 0;
    }

    public   void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking)
    {

    }
}
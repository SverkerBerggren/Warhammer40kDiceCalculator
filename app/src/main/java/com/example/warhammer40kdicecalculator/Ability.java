package com.example.warhammer40kdicecalculator;

public abstract class Ability {

    public abstract void hitRollAbility(int diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void woundRollAbility(int diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void saveRollAbility(int diceResult, MetricsOfAttacking metricsOfAttacking);
}

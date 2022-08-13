package com.example.warhammer40kdicecalculator;

public abstract class Ability {

    public abstract void hitRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void woundRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void saveRollAbility(Integer diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void rollNumberOfShots(Integer diceResult, MetricsOfAttacking metricsOfAttacking);
}

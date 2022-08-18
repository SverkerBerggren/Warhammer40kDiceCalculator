package com.example.warhammer40kdicecalculator;

public abstract class Ability {

    public abstract void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void rollNumberOfShots(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);
}

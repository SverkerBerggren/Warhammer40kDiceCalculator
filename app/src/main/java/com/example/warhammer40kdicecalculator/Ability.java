package com.example.warhammer40kdicecalculator;

import java.util.List;

public abstract class Ability {

    public abstract void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract  void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking);
}

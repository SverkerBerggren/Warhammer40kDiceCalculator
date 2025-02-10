package com.app.DamageCalculator40k.Abilities;

import com.app.DamageCalculator40k.DatasheetModeling.DeactivatableInterface;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DiceResult;
import com.app.DamageCalculator40k.MetricsOfAttacking;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ability implements DeactivatableInterface {

    public String name;
    public boolean active = true;


    public void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult){};

    public void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult){};

    public void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult){};

    public void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult){};

    public int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken){return 0;};

    public void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking){};

    public Ability(String name)
    {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ability ability = (Ability) o;
        return Objects.equals(name, ability.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, active);
    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}

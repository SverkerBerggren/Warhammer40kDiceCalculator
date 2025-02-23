package com.app.DamageCalculator40k.Abilities;

import androidx.annotation.NonNull;

import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DatasheetModeling.DeactivatableInterface;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

import java.util.List;
import java.util.Objects;

public abstract class Ability implements DeactivatableInterface {

    public String name;
    public boolean active = true;

    private final AbilityTiming abilityTiming;

    public AbilityTiming GetAbilityTiming()
    {
        return abilityTiming;
    }

    public abstract void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions);

    public void rollNumberOfShots(List<DiceResult> diceResult){};

    public Ability(String name, AbilityTiming abilityTiming)
    {
        this.name = name;
        this.abilityTiming = abilityTiming;
    }

    @NonNull
    @Override
    public String toString()
    {
        return name;
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

package core.Abilities;

import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DatasheetModeling.DeactivatableInterface;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.AttackResults;
import core.Enums.AbilityTiming;

import java.util.List;
import java.util.Objects;

public abstract class Ability implements DeactivatableInterface {

    public String name;
    // TODO this one is a bit sus, should maybe be moved but it is here for testing
    public String description;
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

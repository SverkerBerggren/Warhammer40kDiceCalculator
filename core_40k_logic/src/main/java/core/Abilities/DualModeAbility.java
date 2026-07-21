package core.Abilities;

import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public abstract class DualModeAbility extends Ability {
    public DualModeAbility(String name, AbilityTiming abilityTiming) {
        super(name, abilityTiming);
    }
    protected boolean boosted = false;
    boolean isBoosted()
    {
        return boosted;
    };
    void setBoosted(boolean boosted)
    {
        this.boosted = boosted;
    };
    void FlipBoosted() { setBoosted(!isBoosted()); }

}
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
    protected boolean isBoosted = false;

    public boolean isBoosted() { return isBoosted; }
    public void setBoosted(boolean boosted) { this.isBoosted = boosted; }
    public void FlipBoosted() { setBoosted(!isBoosted()); }

}
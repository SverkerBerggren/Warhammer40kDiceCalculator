package core.Abilities.WeaponAbilities;

import core.Abilities.DualModeAbility;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class Melta extends  DualModeAbility {
    public static String baseName = "melta";
    private  int extraWounds;
    public Melta(int extraWounds) {
        super(baseName, AbilityTiming.IncreaseDamage);
        this.extraWounds = extraWounds;
    }

    public Melta() {
        super(baseName, AbilityTiming.IncreaseDamage);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {
        if(isBoosted)
        {
            attackResults.damage += extraWounds;
        }
    }

}

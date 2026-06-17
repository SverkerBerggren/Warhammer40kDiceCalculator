package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class ExtraAttacks extends Ability {
    public static String baseName = "extra attacks";

    public ExtraAttacks() {
        super(baseName, AbilityTiming.DetermineActiveState);
    }
    //TODO: the UX for enabling melee weapons should be more advanced
    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {

    }
}

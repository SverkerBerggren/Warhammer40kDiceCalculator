package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class Blast extends Ability {

    public static String baseName = "blast";

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {
        attackResults.attacks += defendingSources.unit.listOfModels.size() /5;
    }

    public Blast()
    {
        super(baseName, AbilityTiming.IncreaseAttacks);
    }
}

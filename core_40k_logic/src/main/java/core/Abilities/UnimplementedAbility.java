package core.Abilities;

import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class UnimplementedAbility extends Ability{
    public UnimplementedAbility(String name)
    {
        super(name, AbilityTiming.Unimplemented);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {

    }
}

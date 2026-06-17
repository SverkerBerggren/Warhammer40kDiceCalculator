package core.Abilities.GenericAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class MortalWoundOnHit extends Ability {
    public static String baseName = "MortalWoundOnHit";
    private int resultToTriggerOn = 0;
    public MortalWoundOnHit(int resultToTriggerOn)
    {
        super(baseName, AbilityTiming.TriggerOnHitRoll);
        this.resultToTriggerOn = resultToTriggerOn;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result >= resultToTriggerOn)
        {
            attackResults.mortalWounds +=1;
        }
    }
}

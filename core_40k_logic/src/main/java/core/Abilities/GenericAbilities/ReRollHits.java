package core.Abilities.GenericAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.AttackResults;
import core.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class ReRollHits extends Ability {

    public static String baseName = "ReRollHits";
    public ReRollHits()
    {
        super(baseName, AbilityTiming.ReRollHits);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if (diceResult.result < requiredRoll && !diceResult.hasBeenReRolled) {
            diceResult.result = ThreadLocalRandom.current().nextInt(1,7);
            diceResult.hasBeenReRolled = true;
        }
    }
}

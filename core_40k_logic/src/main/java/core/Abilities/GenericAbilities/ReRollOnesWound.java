package core.Abilities.GenericAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.AttackResults;
import core.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class ReRollOnesWound extends Ability {

    public final static String baseName = "ReRollOnesWound";
    public ReRollOnesWound()
    {
        super(baseName, AbilityTiming.ReRollWounds);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result == 1 && !diceResult.hasBeenReRolled)
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            diceResult.hasBeenReRolled = true;
        }
    }
}

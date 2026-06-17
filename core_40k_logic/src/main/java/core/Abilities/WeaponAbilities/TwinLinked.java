package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

import java.util.concurrent.ThreadLocalRandom;

public class TwinLinked extends Ability {
    public static String baseName = "twin-linked";
    public TwinLinked() {
        super(baseName, AbilityTiming.ReRollWounds);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result < requiredRoll && !diceResult.hasBeenReRolled )
        {
            diceResult.result = ThreadLocalRandom.current().nextInt(1,7);
        }

    }
}

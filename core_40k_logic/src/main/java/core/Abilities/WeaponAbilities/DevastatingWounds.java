package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.RollingLogic;
import core.Enums.AbilityTiming;

public class DevastatingWounds extends Ability {

    public static String baseName = "devastating wounds";

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {
        if(diceResult.result == 6)
        {
            attackResults.wounds -=1;
            attackResults.devastatingWounds.add( RollingLogic.RollDiceAmount(attackingSource.weapon.damageAmount));
        }
    }

    public DevastatingWounds() {
        super(baseName,AbilityTiming.TriggerOnWoundRoll);
    }
}

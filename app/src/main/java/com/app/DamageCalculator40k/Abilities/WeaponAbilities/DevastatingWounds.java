package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.DamageCalculation.RollingLogic;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

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

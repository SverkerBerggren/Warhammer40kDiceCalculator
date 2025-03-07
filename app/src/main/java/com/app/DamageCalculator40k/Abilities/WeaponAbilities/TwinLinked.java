package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

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

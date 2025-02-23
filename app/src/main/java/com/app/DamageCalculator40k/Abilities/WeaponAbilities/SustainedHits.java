package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

public class SustainedHits extends Ability {

    public static String baseName = "sustained hits";
    private int extraHits = 0;

    public SustainedHits(int extraHits) {
        super(baseName + " " + extraHits, AbilityTiming.TriggerOnHitRoll);
        this.extraHits = extraHits;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result == 6)
        {
            attackResults.hits += extraHits;
        }
    }
}

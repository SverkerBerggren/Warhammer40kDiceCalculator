package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DamageCalculation.AbilitySources;
import com.app.DamageCalculator40k.DamageCalculation.AttackResults;
import com.app.DamageCalculator40k.DamageCalculation.DiceResult;
import com.app.DamageCalculator40k.Enums.AbilityTiming;
import com.app.DamageCalculator40k.Enums.Keyword;

public class AntiKeyword extends Ability {
    public static String baseName = "anti";
    private Keyword keyword;
    private int diceNumber = 0;
    public AntiKeyword(Keyword keyword, int diceNumber) {
        super(baseName + "-" + " " + keyword.name(),AbilityTiming.TriggerOnWoundRoll);
        this.keyword = keyword;
        this.diceNumber = diceNumber;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {

    }
}

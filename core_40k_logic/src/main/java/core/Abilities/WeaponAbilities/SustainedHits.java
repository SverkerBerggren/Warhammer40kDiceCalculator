package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

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

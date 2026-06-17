package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

public class LethalHits extends Ability {

    public static String baseName = "lethal hits";

    public LethalHits() {
        super(baseName, AbilityTiming.TriggerOnHitRoll);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result == 6)
        {
            attackResults.hits -=1;
            attackResults.wounds +=1;
        }
    }
}

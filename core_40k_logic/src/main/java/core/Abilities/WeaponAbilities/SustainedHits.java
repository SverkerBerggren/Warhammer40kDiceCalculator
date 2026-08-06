package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.RollingLogic;
import core.DatasheetModeling.DiceAmount;
import core.Enums.AbilityTiming;

public class SustainedHits extends Ability {

    public static String baseName = "Sustained hits";
    private  DiceAmount extraHits;

    public SustainedHits(DiceAmount extraHits) {
        super(baseName + " " + extraHits, AbilityTiming.TriggerOnHitRoll);
        this.extraHits = extraHits;
    }
    public SustainedHits()
    {
        super(baseName , AbilityTiming.TriggerOnHitRoll);
    };
    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(diceResult.result == 6)
        {
            attackResults.hits += RollingLogic.RollDiceAmount(extraHits);
        }
    }
}

package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Abilities.DualModeAbility;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.DamageCalculation.RollingLogic;
import core.DatasheetModeling.DiceAmount;
import core.Enums.AbilityTiming;

public class RapidFire extends DualModeAbility {
    public static String baseName = "rapid fire";
    private final DiceAmount extraShots;

    public RapidFire(DiceAmount amount) {
        super(baseName + " " + amount, AbilityTiming.IncreaseAttacks);
        this.extraShots = amount;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions)
    {
        if(isBoosted)
        {
            attackResults.attacks += RollingLogic.RollDiceAmount(extraShots);
        }
    }
}

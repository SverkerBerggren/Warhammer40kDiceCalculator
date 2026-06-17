package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.DatasheetModeling.DiceAmount;
import core.Enums.AbilityTiming;

public class Melta extends Ability {
    public static String baseName = "melta";
    private DiceAmount diceAmount = new DiceAmount();
    public Melta(DiceAmount diceAmount) {
        super(baseName, AbilityTiming.IncreaseWounds);
        this.diceAmount = diceAmount;
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {

    }
}

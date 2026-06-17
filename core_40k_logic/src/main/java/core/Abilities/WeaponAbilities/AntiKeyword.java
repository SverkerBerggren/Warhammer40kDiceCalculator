package core.Abilities.WeaponAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;
import core.Enums.Keyword;

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

package core.Abilities.GenericAbilities;

import core.Abilities.Ability;
import core.Conditions;
import core.DamageCalculation.AbilitySources;
import core.DamageCalculation.AttackResults;
import core.DamageCalculation.DiceResult;
import core.Enums.AbilityTiming;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReRollAmountOfAttacks extends Ability {

    public static String baseName = "ReRollAmountOfHits";
    public ReRollAmountOfAttacks()
    {
        super(baseName, AbilityTiming.ReRollAttacks);
    }

    @Override
    public void ApplyAbility(DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll, Conditions conditions) {

    }

    public   void rollNumberOfShots(List<DiceResult>  diceResultList)
    {
        int currentAmountOfAttacks = 0;

        for(int i = 0; i < diceResultList.size(); i++)
        {
            currentAmountOfAttacks += diceResultList.get(i).result;
        }
        if(diceResultList.get(0).isD3Roll)
        {
            int expectedAmount = 2 * diceResultList.size();

            if( currentAmountOfAttacks < expectedAmount)
            {
                for(int k = 0; k < diceResultList.size(); k++)
                {
                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 3 +1 ));

                    diceResultList.get(k).result = diceResult.result;
                }
            }
        }

        if(diceResultList.get(0).isD6Roll)
        {
            double expectedAmount = 3.5 * diceResultList.size();

            if( currentAmountOfAttacks < expectedAmount)
            {
                for(int k = 0; k < diceResultList.size(); k++)
                {
                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));

                    diceResultList.get(k).result = diceResult.result;
                }
            }
        }

    }
}

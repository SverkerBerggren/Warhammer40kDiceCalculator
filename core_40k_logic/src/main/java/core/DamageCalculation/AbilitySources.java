package core.DamageCalculation;

import core.Abilities.Ability;
import core.Conditions;
import core.DatasheetModeling.Army;
import core.DatasheetModeling.Model;
import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Weapon;
import core.Enums.AbilityTiming;

public class AbilitySources
{
    public AbilitySources(Army army)
    {
        this.army = army;
    }
    public final Army army;
    public Unit unit;
    public Model model;
    public Weapon weapon;

    public void ApplyAbility(AbilityTiming abilityTiming, DiceResult diceResult, AttackResults attackResults, AbilitySources attackingSource, AbilitySources defendingSources, int requiredRoll,Conditions conditions)
    {
        for(Ability ability : army.GetAbilities())
        {
            if(ability.GetAbilityTiming() == abilityTiming)
            {
                ability.ApplyAbility(diceResult, attackResults, attackingSource, defendingSources,requiredRoll , conditions);
            }
        }
        for(Ability ability : unit.GetAbilities())
        {
            if(ability.GetAbilityTiming() == abilityTiming)
            {
                ability.ApplyAbility(diceResult, attackResults, attackingSource, defendingSources, requiredRoll, conditions);
            }
        }
        for(Ability ability : model.GetAbilities())
        {
            if(ability.GetAbilityTiming() == abilityTiming)
            {
                ability.ApplyAbility(diceResult, attackResults, attackingSource, defendingSources, requiredRoll, conditions);
            }
        }
        // Defending units weapons is not set walla
        if(weapon != null)
        {
            for(Ability ability : model.GetAbilities())
            {
                if(ability.GetAbilityTiming() == abilityTiming)
                {
                    ability.ApplyAbility(diceResult, attackResults, attackingSource, defendingSources, requiredRoll, conditions);
                }
            }
        }
    }
}

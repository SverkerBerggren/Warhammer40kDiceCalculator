package com.app.DamageCalculator40k.DamageCalculation;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.Enums.AbilityTiming;

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

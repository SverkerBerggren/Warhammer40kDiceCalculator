package com.app.DamageCalculator40k.DamageCalculation;

import com.app.DamageCalculator40k.Enums.StatModifier;

public  class StatModifiers
{
    public int GetModifier(StatModifier statModifier)
    {
        return statModifiers[statModifier.ordinal()];
    }

    public void IncrementModifier(StatModifier statModifier)
    {
        statModifiers[statModifier.ordinal()]++;
    }
    public void DecrementModifier(StatModifier statModifier)
    {
        statModifiers[statModifier.ordinal()]--;
    }

    // According to the rules a hit roll or a wound roll can never be modified by more than 1
    public int GetModifierRespectingCap(StatModifier statModifier)
    {
        int retValue = 0;
        retValue = statModifiers[statModifier.ordinal()];
        if(statModifier == StatModifier.HitRoll || statModifier == StatModifier.WoundRoll )
        {
            if(retValue > 1)
            {
                retValue = 1;
            }
            if(retValue < -1)
            {
                retValue = -1;
            }
        }
        return retValue;
    }
    public StatModifiers Copy()
    {
        StatModifiers retModifiers = new StatModifiers();
        System.arraycopy(statModifiers, 0, retModifiers.statModifiers, 0, retModifiers.statModifiers.length);
        return retModifiers;
    }

    public void AddToModifier(StatModifier statModifier, int value)
    {
        statModifiers[statModifier.ordinal()] += value;
    }
    public void SetStatModifier(StatModifier statModifier, int value)
    {
        statModifiers[statModifier.ordinal()] = value;
    }
    private final int[] statModifiers = new int[StatModifier.values().length];
}

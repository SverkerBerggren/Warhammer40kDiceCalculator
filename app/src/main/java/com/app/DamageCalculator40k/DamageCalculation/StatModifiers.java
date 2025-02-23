package com.app.DamageCalculator40k.DamageCalculation;

import com.app.DamageCalculator40k.Enums.StatModifier;

public  class StatModifiers
{
    public short GetModifier(StatModifier statModifier)
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
    public short GetModifierRespectingCap(StatModifier statModifier)
    {
        short retValue = 0;
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

    public void AddToModifier(StatModifier statModifier, short value)
    {
        statModifiers[statModifier.ordinal()] += value;
    }
    public void SetStatModifier(StatModifier statModifier, short value)
    {
        statModifiers[statModifier.ordinal()] = value;
    }
    private final short[] statModifiers = new short[StatModifier.values().length];
}

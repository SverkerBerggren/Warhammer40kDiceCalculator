package com.app.DamageCalculator40k.BitFunctionality;

import com.app.DamageCalculator40k.DatasheetModeling.DeactivatableInterface;
import com.app.DamageCalculator40k.Enums.AbilityEnum;

// Could be made generic but I am not sure if there actually is a case where it is relevant
public class AbilityBitField extends BigBitField<AbilityEnum> {

    // Only supports 64 abilities
    private long activeAbilities = Long.MAX_VALUE;

    public AbilityBitField(AbilityEnum bitEnum) {
        super(bitEnum);
    }

    // A bit weird that it is almost a copy of big bit field set, but making it generic may sacrifice performance
    public void SetActive(AbilityEnum enumValue, boolean value)
    {
        long index = (long) 1 << enumValue.ordinal();

        if(value)
        {
            if(enumValue.ordinal() <= 64)
            {
                activeAbilities =  (activeAbilities | index);
            }
        }
        else
        {
            if(enumValue.ordinal() <= 64)
            {
                activeAbilities =  (activeAbilities & (~index));
            }
        }
    }

    @Override
    public AbilityBitField Copy()
    {
        AbilityBitField newBitField = new AbilityBitField(AbilityEnum.MinusOneDamage);
        newBitField.FirstBitField = FirstBitField;
        newBitField.count = count;
        newBitField.activeAbilities =  activeAbilities;
        return newBitField;
    }

    public boolean IsActive(AbilityEnum abilityEnum)
    {
        long index = (long) 1 << abilityEnum.ordinal() ;
        if(abilityEnum.ordinal() <= 64)
        {
            return  (activeAbilities & index) != 0;
        }
        else
        {
            return (activeAbilities & index) != 0;
        }
    }
}

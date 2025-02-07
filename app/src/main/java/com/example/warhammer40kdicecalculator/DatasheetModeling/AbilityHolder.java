package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;

public interface AbilityHolder {

    AbilityBitField GetAbilityBitField();
    boolean IsActive(AbilityEnum abilityEnum);
}

package com.example.warhammer40kdicecalculator;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;

public interface AbilityUIHolder {

    void AbilityAdded(AbilityEnum abilityEnum, AbilityHolder abilityHolder);

}

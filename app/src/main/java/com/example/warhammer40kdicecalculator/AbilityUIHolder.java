package com.example.warhammer40kdicecalculator;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.DatasheetModeling.AbilityHolder;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;

public interface AbilityUIHolder {

    void AbilityAdded(Ability ability, AbilityHolder abilityHolder);

}

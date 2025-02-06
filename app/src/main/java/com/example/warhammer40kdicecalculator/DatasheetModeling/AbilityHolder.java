package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;

public interface AbilityHolder {

    Ability GetAbility(int index);
    boolean RemoveAbility(Ability ability);
}

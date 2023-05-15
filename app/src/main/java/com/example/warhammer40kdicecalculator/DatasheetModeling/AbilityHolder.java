package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;

public interface AbilityHolder {

    public abstract Ability GetAbility(int index);
    public abstract boolean RemoveAbility(Ability ability);


}

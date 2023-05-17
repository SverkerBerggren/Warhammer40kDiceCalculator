package com.egenapp.warhammer40kdicecalculator.DatasheetModeling;

import com.egenapp.warhammer40kdicecalculator.Abilities.Ability;

public interface AbilityHolder {

    public abstract Ability GetAbility(int index);
    public abstract boolean RemoveAbility(Ability ability);


}

package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.DamageCalculation.StatModifiers;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.Enums.StatModifier;

import java.util.ArrayList;


// Halv kasst namn walla
public abstract class GamePiece {

    private final ArrayList<Ability> abilities = new ArrayList<>();

    public ArrayList<Ability> GetAbilities()
    {
        return abilities;
    }


    private StatModifiers statModifiers = new StatModifiers();

    public StatModifiers getStatModifiers() {
        return statModifiers;
    }
    public StatModifiers setStatModifiers(StatModifiers statModifiers) {
        return this.statModifiers = statModifiers;
    }

    public abstract IdentifierType GetIdentifierType();
}

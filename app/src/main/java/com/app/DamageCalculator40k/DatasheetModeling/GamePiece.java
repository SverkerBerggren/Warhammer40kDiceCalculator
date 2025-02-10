package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;

import java.util.ArrayList;


// Halv kasst namn walla
public abstract class GamePiece {

    private final ArrayList<String> unimplementedAbilities = new ArrayList<>();

    public ArrayList<String> GetUnimplementedAbilities()
    {
        return unimplementedAbilities;
    }

    public abstract AbilityBitField GetAbilityBitField();
    public abstract boolean IsActive(AbilityEnum abilityEnum);
    public abstract IdentifierType GetIdentifierType();
}

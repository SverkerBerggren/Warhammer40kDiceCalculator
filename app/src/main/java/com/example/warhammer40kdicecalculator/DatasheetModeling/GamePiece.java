package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;

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

package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;


// Halv kasst namn walla
public abstract class GamePiece {

    public abstract AbilityBitField GetAbilityBitField();
    public abstract boolean IsActive(AbilityEnum abilityEnum);
    public abstract IdentifierType GetIdentifierType();
}

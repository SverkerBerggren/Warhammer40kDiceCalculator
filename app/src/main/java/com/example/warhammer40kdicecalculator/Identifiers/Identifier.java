package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.Enums.IdentifierType;
import com.example.warhammer40kdicecalculator.Matchup;

public abstract class Identifier   {

    public abstract String toString();
    public abstract IdentifierType GetIdentifierEnum();
    public abstract boolean equals(Object o);
    public abstract String GetMatchupName();
}

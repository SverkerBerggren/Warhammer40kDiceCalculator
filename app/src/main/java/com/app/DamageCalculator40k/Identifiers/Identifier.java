package com.app.DamageCalculator40k.Identifiers;

import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.Matchup;

public abstract class Identifier   {
    public Allegiance allegiance;

    public abstract String toString();
    public abstract IdentifierType GetIdentifierEnum();
    public abstract boolean equals(Object o);
    public abstract String GetMatchupName();
}

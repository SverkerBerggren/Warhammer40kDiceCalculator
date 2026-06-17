package com.app.DamageCalculator40k.Identifiers;

public abstract class Identifier   {
    public Allegiance allegiance;

    public abstract String toString();
    public abstract IdentifierType GetIdentifierEnum();
    public abstract boolean equals(Object o);
    public abstract String GetMatchupName();
}

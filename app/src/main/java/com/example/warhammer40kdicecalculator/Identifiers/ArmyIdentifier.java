package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.R;

import java.util.Objects;

public class ArmyIdentifier extends Identifier{
    public static int ARMY_IDENTIFIER = R.string.ARMY_IDENTIFIER;

    public String allegiance;
    public String matchupName;

    public ArmyIdentifier(String allegiance, String matchupName)
    {
        this.allegiance = allegiance;
        this.matchupName = matchupName;
    }

    @Override
    public String toString() {
        return "ArmyIdentifier{" +
                "allegiance='" + allegiance + '\'' +
                ", matchupName='" + matchupName + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmyIdentifier that = (ArmyIdentifier) o;
        return Objects.equals(allegiance, that.allegiance) && Objects.equals(matchupName, that.matchupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allegiance, matchupName);
    }
}

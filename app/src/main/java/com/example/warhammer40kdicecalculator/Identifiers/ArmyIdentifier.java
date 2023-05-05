package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.R;

public class ArmyIdentifier extends Identifier{
    public static int ARMY_IDENTIFIER = R.string.ARMY_IDENTIFIER;

    public String allegiance;
    public String matchupName;

    public ArmyIdentifier(String allegiance, String matchupName)
    {
        this.allegiance = allegiance;
        this.matchupName = matchupName;
    }
}

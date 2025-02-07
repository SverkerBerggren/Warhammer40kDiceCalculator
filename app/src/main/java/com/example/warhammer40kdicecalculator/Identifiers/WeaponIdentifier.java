package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.Enums.IdentifierType;

public class WeaponIdentifier extends Identifier{

    @Override
    public String toString() {
        return null;
    }

    @Override
    public IdentifierType GetIdentifierEnum() {
        return  IdentifierType.WEAPON;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public String GetMatchupName() {
        return "";
    }
}

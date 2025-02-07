package com.example.warhammer40kdicecalculator.Enums;

import com.example.warhammer40kdicecalculator.R;

public enum IdentifierType {
    UNIT(R.string.UNIT_IDENTIFIER),
    MODEL(R.string.MODEL_IDENTIFIER),
    WEAPON(R.string.WEAPON_IDENTIFIER),
    ARMY(R.string.ARMY_IDENTIFIER),
    IDENTIFIER(R.string.IDENTIFIER);

    private final int resourceId;
    IdentifierType(int applicationResourceId)
    {
        this.resourceId = applicationResourceId;
    }

    public int GetResourceId()
    {
        return resourceId;
    }
}

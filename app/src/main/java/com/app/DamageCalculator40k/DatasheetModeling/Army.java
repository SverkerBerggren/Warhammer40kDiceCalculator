package com.app.DamageCalculator40k.DatasheetModeling;

import androidx.annotation.NonNull;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.ModifierHolder;

import java.util.ArrayList;

public class Army extends GamePiece  {
    public String name = "";

    public ArrayList<Unit> units = new ArrayList<>();

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.ARMY;
    }
}

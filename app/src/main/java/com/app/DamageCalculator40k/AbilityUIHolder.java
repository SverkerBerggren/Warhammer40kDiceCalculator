package com.app.DamageCalculator40k;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;

import java.util.ArrayList;

import kotlin.NotImplementedError;

public interface AbilityUIHolder {

    void AbilityAdded(Ability ability, GamePiece gamePiece);
    default void UpdateAddedAndRemovedAbilities(ArrayList<String> abilitiesToAdd, ArrayList<String> abilitiesToRemove, UIIdentifier uiId)
    {
        throw new NotImplementedError();
    };
}

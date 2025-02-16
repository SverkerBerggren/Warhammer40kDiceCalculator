package com.app.DamageCalculator40k;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.Enums.AbilityEnum;

public interface AbilityUIHolder {

    void AbilityAdded(Ability ability, GamePiece gamePiece);

}

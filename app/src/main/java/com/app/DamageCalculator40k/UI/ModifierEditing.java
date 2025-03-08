package com.app.DamageCalculator40k.UI;

import com.app.DamageCalculator40k.Enums.StatModifier;
import com.app.DamageCalculator40k.Identifiers.UIIdentifier;

public interface ModifierEditing {
    void UpdateModifierValue(StatModifier modifier, int value, UIIdentifier uiId);
}

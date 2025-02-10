package com.app.DamageCalculator40k;

import com.app.DamageCalculator40k.Activities.CompareActivity;

public interface ModifierHolder {
    public abstract int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount);
    public abstract  int GetModifierValue(CompareActivity.UnitAndModelSkill mod);
    public abstract  void SetModifierValue(CompareActivity.UnitAndModelSkill mod, int toChangeTo);
}

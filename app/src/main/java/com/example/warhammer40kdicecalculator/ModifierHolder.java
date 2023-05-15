package com.example.warhammer40kdicecalculator;

import android.widget.EditText;

public interface ModifierHolder {
    public abstract int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount);
    public abstract  int GetModifierValue(CompareActivity.UnitAndModelSkill mod);
    public abstract  void SetModifierValue(CompareActivity.UnitAndModelSkill mod, int toChangeTo);
}

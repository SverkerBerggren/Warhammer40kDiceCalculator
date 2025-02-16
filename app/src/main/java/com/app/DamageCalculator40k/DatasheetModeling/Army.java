package com.app.DamageCalculator40k.DatasheetModeling;

import androidx.annotation.NonNull;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.ModifierHolder;

import java.util.ArrayList;

public class Army extends GamePiece implements ModifierHolder {
    public String name = "";

    public ArrayList<Unit> units = new ArrayList<>();

    public int toughnessModifier = 0;
    public int strengthModifier = 0;
    public int armorSaveModifier = 0;
    public int invulnerableSaveModifier = 0;
    public int woundsModifier = 0;
    public int hitSkill = 0;
    public int attacksModifier = 0;


    public  Army()
    {

    }

    @NonNull
    @Override
    public String toString() {
        return "Army{" +
                "name='" + name + '\'' +
                ", units=" + units +
                ", toughnessModifier=" + toughnessModifier +
                ", strengthModifier=" + strengthModifier +
                ", armorSaveModifier=" + armorSaveModifier +
                ", invulnerableSaveModifier=" + invulnerableSaveModifier +
                ", woundsModifier=" + woundsModifier +
                ", ballisticSkillModifier=" + hitSkill +
                ", attacksModifier=" + attacksModifier +
                '}';
    }

    @Override
    public int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount) {
        int valueToReturn = 0;
        switch (whatToChange)
        {
            case HitSkill:
                hitSkill += amount;
                valueToReturn = hitSkill;
                break;
            case Strength:
                strengthModifier += amount;
                valueToReturn = strengthModifier;
                break;
            case Toughness:
                toughnessModifier += amount;
                valueToReturn = toughnessModifier;
                break;
            case Wounds:
                woundsModifier += amount;
                valueToReturn = woundsModifier;
                break;
            case Attacks:
                attacksModifier += amount;
                valueToReturn = attacksModifier;
                break;
            case ArmorSaves:
                armorSaveModifier += amount;
                valueToReturn = armorSaveModifier;
                break;
            case InvulnerableSaves:
                invulnerableSaveModifier += amount;
                valueToReturn = invulnerableSaveModifier;
                break;
        }
        return valueToReturn;
    }

    @Override
    public int GetModifierValue(CompareActivity.UnitAndModelSkill mod) {
        int valueToReturn = 0;
        switch (mod)
        {

            case HitSkill:
                valueToReturn = hitSkill;
                break;
            case Strength:
                valueToReturn = strengthModifier;
                break;
            case Toughness:
                valueToReturn = toughnessModifier;
                break;
            case Wounds:
                valueToReturn = woundsModifier;
                break;
            case Attacks:
                valueToReturn = attacksModifier;
                break;
            case ArmorSaves:
                valueToReturn = armorSaveModifier;
                break;
            case InvulnerableSaves:
                valueToReturn = invulnerableSaveModifier;
                break;
        }
        return valueToReturn;
    }

    @Override
    public void SetModifierValue(CompareActivity.UnitAndModelSkill mod, int amount) {
        switch (mod)
        {
            case HitSkill:
                hitSkill = amount;
                break;
            case Strength:
                strengthModifier = amount;
                break;
            case Toughness:
                toughnessModifier = amount;
                break;
            case Wounds:
                woundsModifier = amount;
                break;
            case Attacks:
                attacksModifier = amount;
                break;
            case ArmorSaves:
                armorSaveModifier = amount;
                break;
            case InvulnerableSaves:
                invulnerableSaveModifier = amount;
                break;
        }
    }


    @Override
    public boolean IsActive(AbilityEnum abilityEnum) {
        return false;
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.ARMY;
    }
}

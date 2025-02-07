package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Activities.CompareActivity;
import com.example.warhammer40kdicecalculator.BitFunctionality.AbilityBitField;
import com.example.warhammer40kdicecalculator.Enums.AbilityEnum;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;
import com.example.warhammer40kdicecalculator.ModifierHolder;

import java.util.ArrayList;

public class Unit extends GamePiece implements ModifierHolder, WahapediaIdHolder {

    public boolean singleModelUnit = false;
    public String wahapediaDataId;
    public String unitName = "";
    public int pointCost;
    public int toughnessModifier;
    public int strengthModifier;
    public int armorSaveModifier;
    public int invulnerableSaveModifier;
    public int woundsModifier;
    public int hitSkill;
    public int attacksModifier;

    public ArrayList<Model> listOfModels = new ArrayList<>();

    private AbilityBitField listOfAbilitys = new AbilityBitField(AbilityEnum.MinusOneDamage);

    @Override
    public String GetWahapediaId() {
        return wahapediaDataId;
    }

    public Unit Copy()
    {
        Unit tempUnit = new Unit();

        tempUnit.unitName = unitName;

        for(int i = 0; i < listOfModels.size(); i++)
        {
            tempUnit.listOfModels.add(listOfModels.get(i).Copy());
        }

        tempUnit.listOfAbilitys = listOfAbilitys.Copy();
        tempUnit.wahapediaDataId = wahapediaDataId;
        tempUnit.toughnessModifier = toughnessModifier;
        tempUnit.strengthModifier = strengthModifier;
        tempUnit.armorSaveModifier = armorSaveModifier;
        tempUnit.invulnerableSaveModifier = invulnerableSaveModifier;
        tempUnit.woundsModifier = woundsModifier;
        tempUnit.hitSkill = hitSkill;
        tempUnit.attacksModifier = attacksModifier;

        return  tempUnit;
    }
    public Unit()
    {

    }

    public  Unit(String unitName, int pointCost, int toughnessModifier, int strengthModifier, int armorSaveModifier, int invulnerableSaveModifier, int woundsModifier, int hitSkill,
                 int attacksModifier, ArrayList<Model> listOfModels, AbilityBitField listOfAbilitys)
    {
        this.unitName = unitName;
        this.pointCost = pointCost;
        this.toughnessModifier = toughnessModifier;
        this.strengthModifier = strengthModifier;
        this.armorSaveModifier = armorSaveModifier;
        this.invulnerableSaveModifier = invulnerableSaveModifier;
        this.woundsModifier = woundsModifier;
        this.hitSkill = hitSkill;
        this.attacksModifier = attacksModifier;

        this.listOfModels = listOfModels;

        this.listOfAbilitys = listOfAbilitys;
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
    public AbilityBitField GetAbilityBitField() {
        return listOfAbilitys;
    }

    @Override
    public boolean IsActive(AbilityEnum abilityEnum) {
        return listOfAbilitys.IsActive(abilityEnum);
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.UNIT;
    }
}

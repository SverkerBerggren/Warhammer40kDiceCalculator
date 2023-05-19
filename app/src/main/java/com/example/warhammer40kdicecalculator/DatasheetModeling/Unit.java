package com.example.warhammer40kdicecalculator.DatasheetModeling;

import android.widget.EditText;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.CompareActivity;
import com.example.warhammer40kdicecalculator.ModifierHolder;

import java.util.ArrayList;

public class Unit implements AbilityHolder, ModifierHolder {

    public String unitName = "";

    public int pointCost;


    public int toughnessModifier;
    public int strengthModifier;
    public int armorSaveModifier;
    public int invulnerableSaveModifier;
    public int woundsModifier;
    public int ballisticSkillModifier;
    public int weaponSkillModifier;
    public int attacksModifier;

    public ArrayList<Model> listOfModels = new ArrayList<>();

    public ArrayList<Ability> listOfAbilitys = new ArrayList<>( );

    public Unit Copy()
    {
        Unit tempUnit = new Unit();

        tempUnit.unitName = unitName;

        for(int i = 0; i < listOfModels.size(); i++)
        {
            tempUnit.listOfModels.add(listOfModels.get(i).Copy());
        }

        tempUnit.listOfAbilitys.addAll( listOfAbilitys);


        tempUnit.toughnessModifier = toughnessModifier;
        tempUnit.strengthModifier = strengthModifier;
        tempUnit.armorSaveModifier = armorSaveModifier;
        tempUnit.invulnerableSaveModifier = invulnerableSaveModifier;
        tempUnit.woundsModifier = woundsModifier;
        tempUnit.ballisticSkillModifier = ballisticSkillModifier;
        tempUnit.weaponSkillModifier = weaponSkillModifier;
        tempUnit.attacksModifier = attacksModifier;



        return  tempUnit;
    }
    public Unit()
    {

    }

    public  Unit(Unit other)
    {
        listOfModels = new ArrayList<>(other.listOfModels);

        listOfAbilitys = new ArrayList<>(other.listOfAbilitys);
    }
    public  Unit(String  unitName,int pointCost,int toughnessModifier, int strengthModifier, int armorSaveModifier,int invulnerableSaveModifier,int woundsModifier, int ballisticSkillModifier, int weaponSkillModifier,
                 int attacksModifier, ArrayList<Model> listOfModels,ArrayList<Ability> listOfAbilitys  )
    {
        this.unitName = unitName;
        this.pointCost = pointCost;
        this.toughnessModifier = toughnessModifier;
        this.strengthModifier = strengthModifier;
        this.armorSaveModifier = armorSaveModifier;
        this.invulnerableSaveModifier = invulnerableSaveModifier;
        this.woundsModifier = woundsModifier;
        this.ballisticSkillModifier = ballisticSkillModifier;
        this.weaponSkillModifier = weaponSkillModifier;
        this.attacksModifier = weaponSkillModifier;

        this.listOfModels = listOfModels;

        this.listOfAbilitys = listOfAbilitys;
    }

    @Override
    public int ChangeModifiers(CompareActivity.UnitAndModelSkill whatToChange, int amount) {
        int valueToReturn = 0;
        switch (whatToChange)
        {
            case WeaponSkill:
                weaponSkillModifier += amount;
                valueToReturn = weaponSkillModifier;
                break;
            case BallisticSkill:
                ballisticSkillModifier += amount;
                valueToReturn = ballisticSkillModifier;
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
            case WeaponSkill:
                valueToReturn = weaponSkillModifier;
                break;
            case BallisticSkill:
                valueToReturn = ballisticSkillModifier;
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
            case WeaponSkill:
                weaponSkillModifier = amount;
                break;
            case BallisticSkill:
                ballisticSkillModifier = amount;
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
    public Ability GetAbility(int index) {
        return listOfAbilitys.get(index);
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return listOfAbilitys.remove(ability);
    }
}

package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.DamageCalculation.StatModifiers;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.ModifierHolder;

import java.util.ArrayList;

public class Model extends GamePiece  implements DeactivatableInterface, WahapediaIdHolder{

    public String wahapediaDataId;
    public String name;
    public int toughness = -1;;
    public int strength = -1;
    public int armorSave = -1;
    //Todo: Fix invulnerable save condition
    public int invulnerableSave = 7;
    public int wounds = -1;
    public int attacks=  -1;

    public boolean active = true;

    private AbilityBitField abilityFlags = new AbilityBitField(AbilityEnum.Blast);

    public ArrayList<Weapon> weapons = new ArrayList<>();

    @Override
    public String GetWahapediaId() {
        return wahapediaDataId;
    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.MODEL;
    }

    public Model Copy()
    {
        Model modelToReturn = new Model();
        modelToReturn.toughness = toughness;
        modelToReturn.strength = strength;
        modelToReturn.armorSave = armorSave;
        modelToReturn.invulnerableSave = invulnerableSave;
        modelToReturn.wounds = wounds;
        modelToReturn.attacks=  attacks;
        modelToReturn.wahapediaDataId = wahapediaDataId;
        modelToReturn.active = active;
        modelToReturn.name = name;
        modelToReturn.abilityFlags = abilityFlags.Copy();
        modelToReturn.setStatModifiers( getStatModifiers().Copy());
        ArrayList<Weapon> newList = new ArrayList<>();

        for(Weapon weapon : weapons)
        {
            newList.add(weapon.Copy());
        }

        modelToReturn.weapons = newList;
        return modelToReturn;

    }

    public Model()
    {

    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}

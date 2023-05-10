package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;

import java.util.ArrayList;

public class Army implements AbilityHolder{
    public String name;

    public ArrayList<Unit> units;

    public ArrayList<Ability> abilities;

    public int toughnessModifier;
    public int strengthModifier;
    public int armorSaveModifier;
    public int invulnerableSaveModifier;
    public int woundsModifier;
    public int ballisticSkillModifier;
    public int weaponSkillModifier;
    public int attacksModifier;

    public  Army( String  name,int toughnessModifier, int strengthModifier, int armorSaveModifier,int invulnerableSaveModifier,int woundsModifier, int ballisticSkillModifier, int weaponSkillModifier,
                  int attacksModifier, ArrayList<Unit> units,ArrayList<Ability> abilities )
    {
        this.name = name;

        this.toughnessModifier = toughnessModifier;
        this.strengthModifier = strengthModifier;
        this.armorSaveModifier = armorSaveModifier;
        this.invulnerableSaveModifier = invulnerableSaveModifier;
        this.woundsModifier = woundsModifier;
        this.ballisticSkillModifier = ballisticSkillModifier;
        this.weaponSkillModifier = weaponSkillModifier;
        this.attacksModifier = attacksModifier;

        this.units = units;

        this.abilities = abilities;
    }

    @Override
    public Ability GetAbility(int index) {
        return abilities.get(index);
    }

    @Override
    public boolean RemoveAbility(Ability ability) {
        return abilities.remove(ability);
    }
}

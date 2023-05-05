package com.example.warhammer40kdicecalculator.DatasheetModeling;

import com.example.warhammer40kdicecalculator.Abilities.Ability;

import java.util.ArrayList;

public class Unit implements AbilityHolder{

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

    public Unit copy()
    {
        Unit tempUnit = new Unit();

        for(int i = 0; i < listOfModels.size(); i++)
        {
            tempUnit.listOfModels.add(new Model(listOfModels.get(i)));
        }
        for(int i = 0; i < listOfAbilitys.size(); i++)
        {
            tempUnit.listOfAbilitys.add( listOfAbilitys.get(i));
        }

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
}

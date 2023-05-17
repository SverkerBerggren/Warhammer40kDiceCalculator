package com.egenapp.warhammer40kdicecalculator;

import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.egenapp.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.egenapp.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.egenapp.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

public class Matchup {
    public String name;

    public Army friendlyArmy;

    public Army enemyArmy;

    public Matchup(String name, Army friendlyArmy,Army enemyArmy)
    {
        this.name = name;

        this.friendlyArmy = friendlyArmy;

        this.enemyArmy = enemyArmy;
    }


    public Model GetModel(ModelIdentifier modelId)
    {
        Model modelToReturn;

        if(modelId.allegiance.equals("friendly"))
        {
            modelToReturn =friendlyArmy.units.get(modelId.indexUnit).listOfModels.get(modelId.indexModel);
        }
        else
        {
            modelToReturn =enemyArmy.units.get(modelId.indexUnit).listOfModels.get(modelId.indexModel);
        }
        return modelToReturn;
    }

    public Unit GetUnit(UnitIdentifier unitId)
    {
        Unit unitToReturn;

        if(unitId.allegiance.equals("friendly"))
        {
            unitToReturn =friendlyArmy.units.get(unitId.index);
        }
        else
        {
            unitToReturn =enemyArmy.units.get(unitId.index);
        }
        return unitToReturn;
    }
    public Army GetArmy(ArmyIdentifier armyId)
    {
        Army armyToReturn;

        if(armyId.allegiance.equals("friendly"))
        {
            return friendlyArmy;
        }
        else
        {
            return enemyArmy;
        }
    }
}

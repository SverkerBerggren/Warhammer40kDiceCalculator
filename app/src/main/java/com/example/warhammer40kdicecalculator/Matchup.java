package com.example.warhammer40kdicecalculator;

import android.util.Log;

import com.example.warhammer40kdicecalculator.DatasheetModeling.GamePiece;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.example.warhammer40kdicecalculator.Identifiers.ArmyIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.Identifier;
import com.example.warhammer40kdicecalculator.Identifiers.ModelIdentifier;
import com.example.warhammer40kdicecalculator.Identifiers.UnitIdentifier;

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

    // Kanske gor den mer generisk sen
    public GamePiece GetGamePiece(Identifier identifier)
    {
        switch (identifier.GetIdentifierEnum())
        {
            case UNIT:
                return GetUnit((UnitIdentifier) identifier);
            case ARMY:
                return GetArmy((ArmyIdentifier) identifier);
            case MODEL:
                return GetModel((ModelIdentifier) identifier);
            default:
                Log.d("Ability holder retrival","Unsupported enum found");
                return null;
         }
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

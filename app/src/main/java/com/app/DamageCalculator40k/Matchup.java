package com.app.DamageCalculator40k;

import android.util.Log;

import com.app.DamageCalculator40k.DatasheetModeling.GamePiece;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.Identifiers.Allegiance;
import com.app.DamageCalculator40k.Identifiers.ArmyIdentifier;
import com.app.DamageCalculator40k.Identifiers.Identifier;
import com.app.DamageCalculator40k.Identifiers.ModelIdentifier;
import com.app.DamageCalculator40k.Identifiers.UnitIdentifier;

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

        if(modelId.allegiance.equals(Allegiance.friendly))
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

        if(unitId.allegiance.equals(Allegiance.friendly))
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
        if(armyId.allegiance.equals(Allegiance.friendly))
        {
            return friendlyArmy;
        }
        else
        {
            return enemyArmy;
        }
    }
}

package com.app.DamageCalculator40k.DatasheetModeling;

import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.BitFunctionality.AbilityBitField;
import com.app.DamageCalculator40k.Enums.AbilityEnum;
import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.ModifierHolder;

import java.util.ArrayList;

public class Unit extends GamePiece implements WahapediaIdHolder {

    public boolean singleModelUnit = false;
    public String wahapediaDataId;
    public String unitName = "";
    public int pointCost;

    public ArrayList<Model> listOfModels = new ArrayList<>();
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

        tempUnit.wahapediaDataId = wahapediaDataId;

        return  tempUnit;
    }
    public Unit()
    {

    }

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.UNIT;
    }
}

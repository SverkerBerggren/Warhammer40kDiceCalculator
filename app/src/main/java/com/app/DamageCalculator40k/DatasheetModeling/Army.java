package com.app.DamageCalculator40k.DatasheetModeling;


import com.app.DamageCalculator40k.Enums.IdentifierType;
import java.util.ArrayList;

public class Army extends GamePiece  {
    public String name = "";

    public ArrayList<Unit> units = new ArrayList<>();

    @Override
    public IdentifierType GetIdentifierType() {
        return IdentifierType.ARMY;
    }
}

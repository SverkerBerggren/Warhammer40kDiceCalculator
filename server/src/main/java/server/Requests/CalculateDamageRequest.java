package server.Requests;

import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Army;
import core.Conditions;

import java.util.ArrayList;

public class CalculateDamageRequest {
    public ArrayList<Unit> attackerList;
    public Unit defendingUnit;
    public Army attackingArmy;
    public Army defendingArmy;
    public Conditions conditions;
}
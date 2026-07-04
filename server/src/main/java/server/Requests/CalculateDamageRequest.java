package server.Requests;

import core.DatasheetModeling.GamePiece;
import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Army;
import core.Conditions;

import java.util.ArrayList;

public class CalculateDamageRequest {
    public ArrayList<Unit> attackerList;
    public Unit defendingUnit;
    public GamePiece attackingArmy;
    public GamePiece defendingArmy;
    public Conditions conditions;
}
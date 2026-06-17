package core.DatasheetModeling;

import core.Enums.Keyword;

import java.util.ArrayList;

public class Unit extends GamePiece   {

    public boolean singleModelUnit = false;
    public String unitName = "";
    public int pointCost;

    public ArrayList<Model> listOfModels = new ArrayList<>();
    public ArrayList<Keyword> keywords = new ArrayList<>();

    public Unit Copy()
    {
        Unit tempUnit = new Unit();

        tempUnit.unitName = unitName;

        for(int i = 0; i < listOfModels.size(); i++)
        {
            tempUnit.listOfModels.add(listOfModels.get(i).Copy());
        }

        tempUnit.abilities.addAll(abilities);
        tempUnit.keywords.addAll(keywords);

        return  tempUnit;
    }
    public Unit()
    {

    }
}

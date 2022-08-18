package com.example.warhammer40kdicecalculator;

import java.util.ArrayList;

public class Unit {

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
}

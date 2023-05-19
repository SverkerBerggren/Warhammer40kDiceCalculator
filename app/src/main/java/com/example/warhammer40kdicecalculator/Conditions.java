package com.example.warhammer40kdicecalculator;

import java.util.ArrayList;
import java.util.Arrays;

public class Conditions {

    public boolean rapidFireRange = false;
    public boolean meleeCombat = false;
    public boolean rangedCombat = true;
    public boolean mediumBlast = false;
    public boolean fullBlast = false;


    @Override
    public String toString() {
        return "Conditions{" +
                "rapidFireRange=" + rapidFireRange +
                ", meleeCombat=" + meleeCombat +
                ", rangedCombat=" + rangedCombat +
                ", mediumBlast=" + mediumBlast +
                ", fullBlast=" + fullBlast +
                '}';
    }

    public Conditions(String stringToParse)
    {
        String[] splittedString = stringToParse.split(",");

        if(splittedString[0].contains("false"))
            rapidFireRange = false;
        else
            rapidFireRange = true;
        if(splittedString[1].contains("false"))
            meleeCombat = false;
        else
            meleeCombat = true;
        if(splittedString[2].contains("false"))
            rangedCombat = false;
        else
            rangedCombat = true;



    }

    public Conditions()
    {

    }



}

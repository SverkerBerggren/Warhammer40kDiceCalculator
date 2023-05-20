package com.example.warhammer40kdicecalculator;

import java.util.ArrayList;
import java.util.Arrays;

public class Conditions {

    public boolean rapidFireRange = false;
    public boolean meleeCombat = false;
    public boolean rangedCombat = true;
    public boolean mediumBlast = false;
    public boolean fullBlast = false;
    public boolean tacticalDoctrine = false;
    public boolean devastatorDoctrine = false;
    public boolean assaultDoctrine = false;
    public boolean plusOneToWound = false;
    public boolean dakkaHalfRange = false;

    @Override
    public String toString() {
        return "Conditions{" +
                "rapidFireRange=" + rapidFireRange +
                ", meleeCombat=" + meleeCombat +
                ", rangedCombat=" + rangedCombat +
                ", mediumBlast=" + mediumBlast +
                ", fullBlast=" + fullBlast +
                ", tacticalDoctrine=" + tacticalDoctrine +
                ", devastatorDoctrine=" + devastatorDoctrine +
                ", assaultDoctrine=" + assaultDoctrine +
                ", plusOneToWound=" + plusOneToWound +
                ", dakkaHalfRange=" + dakkaHalfRange +
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
        if(splittedString[3].contains("false"))
            mediumBlast = false;
        else
            mediumBlast = true;
        if(splittedString[4].contains("false"))
            fullBlast = false;
        else
            fullBlast = true;
        if(splittedString[5].contains("false"))
            tacticalDoctrine = false;
        else
            tacticalDoctrine = true;
        if(splittedString[6].contains("false"))
            devastatorDoctrine = false;
        else
            devastatorDoctrine = true;
        if(splittedString[7].contains("false"))
            assaultDoctrine = false;
        else
            assaultDoctrine = true;
        if(splittedString[8].contains("false"))
            plusOneToWound = false;
        else
            plusOneToWound = true;
        if(splittedString[9].contains("false"))
            dakkaHalfRange = false;
        else
            dakkaHalfRange = true;



    }

    public Conditions()
    {

    }



}

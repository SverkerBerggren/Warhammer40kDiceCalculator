package com.example.warhammer40kdicecalculator;

public class UnitIdentifier {

    public static int UNIT_IDENTIFIER = R.string.UNIT_IDENTIFIER;

    public String allegiance;
    public String tag;
    public int index;
    public String matchupName;


   public UnitIdentifier(String allegiance, String tag, int index, String matchupName)
   {
        this.allegiance = allegiance;
        this.tag = tag;
        this.index = index;
        this.matchupName = matchupName;


   }



}

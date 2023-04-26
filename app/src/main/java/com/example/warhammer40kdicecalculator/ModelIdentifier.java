package com.example.warhammer40kdicecalculator;

public class ModelIdentifier extends Identifier{


    public static int MODEL_IDENTIFIER = R.string.MODEL_IDENTIFIER;

    public String allegiance;
    public String tag;
    public int indexUnit;
    public int indexModel;
    public String matchupName;

    public ModelIdentifier(String allegiance, String tag, int indexUnit, int indexModel, String matchupName)
    {
        this.allegiance = allegiance;
        this.tag = tag;
        this.indexUnit = indexUnit;
        this.indexModel = indexModel;
        this.matchupName = matchupName;


    }
}

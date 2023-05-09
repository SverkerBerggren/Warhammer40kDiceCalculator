package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.R;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "ModelIdentifier{" +
                "allegiance='" + allegiance + '\'' +
                ", tag='" + tag + '\'' +
                ", indexUnit=" + indexUnit +
                ", indexModel=" + indexModel +
                ", matchupName='" + matchupName + '\'' +
                '}';
    }

    public ModelIdentifier(String str) {
        // split the string using regex and extract the values
        String[] parts = str.split("[{=,',}]");
        allegiance = parts[1];
        tag = parts[3];
        indexUnit = Integer.parseInt(parts[5]);
        indexModel = Integer.parseInt(parts[7]);
        matchupName = parts[9];
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelIdentifier that = (ModelIdentifier) o;
        return indexUnit == that.indexUnit && indexModel == that.indexModel && Objects.equals(allegiance, that.allegiance) && Objects.equals(tag, that.tag) && Objects.equals(matchupName, that.matchupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allegiance, tag, indexUnit, indexModel, matchupName);
    }
}

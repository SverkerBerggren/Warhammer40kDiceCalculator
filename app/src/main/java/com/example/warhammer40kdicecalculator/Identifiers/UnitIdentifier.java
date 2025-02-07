package com.example.warhammer40kdicecalculator.Identifiers;

import com.example.warhammer40kdicecalculator.Enums.Faction;
import com.example.warhammer40kdicecalculator.Enums.IdentifierType;
import com.example.warhammer40kdicecalculator.Matchup;
import com.example.warhammer40kdicecalculator.R;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UnitIdentifier extends Identifier{

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

   // Chat gpt ass skit
    public UnitIdentifier(String identifierString) {
        String regex = "UnitIdentifier\\{allegiance='(.+)', tag='(.+)', index=(\\d+), matchupName='(.+)'\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(identifierString);
        if (matcher.matches()) {
            this.allegiance = matcher.group(1);



            this.tag = matcher.group(2);

            if(this.tag.equals( "null"))
            {
                this.tag = null;
            }
            this.index = Integer.parseInt(matcher.group(3));
            this.matchupName = matcher.group(4);
        } else {
            throw new IllegalArgumentException("Invalid identifier string: " + identifierString);
        }
    }



    @Override
    public String toString() {
        return "UnitIdentifier{" +
                "allegiance='" + allegiance + '\'' +
                ", tag='" + tag + '\'' +
                ", index=" + index +
                ", matchupName='" + matchupName + '\'' +
                '}';
    }

    @Override
    public IdentifierType GetIdentifierEnum() {
        return IdentifierType.UNIT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitIdentifier that = (UnitIdentifier) o;
        return index == that.index && Objects.equals(allegiance, that.allegiance) && Objects.equals(tag, that.tag) && Objects.equals(matchupName, that.matchupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allegiance, tag, index, matchupName);
    }
}

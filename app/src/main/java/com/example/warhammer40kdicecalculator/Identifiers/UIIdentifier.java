package com.example.warhammer40kdicecalculator.Identifiers;

import java.util.Objects;

public class UIIdentifier {


    public String elementName;

    public Identifier id;


    public UIIdentifier(String elementName, Identifier id)
    {
        this.elementName = elementName;

        this.id = id;
    }





    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIIdentifier that = (UIIdentifier) o;
        return Objects.equals(elementName, that.elementName) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementName, id);
    }
}

package com.app.DamageCalculator40k.Identifiers;

import com.app.DamageCalculator40k.Activities.CompareActivity;
import com.app.DamageCalculator40k.Activities.EditUnitActivity;
import com.app.DamageCalculator40k.UI.WidgetType;

import java.util.Objects;

public class UIIdentifier {
    public WidgetType widgetType;
    public Identifier id;

    public UIIdentifier(WidgetType widgetType, Identifier id)
    {
        this.widgetType = widgetType;

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIIdentifier that = (UIIdentifier) o;
        return Objects.equals(widgetType, that.widgetType) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(widgetType, id);
    }
}

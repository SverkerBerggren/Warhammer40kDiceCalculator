package com.app.DamageCalculator40k.Identifiers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.R;

public class IdentifierUtils {

    // TODO: Make this more efficient by reducing string constructions
    public static Identifier GetIdentifierFromExtra(Intent intent)
    {
        IdentifierType identifierType = IdentifierType.valueOf(intent.getStringExtra(IdentifierType.IDENTIFIER.name()));
        Identifier identifier;
        String identifierData = intent.getStringExtra( identifierType.getName());
        switch (identifierType)
        {
            case UNIT:
            {
                identifier = new UnitIdentifier(identifierData);
                break;
            }
            case ARMY:
            {
                identifier = new ArmyIdentifier(identifierData);
                break;
            }
            case MODEL:
            {
                identifier = new ModelIdentifier(identifierData);
                break;
            }
            default:
            {
                Log.d("Identifier creation","Unkown enum detected");
                identifier = null;
            }
        }
        return identifier;
    }

    public static void FillIntentWithIdentifier(Intent intent, Identifier identifier )
    {
        intent.putExtra(IdentifierType.IDENTIFIER.name(),identifier.GetIdentifierEnum().getName());
        intent.putExtra(identifier.GetIdentifierEnum().getName(),identifier.toString());
    }
}

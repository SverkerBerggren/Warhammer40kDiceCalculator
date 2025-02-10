package com.app.DamageCalculator40k.Identifiers;

import android.content.Intent;
import android.util.Log;

import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.R;

public class IdentifierUtils {

    // TODO: Make this more efficient by reducing string constructions
    public static Identifier GetIdentifierFromExtra(Intent intent)
    {
        // TODO: upgrade api?
        IdentifierType identifierType = IdentifierType.valueOf(intent.getStringExtra(IdentifierType.IDENTIFIER.name()));
        Identifier identifier;
        switch (identifierType)
        {
            case UNIT:
            {
                identifier = new UnitIdentifier(intent.getStringExtra( ""+ R.string.UNIT_IDENTIFIER));
                break;
            }
            case ARMY:
            {
                identifier = new ArmyIdentifier(intent.getStringExtra("" + R.string.ARMY_IDENTIFIER));
                break;
            }
            case MODEL:
            {
                identifier = new ModelIdentifier(intent.getStringExtra("" + R.string.MODEL_IDENTIFIER));
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
}

package com.example.warhammer40kdicecalculator.Identifiers;

import android.content.Intent;
import android.util.Log;

import com.example.warhammer40kdicecalculator.Enums.IdentifierType;
import com.example.warhammer40kdicecalculator.R;

public class IdentifierUtils {

    // TODO: Make this more efficient by reducing string constructions
    public static Identifier GetIdentifierFromExtra(Intent intent)
    {
        // TODO: upgrade api?
        String namnet = IdentifierType.IDENTIFIER.name();
        String valueLmao = intent.getStringExtra(IdentifierType.IDENTIFIER.name());
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

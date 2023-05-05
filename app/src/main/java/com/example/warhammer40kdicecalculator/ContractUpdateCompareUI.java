package com.example.warhammer40kdicecalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.warhammer40kdicecalculator.Identifiers.Identifier;

public class ContractUpdateCompareUI extends ActivityResultContract<Identifier, Uri> {



    @Override
    public Intent createIntent(@NonNull Context context, @NonNull Identifier ringtoneType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, ringtoneType.intValue());
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent result) {
        if (resultCode != Activity.RESULT_OK || result == null) {
            return null;
        }
        return null;//result.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
    }
}

package com.example.warhammer40kdicecalculator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.net.URI;

public class ParseActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    private ActivityResultLauncher<Intent> activityChooseFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();

                        Uri uri = data.getData();
                    }
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse);


        CheckAndRequestPermission();

   //     int permissionCheck = ContextCompat.checkSelfPermission(ParseActivity.this,
   //             Manifest.permission.READ_EXTERNAL_STORAGE);
//
   //     if (ContextCompat.checkSelfPermission(ParseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
   //             != PackageManager.PERMISSION_GRANTED)
   //     {
   //         // OPCIONAL(explicaciones de poque pedimos los permisos)
   //         if (ActivityCompat.shouldShowRequestPermissionRationale(ParseActivity.this,
   //                 Manifest.permission.READ_EXTERNAL_STORAGE))
   //         {
//
   //         }
   //         else
   //         {
   //             //pedir permisos
   //             ActivityCompat.requestPermissions(ParseActivity.this,
   //                     new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
   //                     permissionCheck);
   //         }
   //     }


    }

    private void CheckAndRequestPermission()
    {
        if (ContextCompat.checkSelfPermission(
                getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

        } else if (shouldShowRequestPermissionRationale())
        {
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected, and what
        // features are disabled if it's declined. In this UI, include a
        // "cancel" or "no thanks" button that lets the user continue
        // using your app without granting the permission.
            showInContextUI();
        }else
        {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private boolean shouldShowRequestPermissionRationale()
    {
        return  false;
    }

    private void showInContextUI()
    {
        Log.d("permission", "fick ej permission");
    }

    private void ReadFiles()
    {

        Intent intentTest = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);


        intentTest = Intent.createChooser(intentTest,"choose a file");


        activityChooseFileLauncher.launch(intentTest);

    }

}
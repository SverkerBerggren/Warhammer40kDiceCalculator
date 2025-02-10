package com.example.warhammer40kdicecalculator.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.warhammer40kdicecalculator.FileHandling.FileHandler;
import com.example.warhammer40kdicecalculator.R;

import java.util.ArrayList;

public class ParseActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> activityChooseFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        FileHandler.GetInstance().CreateArmyFromFile(getBaseContext(), uri);


                        CreateArmyButtons(FileHandler.GetInstance().GetSavedArmies());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse);

        findViewById(R.id.ParseArmyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadFiles();
            }
        });

        findViewById(R.id.CreateMatchupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartCreateMatchup();
            }
        });

        CreateArmyButtons(FileHandler.GetInstance().GetSavedArmies());
    }
    private void ReadFiles()
    {
        Intent intentTest = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentTest.setType("*/*");
        intentTest = Intent.createChooser(intentTest,"choose a file");

        activityChooseFileLauncher.launch(intentTest);
    }

    private void CreateArmyButtons(ArrayList<String> listOfNames)
    {
        LinearLayout linearLayout = findViewById(R.id.SavedArmiesList);
        linearLayout.removeAllViews();

        for(String string : listOfNames)
        {
            Button armyButton = new Button(getBaseContext());
            armyButton.setText(string);

            linearLayout.addView(armyButton);
        }
    }


    private void StartCreateMatchup()
    {
        Intent intent = new Intent(this, ActivityCreateMatchup.class);

        startActivity(intent);
    }

}
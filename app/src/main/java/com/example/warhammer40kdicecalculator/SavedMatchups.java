package com.example.warhammer40kdicecalculator;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.warhammer40kdicecalculator.databinding.ActivitySavedMatchups2Binding;

import java.io.File;

public class SavedMatchups extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d("sus","kommer den hit");

        LayoutInflater inflater = getLayoutInflater();


        File dir = new File("SavedMatchups");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing)
            {
                // Do something with child


                View matchupButton = inflater.inflate(R.layout.matchupbutton,findViewById(R.id.SavedMatchupLinearLayout));

                ((Button)matchupButton).setText(child.getName());
               // inflater.inflate(R.,matchupsLayout);

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.

            Log.d("saved matchups", "onCreate: sket sig med att kolla igenom foldern" );
        }
    }

}
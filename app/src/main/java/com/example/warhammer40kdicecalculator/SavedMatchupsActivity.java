package com.example.warhammer40kdicecalculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.HammerOfTheEmperor;
import com.example.warhammer40kdicecalculator.Abilities.ReRollAmountOfHits;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedAttackAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class SavedMatchupsActivity extends AppCompatActivity {


    private Context context;
    private LinearLayout linearLayout;


    private AlertDialogDeleteFragment alertFragment = null;

    private FragmentManager fragmentManager = null;
    private int mStackLevel = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_matchups3);

        fragmentManager = getSupportFragmentManager();

        context = getBaseContext();

        linearLayout = findViewById(R.id.SavedMatchupLinearLayout);


        LayoutInflater inflater = getLayoutInflater();

        String filename = "myfile";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = getBaseContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            //Log.d("as", "onCreate: hej");
        }
        catch (Exception e)
        {
            Log.d("On create matchups", "onCreate: " + e.getMessage());
        }

        File dire = new File(getBaseContext().getFilesDir(),"SavedMatchups");
        File[] directoryListing = dire.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing)
            {
                // Do something with child

                TableLayout tableLayout = new TableLayout(context);
                TableRow tableRow = new TableRow(context);





                View matchupButton = inflater.inflate(R.layout.matchupbutton,linearLayout);


                Button button = matchupButton.findViewWithTag("MatchupButton");

                button.setText(child.getName());


                button.setTag("");

                ImageButton deleteButton = linearLayout.findViewWithTag("DeleteButton");

                deleteButton.setTag(child.getName());

            //    ConstraintLayout parentLayout = (ConstraintLayout)button.getParent();
            //    parentLayout.setTag(child.getName());
//
                //button.setId(androidx.constraintlayout.widget.R.id.closest);




            }
        } else {

            Log.d("saved matchups", "onCreate: sket sig med att kolla igenom foldern" );
        }
    //    Matchup matchupen = new Matchup("forsta matchupen", )

    }
    public void DeleteMatchupClick(View v)
    {

    //    mStackLevel++;
//
    //    // DialogFragment.show() will take care of adding the fragment
    //    // in a transaction.  We also want to remove any currently showing
    //    // dialog, so make our own transaction and take care of that here.
    //    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    //    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
    //    if (prev != null) {
    //        ft.remove(prev);
    //    }
    //    ft.addToBackStack(null);
//
    //    // Create and show the dialog.
    //    DialogFragment newFragment = AlertDialogDeleteFragment.newInstance(mStackLevel);
    //    newFragment.show(ft, "dialog");



    //    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SavedMatchupsActivity.this);
    //    alertBuilder.setMessage("Are you sure you want to delete this matchup?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
    //        @Override
    //        public void onClick(DialogInterface dialogInterface, int i) {
//
    //            ImageButton deleteButton = (ImageButton)linearLayout.findViewWithTag(v.getTag());
    //            ConstraintLayout viewToRemove = (ConstraintLayout) deleteButton.getParent();
//
    //            linearLayout.removeView(viewToRemove);
//
    //            FileHandler fileHandler = new FileHandler(context);
//
    //            fileHandler.DeleteMatchup((String) v.getTag());
//
    //        }
    //    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    //        @Override
    //        public void onClick(DialogInterface dialogInterface, int i) {
//
    //        }
    //    });

    //    AlertDialog dialog = alertBuilder.create();
//
    //    dialog.show();

        Fragment prev = fragmentManager.findFragmentByTag("DeleteWindow");
        if(prev == null)
        {
            AlertDialogDeleteFragment test = new AlertDialogDeleteFragment(this,(String) v.getTag());

            test.show(fragmentManager,"DeleteWindow");
        }
    //    prev = fragmentManager.findFragmentByTag((String) v.getTag());
    //    prev = fragmentManager.findFragmentByTag((String) "DeleteWindow");

    }

    public static class AlertDialogDeleteFragment extends DialogFragment
    {
        private SavedMatchupsActivity savedMatchupsActivity = null;
        private String tag = null;
        public AlertDialogDeleteFragment(SavedMatchupsActivity savedMatchupsActivity, String tag)
        {
            this.savedMatchupsActivity = savedMatchupsActivity;
            this.tag = tag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

            alertBuilder.setMessage("Are you sure you want to delete this matchup?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    savedMatchupsActivity.DeleteMatchupCallback(tag);

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            return alertBuilder.create();
        }
    }

    public void DeleteMatchupCallback(String tag)
    {
        ImageButton deleteButton = (ImageButton)linearLayout.findViewWithTag(tag);
        ConstraintLayout viewToRemove = (ConstraintLayout) deleteButton.getParent();
        linearLayout.removeView(viewToRemove);
        FileHandler fileHandler = new FileHandler(context);
        fileHandler.DeleteMatchup(tag);
    }

    public void OpenCompareActivity(View v)
    {
        Intent intent = new Intent(this, CompareActivity.class);

        intent.putExtra( "SourceFile",((Button)v).getText());

        startActivity(intent);
    }
}
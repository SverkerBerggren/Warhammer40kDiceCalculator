package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class Weapon_Popup extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_popup);

        View searchGroup = findViewById(R.id.SearchGroup);
        searchGroup.setVisibility(View.GONE);

        SearchView searchView = findViewById(R.id.searchView);
        ListView listView = findViewById(R.id.listView);

        ArrayList<String> searchList = new ArrayList<String>(){{
                add("Ability1");
                add("Ability2");
                add("Ability3");
        }};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,searchList);
        listView.setAdapter(arrayAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }
        });

    }

    public void ShowSearch(View v)
    {
        View searchGroup = findViewById(R.id.SearchGroup);
        searchGroup.setVisibility(View.VISIBLE);
    }
}
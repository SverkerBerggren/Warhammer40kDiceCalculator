package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;

public class Weapon_Popup extends AppCompatActivity {

    HashMap<String, Ability> map = new HashMap<String, Ability>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_popup);


    }

    public void ShowPopup(View v)
    {
        View popup = findViewById(R.id.Popup);
        popup.setVisibility(View.VISIBLE);
    }

    public void ClosePopup(View v)
    {
        View popup = findViewById(R.id.ConstraintLayoutPopup);
        popup.setVisibility(View.GONE);
    }

    public void ShowSearch(View v)
    {
        if (findViewById(R.id.SearchLayout) == null)
        {
            LayoutInflater inf = getLayoutInflater();
            ViewGroup constraintLayout = findViewById(R.id.ConstraintLayout);
            inf.inflate(R.layout.search_prefab, constraintLayout);
        }
        else
        {
            View searchLayout = findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.VISIBLE);
        }

        SearchView searchView = findViewById(R.id.searchView);
        ListView listView = findViewById(R.id.listView);

        ArrayList<String> searchList = new ArrayList<String>(){{
            add("Ability1");
            add("Deal More Damage");
            add("Take More Damage");
        }};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,searchList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)parent.getItemAtPosition(position);

                Ability ability = map.get(item);

                View searchLayout = findViewById(R.id.SearchLayout);
                searchLayout.setVisibility(View.GONE);


            }
        });
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


        //View searchGroup = findViewById(R.id.SearchGroup);
        //searchGroup.setVisibility(View.VISIBLE);
    }
}
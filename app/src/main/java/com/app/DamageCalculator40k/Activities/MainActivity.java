package com.app.DamageCalculator40k.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.TextView;

import core.Abilities.Ability;
import com.app.DamageCalculator40k.AbilityUIHolder;
import core.DatabaseManager;
import core.DatasheetModeling.GamePiece;
import core.Logging.Logger;
import core.Logging.Logging;

import com.app.DamageCalculator40k.FileHandling.AndroidFileHandler;
import com.app.DamageCalculator40k.Matchup;
import com.app.DamageCalculator40k.R;

import java.util.ArrayList;

public class  MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        AndroidFileHandler.InitializeFileHandler(context);
        DatabaseManager.InitializeDatabaseManager();
        Logger logger = Log::d;
        Logging.setLogger(logger);
    }

    public void OpenSavedMatchups(View v)
    {
        Intent intent = new Intent(this, SavedMatchupsActivity.class);
        startActivity(intent);
    }

    private void InflateSearch(ViewGroup baseView, Context context)
    {
        Log.d("infalter grejer", "inflatar den??");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        if (baseView.findViewById(R.id.SearchLayout) == null)
        {
            inflater.inflate(R.layout.search_prefab, baseView);
        }
        else
        {
            View searchLayout = baseView.findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.VISIBLE);
        }
    }


    // Why is it here? I am not sure
    public void SearchAbility(GamePiece gamePiece, Matchup matchup, ViewGroup baseView, Context context, AbilityUIHolder abilityUIHolder)
    {
        InflateSearch(baseView,context);
        SearchView searchView = baseView.findViewById(R.id.searchView);
        ListView listView = baseView.findViewById(R.id.listView);
        ArrayList<Ability> searchList = new ArrayList<>(DatabaseManager.getInstance().GetAbilities());

        ArrayAdapter<Ability> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,searchList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new OnClickAbilityItem(gamePiece, baseView, matchup, abilityUIHolder));
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

    private static class OnClickAbilityItem implements AdapterView.OnItemClickListener
    {
        private final GamePiece gamePiece;
        private final ViewGroup baseView;
        private final Matchup matchup;
        private final AbilityUIHolder abilityUIHolder;

        public OnClickAbilityItem(GamePiece gamePiece, ViewGroup baseView, Matchup matchup, AbilityUIHolder abilityUIHolder)
        {
            this.baseView = baseView;
            this.matchup = matchup;
            this.abilityUIHolder = abilityUIHolder;
            this.gamePiece = gamePiece;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String item = ((TextView)view).getText().toString();
            Ability ability = DatabaseManager.getInstance().GetAbility(item);

            gamePiece.GetAbilities().add(ability);
            abilityUIHolder.AbilityAdded(ability, gamePiece);

            View searchLayout = baseView.findViewById(R.id.SearchLayout);
            searchLayout.setVisibility(View.GONE);
            AndroidFileHandler.GetInstance().saveMatchup(matchup);
        }
    }

    public void StartParseActivity(View view)
    {
        Intent intent = new Intent(this, ParseActivity.class);

        startActivity(intent);
    }
}
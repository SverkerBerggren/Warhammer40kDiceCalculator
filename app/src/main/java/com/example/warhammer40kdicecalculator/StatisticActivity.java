package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class StatisticActivity extends AppCompatActivity {

    private TreeMap<Integer,Integer> woundsDealtDistribution = new TreeMap<>();
    private TreeMap<Integer,Integer> modelsSlainDistribution = new TreeMap<>();

    private ArrayList<Unit> listOfAttackingUnits = new ArrayList<Unit>();
    private Unit defendingUnit;
    private RollResult rollResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Intent intent = getIntent();

        int sizeOfAttack = intent.getIntExtra("AttackingUnitSize", -1);
        String matchupName = intent.getStringExtra("MatchupName");


        FileHandler fileHandler = new FileHandler(getBaseContext());

        Matchup matchup = fileHandler.getMatchup(matchupName);

        boolean myUnitsAttacking = intent.getBooleanExtra("FirendlyArmyAttacking", true);

        for (int i = 0; i < sizeOfAttack; i++)
        {
            int key = intent.getIntExtra("IndexOfUnitAttacking" + i, -1000);
            if (myUnitsAttacking)
            {
                listOfAttackingUnits.add(matchup.friendlyArmy.units.get(key));
            }
            else
            {
                listOfAttackingUnits.add(matchup.enemyArmy.units.get(key));
            }

        }

        int defendingUnitIndex = intent.getIntExtra("IndexOfUnitDefending", -1000);
        if (!myUnitsAttacking)
        {
            defendingUnit = matchup.friendlyArmy.units.get(defendingUnitIndex);
        }
        else
        {
            defendingUnit = matchup.enemyArmy.units.get(defendingUnitIndex);
        }

        GraphView graphView1 = findViewById(R.id.Graph1);
        GraphView graphView2 = findViewById(R.id.Graph2);



        RollingLogic rollLogic = new RollingLogic();

        rollResult = rollLogic.newCalculateDamage(listOfAttackingUnits, defendingUnit);

        ConvertResult(rollResult);
        InstaniateGraph(graphView1,woundsDealtDistribution);

        // series.setDrawDataPoints(true);
        // series.setDataPointsRadius(10);
        // series.setDrawBackground(true);
        // series.setBackgroundColor(Color.argb(80,179, 230, 255));
    }

    private void InstaniateGraph(GraphView graphView, TreeMap<Integer,Integer> distribution)
    {

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("Amount of wounds dealt");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(32);




        DataPoint[] dataPoints = CreateDataPoints(distribution);
        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(dataPoints);//new BarGraphSeries<>(CreateDatapoints(distribution))


        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getViewport().setMaxY(100);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(dataPoints[dataPoints.length -1].getX());

        graphView.getGridLabelRenderer().setTextSize(25);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length);

        graphView.getGridLabelRenderer().setHumanRounding(false);

        //    graphView.getViewport().setXAxisBoundsManual(false);
    //    graphView.getViewport().setMaxX(dataPoints[dataPoints.length -1].getX());



        barSeries.setSpacing(10);

        graphView.addSeries(barSeries);


        float percentage = 0;

        for(DataPoint point : dataPoints)
        {
            percentage += point.getY();
        }

        float accumulatedPercentage = 100;
        int loopNumber = 0;
        DataPoint[] lineGraphDatapoint = new DataPoint[dataPoints.length];
        for(DataPoint datapoint : dataPoints)
        {


            lineGraphDatapoint[loopNumber] = new DataPoint(datapoint.getX(),  accumulatedPercentage);

            accumulatedPercentage -= datapoint.getY();

            loopNumber++;
        }

        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(lineGraphDatapoint);

        graphView.addSeries(lineGraphSeries);



        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(10);
        lineGraphSeries.setDrawBackground(true);
        lineGraphSeries.setBackgroundColor(Color.argb(80,179, 230, 255));


        Log.d("procennt", "procenten: " + percentage);

        barSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                ConstraintLayout graphPopup = findViewById(R.id.graphPopup);
                TextView amountText = findViewById(R.id.amountOfWoundsText);
                TextView chanceText = findViewById(R.id.chanceOfWoundsText);
                amountText.setText("Amount of Wounds: " + dataPoint.getX());
                chanceText.setText("Chance of the Wounds: " + Math.round(dataPoint.getY() * 100.0) / 100.0 + " %");
                graphPopup.setVisibility(View.VISIBLE);
            }
        });


        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                TextView chanceOfMoreText = findViewById(R.id.chanceForMoreWoundsText);
                chanceOfMoreText.setText("Chance For More Wounds: " + Math.round(dataPoint.getY() * 100.0) / 100.0 + " %");
            }
        });
        //graphView.addSeries();

    }

    private DataPoint[] CreateDataPoints(TreeMap<Integer,Integer> distribution)
    {
        DataPoint[] returnValue = new DataPoint[distribution.size()];


        int loopNumber = 0;



        for(int x : distribution.keySet())
        {

            float y = (float)(distribution.get(x)/ 100.0);

            returnValue[loopNumber] = new DataPoint(x,y);
            loopNumber++;
        }



        return returnValue;
    }

    private void ConvertResult(RollResult rollResult)
    {
        for(int i = 0; i < rollResult.woundsDealt.size();i++)
        {
            int value = rollResult.woundsDealt.get(i);

            if(!woundsDealtDistribution.containsKey(value))
            {
                woundsDealtDistribution.put(value,1);
            }
            else
            {
                woundsDealtDistribution.put(value, woundsDealtDistribution.get(value) +1);
            }
        }

        for(int i = 0; i < rollResult.modelsSlain.size();i++)
        {
            int value = rollResult.modelsSlain.get(i);

            if(!modelsSlainDistribution.containsKey(value))
            {
                modelsSlainDistribution.put(value,1);
            }
            else
            {
                modelsSlainDistribution.put(value, modelsSlainDistribution.get(value) +1);
            }
        }
    }
}
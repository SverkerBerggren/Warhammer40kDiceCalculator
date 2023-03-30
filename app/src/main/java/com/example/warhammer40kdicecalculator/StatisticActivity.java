package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        GraphView graphView1 = findViewById(R.id.Graph1);
        GraphView graphView2 = findViewById(R.id.Graph2);




        MainActivity mainActivity = new MainActivity();

        Unit manticore = new Unit();
        Model manticoreHunterKillerMissile = new Model();
        manticoreHunterKillerMissile.ballisticSkill = 4;
        RangedWeapon hunterKillerMissile = new RangedWeapon(10,-2,new DamageAmount(3,0,0),new RangedAttackAmount(0,0,2));
        manticoreHunterKillerMissile.listOfRangedWeapons.add(hunterKillerMissile);
        manticore.listOfModels.add(manticoreHunterKillerMissile);

        manticore.listOfAbilitys.add(new HammerOfTheEmperor());
        manticoreHunterKillerMissile.listOfAbilites.add(new HammerOfTheEmperor());
        //  hej.newCalculateDamage(conscripts,spaceMarineIntercessorUnit);
        manticore.listOfAbilitys.add(new ReRollAmountOfHits());

        Unit spaceMarineIntercessorUnit = new Unit();
        Model intercessor = new Model();
        intercessor.wounds = 2;
        intercessor.armorSave = 3;
        intercessor.toughness = 4;

        for(int i =0; i <10; i ++)
        {
            spaceMarineIntercessorUnit.listOfModels.add(new Model(intercessor));
        }
        ArrayList<Unit> listToCompare = new ArrayList<>();

        listToCompare.add(manticore);

        RollingLogic rollLogic = new RollingLogic();

        RollResult rollResult = rollLogic.newCalculateDamage(listToCompare,spaceMarineIntercessorUnit);

        ConvertResult(rollResult);


        // series.setDrawDataPoints(true);
        // series.setDataPointsRadius(10);
        // series.setDrawBackground(true);
        // series.setBackgroundColor(Color.argb(80,179, 230, 255));

        InstaniateGraph(graphView1,woundsDealtDistribution);



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
                Log.d("Stats", "On Tap Bar: " + dataPoint.getY());
            }
        });


        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("Stats", "On Tap line: " + dataPoint.getY());
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
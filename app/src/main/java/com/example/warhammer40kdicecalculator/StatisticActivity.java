package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class StatisticActivity extends AppCompatActivity {

    private TreeMap<Integer,Integer> woundsDealtDistribution = new TreeMap<>();
    private TreeMap<Integer,Integer> modelsSlainDistribution = new TreeMap<>();

    private ArrayList<Unit> listOfAttackingUnits = new ArrayList<Unit>();
    private Unit defendingUnit;
    private RollResult rollResult;
    private Toast activeToast;

    private Conditions conditions;

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

        String attackingString = "";
        String defendingString = "";
        for (int i = 0; i < sizeOfAttack; i++)
        {
            int key = intent.getIntExtra("IndexOfUnitAttacking" + i, -1000);
            if (myUnitsAttacking)
            {
                listOfAttackingUnits.add(matchup.friendlyArmy.units.get(key));
                if (i == sizeOfAttack -1)
                {
                    attackingString += matchup.friendlyArmy.units.get(key).unitName;
                }
                else
                {
                    attackingString += matchup.friendlyArmy.units.get(key).unitName + ", ";
                }
            }
            else
            {
                listOfAttackingUnits.add(matchup.enemyArmy.units.get(key));
                if (i == sizeOfAttack -1)
                {
                    attackingString += matchup.enemyArmy.units.get(key).unitName;
                }
                else
                {
                    attackingString += matchup.enemyArmy.units.get(key).unitName + " ,";
                }
            }

        }

        int defendingUnitIndex = intent.getIntExtra("IndexOfUnitDefending", -1000);
        if (!myUnitsAttacking)
        {
            defendingUnit = matchup.friendlyArmy.units.get(defendingUnitIndex);
            defendingString += defendingUnit.unitName;
        }
        else
        {
            defendingUnit = matchup.enemyArmy.units.get(defendingUnitIndex);
            defendingString += defendingUnit.unitName;
        }

        TextView attackingText = (TextView)findViewById(R.id.AttackingUnitsText);
        TextView defendingText = (TextView)findViewById(R.id.DefendingUnitText);
        attackingText.setText(attackingString);
        defendingText.setText(defendingString);

        GraphView graphView1 = findViewById(R.id.Graph1);
        GraphView graphView2 = findViewById(R.id.Graph2);



        RollingLogic rollLogic = new RollingLogic();

        Army attackingArmy;
        Army defendingArmy;

        if(myUnitsAttacking)
        {
            attackingArmy = matchup.friendlyArmy;
            defendingArmy = matchup.enemyArmy;
        }
        else
        {
            attackingArmy = matchup.enemyArmy;
            defendingArmy = matchup.friendlyArmy;
        }


        rollResult = rollLogic.newCalculateDamage(listOfAttackingUnits, defendingUnit, attackingArmy,defendingArmy,conditions);

        ConvertResult(rollResult);

        graphView1.setTitle("Amount of wounds dealt");
        InstaniateGraph(graphView1,woundsDealtDistribution, true);
        graphView2.setTitle("Amount of Enemies killed");
        InstaniateGraph(graphView2, modelsSlainDistribution, false);

        // series.setDrawDataPoints(true);
        // series.setDataPointsRadius(10);
        // series.setDrawBackground(true);
        // series.setBackgroundColor(Color.argb(80,179, 230, 255));
    }

    private void InstaniateGraph(GraphView graphView, TreeMap<Integer,Integer> distribution, boolean isGraph1)
    {

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf,nf));
        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(32);
        graphView.getViewport().setScalable(true);

        DataPoint[] dataPoints = CreateDataPoints(distribution);
        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(dataPoints);//new BarGraphSeries<>(CreateDatapoints(distribution))


        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getViewport().setMaxY(100);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(dataPoints[dataPoints.length - 1].getX());

        graphView.getGridLabelRenderer().setHumanRounding(true);
        graphView.getGridLabelRenderer().setTextSize(20);

        graphView.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Amount");


        graphView.getGridLabelRenderer().setNumVerticalLabels(10);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Percent");


        //    graphView.getViewport().setXAxisBoundsManual(false);
    //    graphView.getViewport().setMaxX(dataPoints[dataPoints.length -1].getX());



        barSeries.setSpacing(10);

        graphView.addSeries(barSeries);

        float accumulatedPercentage = 100;
        int loopNumber = 0;

        int adding = 0;
        for (int i = 0; i < dataPoints[0].getX(); i++)
        {
            adding++;
        }

        DataPoint[] lineGraphDatapoint = new DataPoint[dataPoints.length + adding];
        boolean firstNumbers = true;
        for(DataPoint datapoint : dataPoints)
        {
            if (firstNumbers)
            {
                for (int i = 0; i < datapoint.getX(); i++)
                {
                    lineGraphDatapoint[loopNumber] = new DataPoint(i, 100);
                    loopNumber++;
                }
                firstNumbers = false;
//
            }
            lineGraphDatapoint[loopNumber] = new DataPoint(datapoint.getX(),  accumulatedPercentage);

            accumulatedPercentage -= datapoint.getY();

            loopNumber++;
        }

        LineGraphSeriesSelfMade<DataPoint> lineGraphSeries = new LineGraphSeriesSelfMade<>(lineGraphDatapoint);

        graphView.addSeries(lineGraphSeries);

        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(10);
        lineGraphSeries.setDrawBackground(true);
        lineGraphSeries.setBackgroundColor(Color.argb(80,179, 230, 255));
        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Iterator<DataPoint> barDataPoints = barSeries.getValues(dataPoint.getX() + 1, dataPoint.getX() + 1);
                if (isGraph1)
                {
                    ShowToastMessage("Amount of Wounds: " + dataPoint.getX() + "\n" +
                            "Chance for Wounds: " + Math.round(barDataPoints.next().getY()  * 100.0) / 100.0 + " %\n" +
                            "Chance for " + dataPoint.getX() + " or more Wounds: " + + Math.round(dataPoint.getY() * 100.0) / 100.0 + " %"
                    );
                }
               else
                {
                    ShowToastMessage("Amount of Models Slain: " + dataPoint.getX() + "\n" +
                            "Chance for Models Slain: " + Math.round(barDataPoints.next().getY()  * 100.0) / 100.0 + " %\n" +
                            "Chance for " + dataPoint.getX() + " or more Models Slain: " + + Math.round(dataPoint.getY() * 100.0) / 100.0 + " %"
                    );
                }
            }
        });


        //graphView.addSeries();

    }

    private class LineGraphSeriesSelfMade<E extends DataPointInterface> extends LineGraphSeries
    {
        public LineGraphSeriesSelfMade(E[] data) {
            super(data);
            init();
        }
        @Override
        protected DataPointInterface findDataPoint(float x, float y) {
            return super.findDataPointAtX(x);
        }

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


    private void ShowToastMessage(String message)
    {
        if (activeToast != null)
        {
            activeToast.cancel();
        }
        activeToast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        activeToast.show();
    }
}
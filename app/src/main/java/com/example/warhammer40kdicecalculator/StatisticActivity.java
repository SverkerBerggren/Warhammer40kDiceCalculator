package com.example.warhammer40kdicecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class StatisticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        GraphView graphView1 = findViewById(R.id.Graph1);
        GraphView graphView2 = findViewById(R.id.Graph2);

        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(new DataPoint[]{
            new DataPoint(0, 30),
            new DataPoint(1, 20),
            new DataPoint(2, 64),
            new DataPoint(3, 39),
            new DataPoint(4, 69),
            new DataPoint(5, 92),
            new DataPoint(6, 54),
            new DataPoint(7, 23),
            new DataPoint(8, 46),


        });

        barSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("Stats", "On Tap Bar: " + dataPoint.getY());
            }
        });

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 100),
                new DataPoint(1, 100),
                new DataPoint(2, 94),
                new DataPoint(3, 90),
                new DataPoint(4, 70),
                new DataPoint(5, 65),
                new DataPoint(6, 54),
                new DataPoint(7, 30),
                new DataPoint(8, 10)
        });
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(80,179, 230, 255));

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Log.d("Stats", "On Tap Line: " + dataPoint.getY());
            }
        });

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView1.setTitle("Damage");

        // on below line we are setting
        // text color to our graph view.
        graphView1.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView1.setTitleTextSize(32);

        graphView1.getViewport().setYAxisBoundsManual(true);
        graphView1.getViewport().setMaxY(100);

        barSeries.setSpacing(50);
        // on below line we are adding
        // data series to our graph view.
        graphView1.addSeries(series);
        graphView1.addSeries(barSeries);
    }
}
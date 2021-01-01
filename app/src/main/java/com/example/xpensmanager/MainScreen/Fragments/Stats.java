package com.example.xpensmanager.MainScreen.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xpensmanager.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.hadiidbouk.charts.ChartProgressBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.dacer.androidcharts.BarView;
import im.dacer.androidcharts.LineView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Stats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stats extends Fragment {
    PieChart pchart;
    HorizontalBarChart hchart;
    public Stats() {
        // Required empty public constructor
    }


    public static Stats newInstance(String param1, String param2) {
        Stats fragment = new Stats();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ChartProgressBar mChart = view.findViewById(R.id.ChartProgressBar);
        BarView barView = view.findViewById(R.id.bar_view);
        LineView lineView = view.findViewById(R.id.line_view);
        pchart = view.findViewById(R.id.chart1);
        hchart = view.findViewById(R.id.chart2);

        // chart.setHighlightEnabled(false);
        hchart.setDrawBarShadow(false);
        hchart.setDrawValueAboveBar(true);
        hchart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        //hchart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        hchart.setPinchZoom(false);
        // draw shadows for each bar that show the maximum value
        // chart.setDrawBarShadow(true);
        hchart.setDrawGridBackground(false);

        XAxis xl = hchart.getXAxis();
        List<String> lbl = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
        xl.setValueFormatter(new IndexAxisValueFormatter(lbl));
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setDrawLabels(true);
        xl.setGranularity(1f);
        xl.setGranularityEnabled(true);
        xl.setCenterAxisLabels(true);
        //xl.setLabelCount(xAsixLabel.size());

        YAxis yl = hchart.getAxisLeft();
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setDrawLabels(false);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);
        YAxis yr = hchart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setDrawLabels(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);
        hchart.setFitBars(true);
        hchart.animateY(500);
        hchart.setDoubleTapToZoomEnabled(false);

        Legend lh = hchart.getLegend();
        lh.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        lh.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lh.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lh.setDrawInside(false);
        lh.setFormSize(8f);
        lh.setXEntrySpace(4f);
        setDataHorizontalChart(12,12);


        pchart.setUsePercentValues(true);
        pchart.getDescription().setEnabled(false);
        pchart.setExtraOffsets(5, 10, 5, 5);
        pchart.setDragDecelerationFrictionCoef(0.95f);
        pchart.setDrawHoleEnabled(false);
        pchart.setHoleColor(Color.WHITE);
        pchart.setTransparentCircleColor(Color.WHITE);
        pchart.setTransparentCircleAlpha(110);
        pchart.setHoleRadius(20f);
        pchart.setTransparentCircleRadius(22f);
        pchart.setDrawCenterText(true);
        pchart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pchart.setRotationEnabled(false);
        pchart.setHighlightPerTapEnabled(true);
        pchart.animateY(1400, Easing.EaseInOutQuad);
        Legend l = pchart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(2f);
        l.setYOffset(3f);
        setData(6,20);
        // entry label styling
        //chart.setEntryLabelColor(Color.BLACK);
        //chart.setEntryLabelTextSize(12f);




        lineView.setDrawDotLine(true); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView.setBottomTextList(new ArrayList<>(Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")));
        lineView.setColorArray(new int[]{getActivity().getResources().getColor(R.color.theme_red),getActivity().getResources().getColor(R.color.theme_blue),getActivity().getResources().getColor(R.color.theme_yellow),
                getActivity().getResources().getColor(R.color.theme_cyan),getActivity().getResources().getColor(R.color.theme_orange),getActivity().getResources().getColor(R.color.theme_blue_variant)});
        ArrayList<Integer> al = new ArrayList<>(Arrays.asList(90,30,56,36,75,80,40,70,42,53,44,52));
        ArrayList<Integer> al2 = new ArrayList<>(Arrays.asList(80,40,70,42,53,56,55,85,74,65,25,96));
        ArrayList<Integer> al4 = new ArrayList<>(Arrays.asList(56,55,85,74,65,74,85,89,55,54,33,45));
        //ArrayList<Integer> al5 = new ArrayList<>(Arrays.asList(74,85,89,55,54,56,95,94,53,63,65,98));
        //ArrayList<Integer> al3 = new ArrayList<>(Arrays.asList(56,95,94,53,63,90,30,56,36,75,80,40));
        ArrayList<ArrayList<Integer>>  lis = new ArrayList<>();
        lis.add(al);lis.add(al2);//lis.add(al3);
        lis.add(al4);//lis.add(al5);
        lineView.setDataList(lis); //or lineView.setFloatDataList(floatDataLists)


        barView.setBottomTextList(new ArrayList<>(Arrays.asList("Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec")));
        barView.setDataList(new ArrayList<>(Arrays.asList(9,5,6,3,5,8,6,4,5,2,6,8)),10);


        ArrayList<com.hadiidbouk.charts.BarData> dataList = new ArrayList<>();
        com.hadiidbouk.charts.BarData data = new com.hadiidbouk.charts.BarData("Sun", 3.4f, "3.4€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Mon", 22f, "8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Tue", 1.8f, "1.8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Wed", 7.3f, "7.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Thu", 6.2f, "6.2€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Fri", 3.3f, "3.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Sat", 3.3f, "3.3€");
        dataList.add(data);
        mChart.setDataList(dataList);
        mChart.build();

        return view;
    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        String parties[] = {"Alpha","Beta","Calcium","Drone","Enlarged","Fcake"};
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    parties[i % parties.length],
                    getResources().getDrawable(R.drawable.dot)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getActivity().getResources().getColor(R.color.theme_red));
        colors.add(getActivity().getResources().getColor(R.color.theme_green));
        colors.add(getActivity().getResources().getColor(R.color.theme_yellow));
        colors.add(getActivity().getResources().getColor(R.color.theme_blue));
        colors.add(getActivity().getResources().getColor(R.color.theme_cyan));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange_variant));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pchart.setData(data);
        pchart.setEntryLabelColor(Color.WHITE);
        pchart.setEntryLabelTextSize(11f);
        // undo all highlights
        //chart.highlightValues(null);
        pchart.highlightValue(10f,2);

        pchart.invalidate();
    }

    private void setDataHorizontalChart(int count, float range) {

        float barWidth = 7f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> values = new ArrayList<>();


        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            values.add(new BarEntry(i * spaceForBar, val));
        }

        BarDataSet set1;


            set1 = new BarDataSet(values, "All Time Spends - Month Wise");

            set1.setDrawIcons(false);
            //set1.setStackLabels(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);

            data.setBarWidth(barWidth);
            hchart.setData(data);

    }


}
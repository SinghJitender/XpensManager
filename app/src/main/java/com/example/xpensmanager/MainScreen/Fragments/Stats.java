package com.example.xpensmanager.MainScreen.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xpensmanager.Database.GenericExpenseDB;
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
import java.util.HashMap;
import java.util.List;

import im.dacer.androidcharts.BarView;
import im.dacer.androidcharts.LineView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Stats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stats extends Fragment {
    PieChart pchart,pieChartGroup;
    GenericExpenseDB genericExpenseDB;
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
        ChartProgressBar yChart = view.findViewById(R.id.ChartProgressBarYearly);
        ChartProgressBar dChart = view.findViewById(R.id.ChartProgressBarEachDay);
        //BarView barView = view.findViewById(R.id.bar_view);
        LineView lineView = view.findViewById(R.id.line_view);
        pchart = view.findViewById(R.id.chart1);
        pieChartGroup = view.findViewById(R.id.pieChartGroup);
        genericExpenseDB = new GenericExpenseDB(getActivity());

        HashMap<String,Double> weekList = genericExpenseDB.getSumForAllWeekDays();
        HashMap<String,Double> yearList = genericExpenseDB.getSumForAllMonths();
        HashMap<Integer,Double> dayList = genericExpenseDB.getSumForAllDays();
        HashMap<String,Double> categoryList = genericExpenseDB.getSumByCategory();
        HashMap<String,Double> groupList = genericExpenseDB.getSumByGroup();

        double totalSpends = genericExpenseDB.getAllExpenseSum();
        Log.d("Stats",weekList+"");
        Log.d("Stats",yearList+"");
        Log.d("Stats",dayList+"");
        Log.d("Stats",categoryList+"");
        Log.d("Stats",groupList+"");

        //Weekly Chart View
        mChart.setDataList(setWeeklyChart(weekList,totalSpends));
        mChart.setMaxValue(100);
        mChart.build();

        //Yearly Chart View
        yChart.setDataList(setYearlyChart(yearList,totalSpends));
        yChart.setMaxValue(100);
        yChart.build();

        //Each Day Chart View
        dChart.setDataList(setDailyChart(dayList,totalSpends));
        dChart.setMaxValue(100);
        dChart.build();

        //Set Category Data
        setCategoryData(categoryList,totalSpends);

        //Set Group Data
        setGroupData(groupList,totalSpends);




        lineView.setDrawDotLine(true); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView.setBottomTextList(new ArrayList<>(Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")));
        lineView.setColorArray(new int[]{getActivity().getResources().getColor(R.color.theme_red),getActivity().getResources().getColor(R.color.theme_blue),getActivity().getResources().getColor(R.color.theme_yellow),
                getActivity().getResources().getColor(R.color.theme_cyan),getActivity().getResources().getColor(R.color.theme_orange),getActivity().getResources().getColor(R.color.theme_blue_variant)});
        ArrayList<Integer> al = new ArrayList<>(Arrays.asList(44,30,56,36,75,80,40,70,42,53,44,52));
        ArrayList<Integer> al2 = new ArrayList<>(Arrays.asList(80,40,70,42,53,56,55,85,74,65,25,96));
        ArrayList<Integer> al4 = new ArrayList<>(Arrays.asList(56,55,85,74,65,74,85,89,55,54,33,45));
        //ArrayList<Integer> al5 = new ArrayList<>(Arrays.asList(74,85,89,55,54,56,95,94,53,63,65,98));
        //ArrayList<Integer> al3 = new ArrayList<>(Arrays.asList(56,95,94,53,63,90,30,56,36,75,80,40));
        ArrayList<ArrayList<Integer>>  lis = new ArrayList<>();
        lis.add(al);lis.add(al2);//lis.add(al3);
        lis.add(al4);//lis.add(al5);
        lineView.setDataList(lis); //or lineView.setFloatDataList(floatDataLists)


       /* barView.setBottomTextList(new ArrayList<>(Arrays.asList("Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec")));
        barView.setDataList(new ArrayList<>(Arrays.asList(9,5,6,3,5,8,6,4,5,2,6,8)),10);*/


        return view;
    }

    private ArrayList<com.hadiidbouk.charts.BarData> setWeeklyChart(HashMap<String,Double> weekList,double totalSpends){
        ArrayList<com.hadiidbouk.charts.BarData> dataList = new ArrayList<>();
        com.hadiidbouk.charts.BarData data = new com.hadiidbouk.charts.BarData("Sun", (float) (((weekList.containsKey("Sunday")?weekList.get("Sunday"):0.0)/totalSpends)*100), "3.4€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Mon", (float) (((weekList.containsKey("Monday")?weekList.get("Monday"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Tue", (float) (((weekList.containsKey("Tuesday")?weekList.get("Tuesday"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Wed", (float) (((weekList.containsKey("Wednesday")?weekList.get("Wednesday"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Thu", (float) (((weekList.containsKey("Thursday")?weekList.get("Thursday"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Fri", (float) (((weekList.containsKey("Friday")?weekList.get("Friday"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("Sat", (float) (((weekList.containsKey("Saturday")?weekList.get("Saturday"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        return dataList;
    }

    private ArrayList<com.hadiidbouk.charts.BarData> setYearlyChart(HashMap<String,Double> yearList,double totalSpends){
        ArrayList<com.hadiidbouk.charts.BarData> dataList = new ArrayList<>();
        com.hadiidbouk.charts.BarData data = new com.hadiidbouk.charts.BarData("JAN", (float) (((yearList.containsKey("January")?yearList.get("January"):0.0)/totalSpends)*100), "3.4€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("FEB", (float) (((yearList.containsKey("February")?yearList.get("February"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("MAR", (float) (((yearList.containsKey("March")?yearList.get("March"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("APR", (float) (((yearList.containsKey("April")?yearList.get("April"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("MAY", (float) (((yearList.containsKey("May")?yearList.get("May"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("JUN", (float) (((yearList.containsKey("June")?yearList.get("June"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("JUL", (float) (((yearList.containsKey("July")?yearList.get("July"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("AUG", (float) (((yearList.containsKey("August")?yearList.get("August"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("SEP", (float) (((yearList.containsKey("September")?yearList.get("September"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("OCT", (float) (((yearList.containsKey("October")?yearList.get("October"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("NOV", (float) (((yearList.containsKey("November")?yearList.get("November"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new com.hadiidbouk.charts.BarData("DEC", (float) (((yearList.containsKey("December")?yearList.get("December"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        return dataList;
    }

    private ArrayList<com.hadiidbouk.charts.BarData> setDailyChart(HashMap<Integer,Double> dayList,double totalSpends){
        ArrayList<com.hadiidbouk.charts.BarData> dataList = new ArrayList<>();
        for(int i=1;i<=31;i++){
            com.hadiidbouk.charts.BarData data = new com.hadiidbouk.charts.BarData(i+"", (float) (((dayList.containsKey(i)?dayList.get(i):0.0)/totalSpends)*100), "0.0");
            dataList.add(data);
        }
        return dataList;
    }

    private void setCategoryData(HashMap<String,Double> categoryList,double totalSpends) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for(String key : categoryList.keySet()){
            entries.add(new PieEntry((float) ((categoryList.get(key)/totalSpends)*100),
                    key,
                    getResources().getDrawable(R.drawable.dot)));
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getActivity().getResources().getColor(R.color.theme_blue_variant));
        colors.add(getActivity().getResources().getColor(R.color.theme_red));
        colors.add(getActivity().getResources().getColor(R.color.theme_yellow));
        colors.add(getActivity().getResources().getColor(R.color.theme_blue));
        colors.add(getActivity().getResources().getColor(R.color.theme_cyan));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange));
        colors.add(getActivity().getResources().getColor(R.color.black));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange_variant));
        colors.add(getActivity().getResources().getColor(R.color.theme_green));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setDrawValues(false);
        pchart.setUsePercentValues(true);
        pchart.getDescription().setEnabled(false);
        pchart.setExtraOffsets(5, 10, 5, 5);
        pchart.setDragDecelerationFrictionCoef(0.95f);
        pchart.setDrawHoleEnabled(true);
        pchart.setHoleColor(Color.WHITE);
        pchart.setTransparentCircleColor(Color.WHITE);
        pchart.setTransparentCircleAlpha(110);
        pchart.setHoleRadius(44f);
        pchart.setTransparentCircleRadius(48f);
        pchart.setDrawCenterText(true);
        pchart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pchart.setRotationEnabled(false);
        pchart.setHighlightPerTapEnabled(true);
        pchart.animateY(500, Easing.EaseInOutQuad);
        Legend l = pchart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(2f);
        l.setYOffset(3f);
        pchart.setData(data);
        pchart.setEntryLabelColor(Color.WHITE);
        pchart.setEntryLabelTextSize(11f);
        // undo all highlights
        //chart.highlightValues(null);
        pchart.setDrawEntryLabels(false);
        pchart.invalidate();
    }

    private void setGroupData(HashMap<String,Double> groupList,double totalSpends) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for(String key : groupList.keySet()){
            entries.add(new PieEntry((float) ((groupList.get(key)/totalSpends)*100),
                    key,
                    getResources().getDrawable(R.drawable.dot)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getActivity().getResources().getColor(R.color.theme_blue_variant));
        colors.add(getActivity().getResources().getColor(R.color.theme_red));
        colors.add(getActivity().getResources().getColor(R.color.theme_yellow));
        colors.add(getActivity().getResources().getColor(R.color.theme_blue));
        colors.add(getActivity().getResources().getColor(R.color.theme_cyan));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange));
        colors.add(getActivity().getResources().getColor(R.color.black));
        colors.add(getActivity().getResources().getColor(R.color.theme_orange_variant));
        colors.add(getActivity().getResources().getColor(R.color.theme_green));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setDrawValues(false);
        pieChartGroup.setUsePercentValues(true);
        pieChartGroup.getDescription().setEnabled(false);
        pieChartGroup.setExtraOffsets(5, 10, 5, 5);
        pieChartGroup.setDragDecelerationFrictionCoef(0.95f);
        pieChartGroup.setDrawHoleEnabled(true);
        pieChartGroup.setHoleColor(Color.WHITE);
        pieChartGroup.setTransparentCircleColor(Color.WHITE);
        pieChartGroup.setTransparentCircleAlpha(110);
        pieChartGroup.setHoleRadius(44f);
        pieChartGroup.setTransparentCircleRadius(48f);
        pieChartGroup.setDrawCenterText(true);
        pieChartGroup.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChartGroup.setRotationEnabled(false);
        pieChartGroup.setHighlightPerTapEnabled(true);
        pieChartGroup.animateY(500, Easing.EaseInOutQuad);
        Legend l = pieChartGroup.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(2f);
        l.setYOffset(3f);
        pieChartGroup.setData(data);
        pieChartGroup.setEntryLabelColor(Color.WHITE);
        pieChartGroup.setEntryLabelTextSize(11f);
        // undo all highlights
        //chart.highlightValues(null);
        pieChartGroup.setDrawEntryLabels(false);
        pieChartGroup.invalidate();
    }

}
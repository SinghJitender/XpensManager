package com.jitender.xpensmanager.MainScreen.Fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.jitender.xpensmanager.Database.ExpenseDB;
import com.jitender.xpensmanager.MainScreen.MainActivity;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.whiteelephant.monthpicker.MonthPickerDialog;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Stats extends Fragment {
    private PieChart pchart,pieChartGroup,pieChartPayment;
    private ExpenseDB expenseDB;
    private HashMap<String,Double> weekList,yearList,groupList,paymentList;
    private LinkedHashMap<String,Double> categoryList;
    private int monthCount, weekCount, dayCount, totalDistinctMonthCount;
    private HashMap<Integer,Double> dayList;
    private ExecutorService mExecutor;
    private double totalSpends,todaysTotalExpense, monthsTotalExpense,yearsTotalExpense;
    private ChartProgressBar mChart, yChart, dChart;
    private TextView overallMonthlyExpense,monthName,currentMonthAverage,weekDayName,currentWeekAverage,dayName,currentDayAverage,
            percentageOfMonthlySalary,percentageOfMonthlySalaryText,selectdate,todaysExpense,selectyear,yearsExpense,selectmonthyear, monthsExpense;
    private TextView category1,category2,category3,category4,category5;
    private CardView categoryChartView,groupChartView,paymentChartView;
    private LinearLayout mainLinearLayout;
    private CoordinatorLayout emptyView;
    private Calendar myCalendar;

    public Stats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weekList = new HashMap<>();
        yearList =  new HashMap<>();
        dayList =  new HashMap<>();
        categoryList =  new LinkedHashMap<>();
        groupList =  new HashMap<>();
        paymentList = new HashMap<>();
        mExecutor = Executors.newFixedThreadPool(4);
        expenseDB = new ExpenseDB(getActivity());
        totalSpends = expenseDB.getAllExpenseSum();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        mChart = view.findViewById(R.id.ChartProgressBar);
        yChart = view.findViewById(R.id.ChartProgressBarYearly);
        dChart = view.findViewById(R.id.ChartProgressBarEachDay);
        //BarView barView = view.findViewById(R.id.bar_view);
       // LineView lineView = view.findViewById(R.id.line_view);
        pchart = view.findViewById(R.id.chart1);
        pieChartGroup = view.findViewById(R.id.pieChartGroup);
        pieChartPayment = view.findViewById(R.id.pieChartPayment);
        overallMonthlyExpense= view.findViewById(R.id.overallMonthlyExpense);
        monthName = view.findViewById(R.id.monthName);
        weekDayName = view.findViewById(R.id.weekDayName);
        dayName = view.findViewById(R.id.dayName);
        currentMonthAverage = view.findViewById(R.id.currentMonthAverage);
        currentWeekAverage = view.findViewById(R.id.currentWeekAverage);
        currentDayAverage = view.findViewById(R.id.currentDayAverage);
        category1 = view.findViewById(R.id.category1);
        category2 = view.findViewById(R.id.category2);
        category3 = view.findViewById(R.id.category3);
        category4 = view.findViewById(R.id.category4);
        category5 = view.findViewById(R.id.category5);
        categoryChartView = view.findViewById(R.id.categoryChartView);
        groupChartView = view.findViewById(R.id.groupChartView);
        paymentChartView = view.findViewById(R.id.paymentChartView);
        percentageOfMonthlySalary = view.findViewById(R.id.percentageOfMonthlySalary);
        percentageOfMonthlySalaryText =  view.findViewById(R.id.percentageOfMonthlySalaryText);
        mainLinearLayout = view.findViewById(R.id.mainLinearLayout);
        emptyView = view.findViewById(R.id.frameLayout);
        todaysExpense = view.findViewById(R.id.todaysExpense);
        monthsExpense = view.findViewById(R.id.monthsExpense);
        yearsExpense = view.findViewById(R.id.yearsExpense);
        selectdate = view.findViewById(R.id.selectdate);
        selectyear = view.findViewById(R.id.selectyear);
        selectmonthyear = view.findViewById(R.id.selectmonthyear);

        myCalendar = Calendar.getInstance();
        if(expenseDB.getExpenseCount()<=0){
            mainLinearLayout.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            mainLinearLayout.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            //Sum of all weekdays
            mExecutor.execute(()->{
                if(weekList.size()>0){
                    weekList.clear();
                }
                weekList = expenseDB.getSumForAllWeekDays();
                Log.d("Stats",weekList+"");
                getActivity().runOnUiThread(()->{
                    //Weekly Chart View
                    mChart.setDataList(setWeeklyChart(weekList,totalSpends));
                    mChart.setMaxValue(100);
                    mChart.build();
                });
            });

            //Sum of all months
            mExecutor.execute(()->{
                if(yearList.size()>0){
                    yearList.clear();
                }
                yearList = expenseDB.getSumForAllMonths();
                Log.d("Stats",yearList+"");
                getActivity().runOnUiThread(()->{
                    //Yearly Chart View
                    yChart.setDataList(setYearlyChart(yearList,totalSpends));
                    yChart.setMaxValue(100);
                    yChart.build();
                });
            });

            //Sum of all days
            mExecutor.execute(()->{
                if(dayList.size()>0){
                    dayList.clear();
                }
                dayList = expenseDB.getSumForAllDays();
                Log.d("Stats",dayList+"");
                getActivity().runOnUiThread(()->{
                    //Each Day Chart View
                    dChart.setDataList(setDailyChart(dayList,totalSpends));
                    dChart.setMaxValue(100);
                    dChart.build();
                });
            });

            //Sum of all categories
            mExecutor.execute(()->{
                if(categoryList.size()>0){
                    categoryList.clear();
                }
                categoryList = expenseDB.getSumByCategory();
                ArrayList<TextView> categoryTextViewList = new ArrayList<>();
                categoryTextViewList.add(category1);categoryTextViewList.add(category2);categoryTextViewList.add(category3);categoryTextViewList.add(category4);categoryTextViewList.add(category5);
                Log.d("Stats",categoryList+"");
                getActivity().runOnUiThread(()->{
                    //Set Category Data
                    if(categoryList.size()>0){
                        categoryChartView.setVisibility(View.VISIBLE);
                    }else{
                        categoryChartView.setVisibility(View.GONE);
                    }
                    setCategoryData(categoryList,totalSpends);
                    int i = 0;
                    for(String key:categoryList.keySet()){
                        if(i<5) {
                            categoryTextViewList.get(i).setText("#"+(i+1)+"  "+key+"  - " + SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(categoryList.get(key)));
                        }else{
                            break;
                        }
                        i++;
                    }
                });
            });

            //Sum of all groups
            mExecutor.execute(()->{
                if(groupList.size()>0){
                    groupList.clear();
                }
                groupList = expenseDB.getSumByGroup();
                Log.d("Stats",groupList+"");
                getActivity().runOnUiThread(()->{
                    //Set Group Data
                    if(groupList.size()>0){
                        groupChartView.setVisibility(View.VISIBLE);
                    }else{
                        groupChartView.setVisibility(View.GONE);
                    }
                    setGroupData(groupList,totalSpends);
                });
            });

            //Sum of all payments
            mExecutor.execute(()->{
                if(paymentList.size()>0){
                    paymentList.clear();
                }
                paymentList = expenseDB.getSumByPayment();
                Log.d("Stats",paymentList+"");
                getActivity().runOnUiThread(()->{
                    //Set Group Data
                    if(paymentList.size()>0){
                        paymentChartView.setVisibility(View.VISIBLE);
                    }else{
                        paymentChartView.setVisibility(View.GONE);
                    }
                    setPaymentData(paymentList,totalSpends);
                });
            });

            selectdate.setOnClickListener((v) -> {
                DatePickerDialog mDatePickerDialogue = new DatePickerDialog(getContext(), datePickerDialogue, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialogue.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePickerDialogue.show();
            });

            selectmonthyear.setOnClickListener(v->{
                displayGroupDialogBox();
            });

            selectyear.setOnClickListener(v->{
                displayGroupDialogBoxForYear();
            });

            //Text Stats
            mExecutor.execute(()->{
                Date date = new Date();
                Locale locale = Locale.ENGLISH;
                String currentDayOfWeek = ExpenseDB.getDayOfWeek(date, locale);
                String currentMonth = ExpenseDB.getNameOfMonth(date,locale);
                int currentDay = ExpenseDB.getDayFromDate(date);
                weekCount = expenseDB.getCountOfDistinctWeekDaysRecords(currentDayOfWeek);
                monthCount = expenseDB.getCountOfDistinctMonthRecords(currentMonth);
                dayCount = expenseDB.getCountOfDistinctDayRecords(currentDay);
                todaysTotalExpense = expenseDB.getExpenseSumForDate(date);
                monthsTotalExpense = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
                yearsTotalExpense = expenseDB.getYearlyExpenseSum(ExpenseDB.getYearFromDate(new Date()));
                totalDistinctMonthCount = expenseDB.getCountOfAllMonthRecords();
                getActivity().runOnUiThread(()->{
                    selectdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                    selectmonthyear.setText(ExpenseDB.shortMonthTextFromIntMonth(ExpenseDB.getMonthFromDate(new Date())-1)+"-"+ExpenseDB.getYearFromDate(new Date()));
                    selectyear.setText(ExpenseDB.getYearFromDate(new Date())+"");
                    monthsExpense.setText(SplashScreenActivity.cSymbol+" "+monthsTotalExpense);
                    yearsExpense.setText(SplashScreenActivity.cSymbol+" "+yearsTotalExpense);
                    monthName.setText("Average Spending In "+currentMonth);
                    weekDayName.setText("Average Expenditure On "+currentDayOfWeek+"");
                    dayName.setText("Average Spends On Day "+currentDay+" Of Month");
                    todaysExpense.setText(SplashScreenActivity.cSymbol+" "+todaysTotalExpense);
                    if(weekCount>0 && weekList.containsKey(currentDayOfWeek)){
                        //Log.d("Stats - Week Day Avg",(weekList.get(currentDayOfWeek)/weekCount)+"");
                        currentWeekAverage.setText(SplashScreenActivity.cSymbol+" "+new DecimalFormat("00.00").format(weekList.get(currentDayOfWeek)/weekCount)+"");
                    }else{
                        // No Value Present in db
                        currentWeekAverage.setText("Not enough data to calculate");
                        currentWeekAverage.setTextSize(10f);
                    }

                    if(monthCount>0 && yearList.containsKey(currentMonth)){
                        //Log.d("Stats - Month Avg",(yearList.get(currentMonth)/monthCount)+"");
                        currentMonthAverage.setText(SplashScreenActivity.cSymbol+" "+new DecimalFormat("00.00").format(yearList.get(currentMonth)/monthCount)+"");
                    }else{
                        // No Value Present in db
                        currentMonthAverage.setText("Not enough data to calculate");
                        currentMonthAverage.setTextSize(10f);
                    }

                    if(dayCount>0 && dayList.containsKey(currentDay)){
                        //Log.d("Stats - Day Avg",(dayList.get(currentDay)/dayCount)+"");
                        currentDayAverage.setText(SplashScreenActivity.cSymbol+" "+new DecimalFormat("00.00").format(dayList.get(currentDay)/dayCount)+"");
                    }else{
                        // No Value Present in db
                        currentDayAverage.setText("Not enough data to calculate");
                        currentDayAverage.setTextSize(10f);
                    }

                    if(totalDistinctMonthCount>0){
                        Log.d("All Time Monthly Avg", (totalSpends/totalDistinctMonthCount)+"");
                        double monthlyAverageCal = (totalSpends/totalDistinctMonthCount);
                        overallMonthlyExpense.setText(SplashScreenActivity.cSymbol+" "+new DecimalFormat("00.00").format(monthlyAverageCal));
                        if(SplashScreenActivity.salary>0){
                            percentageOfMonthlySalary.setVisibility(View.VISIBLE);
                            percentageOfMonthlySalaryText.setVisibility(View.VISIBLE);
                            percentageOfMonthlySalary.setText(new DecimalFormat("00.00").format((monthlyAverageCal/SplashScreenActivity.salary*100))+"%");
                        }
                    }else{
                        // No Value Present in db
                        overallMonthlyExpense.setText("Not enough data to calculate");
                    }
                });


            });
        }




       /* lineView.setDrawDotLine(true); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView.setBottomTextList(new ArrayList<>(Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")));
        lineView.setColorArray(new int[]{getActivity().getResources().getColor(R.color.theme_red),getActivity().getResources().getColor(R.color.theme_blue),getActivity().getResources().getColor(R.color.theme_yellow),
                getActivity().getResources().getColor(R.color.theme_cyan),getActivity().getResources().getColor(R.color.theme_orange),getActivity().getResources().getColor(R.color.theme_blue_variant)});
        ArrayList<Integer> al = new ArrayList<>(Arrays.asList(44,30,56,36,75,80,40,70,42,53,44,52));
        ArrayList<Integer> al2 = new ArrayList<>(Arrays.asList(80,40,70,42,53,56,55,85,74,65,25,96));
        ArrayList<Integer> al4 = new ArrayList<>(Arrays.asList(56,55,85,74,65,74,85,89,55,54,33,45));
        ArrayList<ArrayList<Integer>>  lis = new ArrayList<>();
        lis.add(al);lis.add(al2);//lis.add(al3);
        lis.add(al4);//lis.add(al5);
        lineView.setDataList(lis); //or lineView.setFloatDataList(floatDataLists)*/

        return view;
    }

    DatePickerDialog.OnDateSetListener datePickerDialogue = (view1, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    void updateLabel(){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        selectdate.setText(sdf.format(myCalendar.getTime()));
        todaysTotalExpense = expenseDB.getExpenseSumForDate(myCalendar.getTime());
        todaysExpense.setText(SplashScreenActivity.cSymbol+" "+todaysTotalExpense);
    }

    private ArrayList<BarData> setWeeklyChart(HashMap<String,Double> weekList,double totalSpends){
        ArrayList<BarData> dataList = new ArrayList<>();
        BarData data = new BarData("Sun", (float) (((weekList.containsKey("Sunday")?weekList.get("Sunday"):0.0)/totalSpends)*100), "3.4€");
        dataList.add(data);
        data = new BarData("Mon", (float) (((weekList.containsKey("Monday")?weekList.get("Monday"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new BarData("Tue", (float) (((weekList.containsKey("Tuesday")?weekList.get("Tuesday"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new BarData("Wed", (float) (((weekList.containsKey("Wednesday")?weekList.get("Wednesday"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new BarData("Thu", (float) (((weekList.containsKey("Thursday")?weekList.get("Thursday"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new BarData("Fri", (float) (((weekList.containsKey("Friday")?weekList.get("Friday"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new BarData("Sat", (float) (((weekList.containsKey("Saturday")?weekList.get("Saturday"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        return dataList;
    }

    private ArrayList<BarData> setYearlyChart(HashMap<String,Double> yearList,double totalSpends){
        ArrayList<BarData> dataList = new ArrayList<>();
        BarData data = new BarData("JAN", (float) (((yearList.containsKey("January")?yearList.get("January"):0.0)/totalSpends)*100), "3.4€");
        dataList.add(data);
        data = new BarData("FEB", (float) (((yearList.containsKey("February")?yearList.get("February"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new BarData("MAR", (float) (((yearList.containsKey("March")?yearList.get("March"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new BarData("APR", (float) (((yearList.containsKey("April")?yearList.get("April"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new BarData("MAY", (float) (((yearList.containsKey("May")?yearList.get("May"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new BarData("JUN", (float) (((yearList.containsKey("June")?yearList.get("June"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new BarData("JUL", (float) (((yearList.containsKey("July")?yearList.get("July"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        data = new BarData("AUG", (float) (((yearList.containsKey("August")?yearList.get("August"):0.0)/totalSpends)*100), "8€");
        dataList.add(data);
        data = new BarData("SEP", (float) (((yearList.containsKey("September")?yearList.get("September"):0.0)/totalSpends)*100), "1.8€");
        dataList.add(data);
        data = new BarData("OCT", (float) (((yearList.containsKey("October")?yearList.get("October"):0.0)/totalSpends)*100), "7.3€");
        dataList.add(data);
        data = new BarData("NOV", (float) (((yearList.containsKey("November")?yearList.get("November"):0.0)/totalSpends)*100), "6.2€");
        dataList.add(data);
        data = new BarData("DEC", (float) (((yearList.containsKey("December")?yearList.get("December"):0.0)/totalSpends)*100), "3.3€");
        dataList.add(data);
        return dataList;
    }

    private ArrayList<BarData> setDailyChart(HashMap<Integer,Double> dayList,double totalSpends){
        ArrayList<BarData> dataList = new ArrayList<>();
        for(int i=1;i<=31;i++){
            BarData data = new BarData(i+"", (float) (((dayList.containsKey(i)?dayList.get(i):0.0)/totalSpends)*100), "0.0");
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

    private void setPaymentData(HashMap<String,Double> paymentList,double totalSpends) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for(String key : paymentList.keySet()){
            entries.add(new PieEntry((float) ((paymentList.get(key)/totalSpends)*100),
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
        pieChartPayment.setUsePercentValues(true);
        pieChartPayment.getDescription().setEnabled(false);
        pieChartPayment.setExtraOffsets(5, 10, 5, 5);
        pieChartPayment.setDragDecelerationFrictionCoef(0.95f);
        pieChartPayment.setDrawHoleEnabled(true);
        pieChartPayment.setHoleColor(Color.WHITE);
        pieChartPayment.setTransparentCircleColor(Color.WHITE);
        pieChartPayment.setTransparentCircleAlpha(110);
        pieChartPayment.setHoleRadius(44f);
        pieChartPayment.setTransparentCircleRadius(48f);
        pieChartPayment.setDrawCenterText(true);
        pieChartPayment.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChartPayment.setRotationEnabled(false);
        pieChartPayment.setHighlightPerTapEnabled(true);
        pieChartPayment.animateY(500, Easing.EaseInOutQuad);
        Legend l = pieChartPayment.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(2f);
        l.setYOffset(3f);
        pieChartPayment.setData(data);
        pieChartPayment.setEntryLabelColor(Color.WHITE);
        pieChartPayment.setEntryLabelTextSize(11f);
        // undo all highlights
        //chart.highlightValues(null);
        pieChartPayment.setDrawEntryLabels(false);
        pieChartPayment.invalidate();
    }

    public void displayGroupDialogBox(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                        selectmonthyear.setText(ExpenseDB.shortMonthTextFromIntMonth(selectedMonth) +"-"+ selectedYear);
                        monthsTotalExpense = expenseDB.getMonthlyExpenseSum(selectedMonth+1,selectedYear);
                        monthsExpense.setText(SplashScreenActivity.cSymbol+" "+monthsTotalExpense);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH));
        builder.setTitle("Select Month-Year")
                //.setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .setYearRange(2010, myCalendar.get(Calendar.YEAR))
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                //.showMonthOnly()
                // .showYearOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) { /* on month selected*/ } })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) { /* on year selected*/ } })
                .build().show();
    }

    public void displayGroupDialogBoxForYear(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                        selectyear.setText(selectedYear+"");
                        yearsTotalExpense = expenseDB.getYearlyExpenseSum(selectedYear);
                        yearsExpense.setText(SplashScreenActivity.cSymbol+" "+yearsTotalExpense);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH));
        builder.setTitle("Select Year")
                //.setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .setYearRange(2010, myCalendar.get(Calendar.YEAR))
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                //.showMonthOnly()
                .showYearOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) { /* on month selected*/ } })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) { /* on year selected*/ } })
                .build().show();
    }
}
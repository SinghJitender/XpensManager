package com.example.xpensmanager.ExpenseScreen.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyViewFragment extends Fragment {
    private ExpenseDB expenseDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private ViewType viewType;
    private ArrayList<ExpenseData> expenseData;
    private Calendar today;
    private TextView currentMonthName,currentMonthTotalSpends,limit;
    private TimeLineRecyclerView recyclerView;
    private ExpenseViewAdapter adapter;
    private ProgressBar progressBar;
    private CategoryDB categoryDB;
    private GroupDB groupDB;
    private double totalCategorySum, categoryLimit, groupLimit;

    public MonthlyViewFragment() {
        // Required empty public constructor
    }

    public static MonthlyViewFragment newInstance(String param1, String param2) {
        MonthlyViewFragment fragment = new MonthlyViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AGRS", getArguments()+"");
        if (getArguments() != null) {
            groupBy = getArguments().getString("groupBy");
            filterValue = getArguments().getInt("filterValue");
            filterType = getArguments().getString("filterType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monthly_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        currentMonthName = view.findViewById(R.id.currentMonthName);
        currentMonthTotalSpends = view.findViewById(R.id.currentMonthTotalSpends);
        progressBar = view.findViewById(R.id.progress);
        limit = view.findViewById(R.id.limit);

        expenseDB = new ExpenseDB(getActivity());
        categoryDB = new CategoryDB(getActivity());
        groupDB = new GroupDB(getActivity());
        totalCategorySum = categoryDB.getTotalCategoryLimitSum();

        today = Calendar.getInstance();
        currentMonthName.setText(ExpenseDB.getNameOfMonth(new Date(), Locale.ENGLISH) +"-"+ today.get(Calendar.YEAR));

        currentMonthName.setOnClickListener(v -> {
            displayGroupDialogBox();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));

        if(groupBy.equalsIgnoreCase("None")) {
            expenseData = expenseDB.findByMonth(filterValue, today.get(Calendar.YEAR));
            double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
            currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
            progressBar.setProgress((int) ((totalSpendsThisMonth / totalCategorySum) * 100));
            limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(totalCategorySum));
        }
        else{
            if(filterType.equalsIgnoreCase("category")) {
                expenseData = expenseDB.findByMonthAndCategory(groupBy,filterValue,today.get(Calendar.YEAR));
                double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByCategory(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                categoryLimit = categoryDB.getCategoryLimitByTitle(groupBy);
                progressBar.setProgress((int) ((totalSpendsThisMonth / categoryLimit) * 100));
                limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(categoryLimit));
            }else{
                expenseData = expenseDB.findByMonthAndGroup(groupBy,filterValue,today.get(Calendar.YEAR));
                double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByGroup(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                groupLimit = groupDB.getGroupLimitByTitle(groupBy);
                progressBar.setProgress((int) ((totalSpendsThisMonth / groupLimit) * 100));
                limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(groupLimit));
            }
        }

        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.MONTHLY);
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(adapter);
        return view;
    }
    private SectionCallback getSectionCallback(final List<ExpenseData> d) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(d.get(position).getDate(), "", AppCompatResources.getDrawable(getActivity(), R.drawable.dot_yellow));
            }

            @Override
            public boolean isSection(int position) {
                return !d.get(position).getDate().equals(d.get(position - 1).getDate());
            }
        };
    }

    public void displayGroupDialogBox(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                        currentMonthName.setText(ExpenseDB.monthTextFromIntMonth(selectedMonth) +"-"+ selectedYear);
                        expenseData.clear();
                            if (groupBy.equalsIgnoreCase("None")) {
                                expenseData.addAll(expenseDB.findByMonth(selectedMonth + 1, selectedYear));
                                double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSum(selectedMonth+1,selectedYear);
                                currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                                progressBar.setProgress((int) ((totalSpendsThisMonth / totalCategorySum) * 100));
                                limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(totalCategorySum));
                            } else {
                                if (filterType.equalsIgnoreCase("category")) {
                                    expenseData.addAll(expenseDB.findByMonthAndCategory(groupBy, selectedMonth + 1, selectedYear));
                                    double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByCategory(selectedMonth+1,selectedYear,groupBy);
                                    currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisMonth);
                                    progressBar.setProgress((int) ((totalSpendsThisMonth / categoryLimit) * 100));
                                    limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(categoryLimit));
                                } else {
                                    expenseData.addAll(expenseDB.findByMonthAndGroup(groupBy, selectedMonth + 1, selectedYear));
                                    double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByGroup(selectedMonth+1,selectedYear,groupBy);
                                    currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                                    progressBar.setProgress((int) ((totalSpendsThisMonth / groupLimit) * 100));
                                    limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(groupLimit));
                                }
                            }
                            adapter.notifyDataSetChanged();
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setTitle("View Expense For Month-Year")
                //.setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                 .setYearRange(1890, today.get(Calendar.YEAR))
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

}
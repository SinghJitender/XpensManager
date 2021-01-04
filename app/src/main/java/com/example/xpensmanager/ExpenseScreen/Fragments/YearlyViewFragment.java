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
import android.widget.TextView;

import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YearlyViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearlyViewFragment extends Fragment {
    private ExpenseDB expenseDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private ArrayList<ExpenseData> expenseData;
    private TimeLineRecyclerView recyclerView;
    private Calendar today;
    private TextView currentYearName,currentYearTotalSpends;
    private ExpenseViewAdapter adapter;

    public YearlyViewFragment() {
        // Required empty public constructor
    }

    public static YearlyViewFragment newInstance(String param1, String param2) {
        YearlyViewFragment fragment = new YearlyViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_yearly_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        currentYearName = view.findViewById(R.id.currentYearName);
        currentYearTotalSpends = view.findViewById(R.id.currentYearTotalSpends);

        expenseDB = new ExpenseDB(getActivity());

        today = Calendar.getInstance();
        currentYearName.setText(today.get(Calendar.YEAR)+"");

        currentYearName.setOnClickListener(v -> {
            displayGroupDialogBox();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));

        if(groupBy.equalsIgnoreCase("None")) {
            expenseData = expenseDB.findByYear(filterValue);
            double totalSpendsThisYear = expenseDB.getYearlyExpenseSum(ExpenseDB.getYearFromDate(new Date()));
            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisYear);
        }
        else{
            if(filterType.equalsIgnoreCase("category")) {
                expenseData = expenseDB.findByYearAndCategory(groupBy,filterValue);
                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByCategory(ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisYear);
            }else{
                expenseData = expenseDB.findByYearAndGroup(groupBy,filterValue);
                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByGroup(ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisYear);
            }
        }

        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.YEARLY);
        recyclerView.addItemDecoration(getSectionCallback(expenseData,groupBy));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData, String groupBy) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getTextMonth(), "", AppCompatResources.getDrawable(getActivity(), R.drawable.dot_yellow));
            }

            @Override
            public boolean isSection(int position) {
                return !(expenseData.get(position).getMonth()  == expenseData.get(position - 1).getMonth());
            }
        };
    }

    public void displayGroupDialogBox(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) { // on date set
                        currentYearName.setText(""+selectedYear);
                        expenseData.clear();
                        if (groupBy.equalsIgnoreCase("None")) {
                            expenseData.addAll(expenseDB.findByYear(selectedYear));
                            double totalSpendsThisYear = expenseDB.getYearlyExpenseSum(selectedYear);
                            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisYear);
                        } else {
                            if (filterType.equalsIgnoreCase("category")) {
                                expenseData.addAll(expenseDB.findByYearAndCategory(groupBy, selectedYear));
                                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByCategory(selectedYear,groupBy);
                                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisYear);
                            } else {
                                expenseData.addAll(expenseDB.findByYearAndGroup(groupBy, selectedYear));
                                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByGroup(selectedYear,groupBy);
                                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpendsThisYear);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setTitle("View Expense For Year")
                //.setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .setYearRange(1890, today.get(Calendar.YEAR))
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
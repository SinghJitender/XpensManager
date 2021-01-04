package com.example.xpensmanager.MainScreen.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class Home extends Fragment {

    private static ExpenseDB expenseDB;
    private static double totalSpentThisMonth, totalCategorySum;
    private ImageButton viewAllButtom;
    private static TextView currentMonthTotalSpends,limit;
    private static ProgressBar progressBar;
    private static ExpenseViewAdapter adapter;
    private static ArrayList<ExpenseData> expenseData;
    private static boolean toggle = true;
    private static boolean toggleNewExpense = true;
    private static TimeLineRecyclerView recyclerView;

    //SwipeController swipeController = null;

    private GroupDB groups;
    private static CategoryDB categoryDB;
    private ExecutorService mExecutor;

    public Home() {
    }

    private Runnable updateData = () -> {
        // Do some work
        expenseData.clear();
        expenseData.addAll(expenseDB.findByMonth(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date())));
        totalSpentThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
        totalCategorySum = categoryDB.getTotalCategoryLimitSum();
        getActivity().runOnUiThread(()->{
            adapter.notifyDataSetChanged();
            currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpentThisMonth));
            limit.setText(new DecimalFormat("00.00").format(totalSpentThisMonth)+"/"+new DecimalFormat("00.00").format(totalCategorySum));
            progressBar.setProgress((int)((totalSpentThisMonth/totalCategorySum)*100));
        });
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        expenseData = new ArrayList<>();
        categoryDB = new CategoryDB(getActivity());
        expenseDB = new ExpenseDB(getActivity());
        groups = new GroupDB(getActivity());
        mExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(ExpenseDB.getNameOfMonth(new Date(),Locale.ENGLISH));
        recyclerView = view.findViewById(R.id.recyclerView);
        viewAllButtom = view.findViewById(R.id.viewAll);
        currentMonthTotalSpends = view.findViewById(R.id.currentMonthTotalSpends);
        progressBar = view.findViewById(R.id.progress);
        limit = view.findViewById(R.id.limit);
        mExecutor.execute(updateData);

        currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpentThisMonth));
        limit.setText(new DecimalFormat("00.00").format(totalSpentThisMonth)+"/"+new DecimalFormat("00.00").format(totalCategorySum));
        progressBar.setProgress((int)((totalSpentThisMonth/totalCategorySum)*100));

        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.MONTHLY);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(adapter);

        viewAllButtom.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), Expense.class);
            intent.putExtra("groupBy","None");
            intent.putExtra("filterType","None");
            startActivity(intent);
        });

        return view;
    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getDate(),"", AppCompatResources.getDrawable(getActivity(), R.drawable.dot_yellow));
            }

            @Override
            public boolean isSection(int position) {
                return !expenseData.get(position).getDate().equals(expenseData.get(position - 1).getDate());
            }
        };
    }

    public static void updateRecyclerView(ArrayList<ExpenseData> data,double updatedTotalSpentThisMonth,double updatedTotalCategorySum){
        expenseData.clear();
        expenseData.addAll(data);
        adapter.notifyDataSetChanged();
        currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(updatedTotalSpentThisMonth));
        limit.setText(new DecimalFormat("00.00").format(updatedTotalSpentThisMonth)+"/"+new DecimalFormat("00.00").format(updatedTotalCategorySum));
        progressBar.setProgress((int)((updatedTotalSpentThisMonth/updatedTotalCategorySum)*100));
    }

}
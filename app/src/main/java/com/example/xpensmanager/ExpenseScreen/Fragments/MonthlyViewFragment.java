package com.example.xpensmanager.ExpenseScreen.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.MainScreen.Adapters.SwipeToDeleteCallback;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.snackbar.Snackbar;
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
    private CategoryDB categoryDB;
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
    private GroupDB groupDB;
    private double totalCategorySum, categoryLimit, groupLimit;
    public static RelativeLayout emptyView;
    private CoordinatorLayout framelayout;
    private FrameLayout monthlyContainer;

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
        emptyView = view.findViewById(R.id.emptyView);
        framelayout = view.findViewById(R.id.frameLayout);
        monthlyContainer = view.findViewById(R.id.monthly_container);

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
        if(expenseData.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.MONTHLY);
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();
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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final ExpenseData item = adapter.getData().get(position);
                Log.d("Snackbar",item.toString());
                adapter.removeItem(position);
                if(adapter.getData().size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
                Snackbar snackbar = Snackbar
                        .make(monthlyContainer, "Item was removed from the list",10000)
                        .addCallback(new Snackbar.Callback(){

                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                                //Toast.makeText(getActivity(),"Snack bar shown",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE ||
                                        event == Snackbar.Callback.DISMISS_EVENT_MANUAL || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                                    // Snackbar closed on its own or swiped to close
                                    double netAmount = groupDB.getNetAmountByTitle(item.getGroup());
                                    double totalAmount = groupDB.getTotalAmountByTitle(item.getGroup());
                                    double totalCategoryAmount = categoryDB.getTotalAmountByTitle(item.getCategory());
                                    totalAmount = totalAmount - item.getAmount();
                                    if(item.getPaidBy().equalsIgnoreCase("Me")) {
                                        netAmount = netAmount - (item.getAmount()-item.getSplitAmount());
                                    }else{
                                        netAmount = netAmount + item.getSplitAmount();
                                    }
                                    groupDB.updateGroupAmountByTitle(item.getGroup(),netAmount,totalAmount);
                                    categoryDB.updateCategoryAmountByTitle(item.getCategory(),(totalCategoryAmount-item.getSplitAmount()));
                                    expenseDB.deleteExpenseById(item.getId());
                                    if(groupBy.equalsIgnoreCase("None")) {
                                        double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
                                        currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                                        progressBar.setProgress((int) ((totalSpendsThisMonth / totalCategorySum) * 100));
                                        limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(totalCategorySum));
                                    }
                                    else{
                                        if(filterType.equalsIgnoreCase("category")) {
                                            double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByCategory(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()),groupBy);
                                            currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                                            categoryLimit = categoryDB.getCategoryLimitByTitle(groupBy);
                                            progressBar.setProgress((int) ((totalSpendsThisMonth / categoryLimit) * 100));
                                            limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(categoryLimit));
                                        }else{
                                            double totalSpendsThisMonth = expenseDB.getMonthlyExpenseSumByGroup(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()),groupBy);
                                            currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ totalSpendsThisMonth);
                                            groupLimit = groupDB.getGroupLimitByTitle(groupBy);
                                            progressBar.setProgress((int) ((totalSpendsThisMonth / groupLimit) * 100));
                                            limit.setText(new DecimalFormat("00.00").format(totalSpendsThisMonth) + "/" + new DecimalFormat("00.00").format(groupLimit));
                                        }
                                    }
                                }
                            }
                        });
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                        if(emptyView.getVisibility() == View.VISIBLE) {
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                });
                snackbar.setActionTextColor(getActivity().getResources().getColor(R.color.theme_yellow));
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
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
                            if(expenseData.size() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                            }else{
                                emptyView.setVisibility(View.GONE);
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
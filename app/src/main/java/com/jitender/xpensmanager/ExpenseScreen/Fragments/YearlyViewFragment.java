package com.jitender.xpensmanager.ExpenseScreen.Fragments;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.ExpenseDB;
import com.jitender.xpensmanager.Database.GroupDB;
import com.jitender.xpensmanager.Database.PaymentsDB;
import com.jitender.xpensmanager.Enums.ViewType;
import com.jitender.xpensmanager.Database.ExpenseData;
import com.jitender.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.jitender.xpensmanager.MainScreen.Adapters.SwipeToDeleteCallback;
import com.jitender.xpensmanager.MainScreen.Adapters.SwipeToSettleCallback;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.snackbar.Snackbar;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

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
    private CategoryDB categoryDB;
    private GroupDB groupsDB;
    private PaymentsDB paymentsDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private ArrayList<ExpenseData> expenseData;
    private TimeLineRecyclerView recyclerView;
    private Calendar today;
    private TextView currentYearName,currentYearTotalSpends;
    private ExpenseViewAdapter adapter;
    public static RelativeLayout emptyView;
    private CoordinatorLayout framelayout;
    private FrameLayout yearlyContainer;

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
        emptyView = view.findViewById(R.id.emptyView);
        framelayout = view.findViewById(R.id.frameLayout);
        yearlyContainer = view.findViewById(R.id.yearly_container);

        expenseDB = new ExpenseDB(getActivity());
        groupsDB = new GroupDB(getActivity());
        categoryDB = new CategoryDB(getActivity());
        paymentsDB = new PaymentsDB(getActivity());

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
            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpendsThisYear));
        }
        else{
            if(filterType.equalsIgnoreCase("category")) {
                expenseData = expenseDB.findByYearAndCategory(groupBy,filterValue);
                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByCategory(ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
            }else if(filterType.equalsIgnoreCase("payment")) {
                expenseData = expenseDB.findByYearAndPayment(groupBy,filterValue);
                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByPayment(ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
            }else{
                expenseData = expenseDB.findByYearAndGroup(groupBy,filterValue);
                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByGroup(ExpenseDB.getYearFromDate(new Date()),groupBy);
                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
            }
        }
        if(expenseData.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.YEARLY);
        recyclerView.addItemDecoration(getSectionCallback(expenseData,groupBy));
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();
        return view;
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
                        .make(yearlyContainer, "Item was removed from the list",10000)
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
                                    double totalCategoryAmount = categoryDB.getTotalAmountByTitle(item.getCategory());
                                    double totalPaymentAmount = paymentsDB.getTotalAmountByMode(item.getModeOfPayment());
                                    Executors.newSingleThreadExecutor().execute(()->{
                                        double totalSettleAmount = expenseDB.getExpenseSettledAmountByGroup(item.getGroup());
                                        double groupTotalAmount = expenseDB.getAllExpenseTotalSumByGroup(item.getGroup());
                                        groupsDB.updateGroupAmountByTitle(item.getGroup(),totalSettleAmount,groupTotalAmount);
                                        categoryDB.updateCategoryAmountByTitle(item.getCategory(),(totalCategoryAmount-item.getSplitAmount()));
                                        paymentsDB.updatePaymentAmountByMode(item.getModeOfPayment(),(totalPaymentAmount - item.getSplitAmount()));
                                    });
                                    expenseDB.deleteExpenseById(item.getId());
                                    if(groupBy.equalsIgnoreCase("None")) {
                                        double totalSpendsThisYear = expenseDB.getYearlyExpenseSum(ExpenseDB.getYearFromDate(new Date()));
                                        currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpendsThisYear));
                                    }
                                    else{
                                        if(filterType.equalsIgnoreCase("category")) {
                                            double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByCategory(ExpenseDB.getYearFromDate(new Date()),groupBy);
                                            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                                        }else if(filterType.equalsIgnoreCase("payment")) {
                                            double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByPayment(ExpenseDB.getYearFromDate(new Date()),groupBy);
                                            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                                        }else{
                                            double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByGroup(ExpenseDB.getYearFromDate(new Date()),groupBy);
                                            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
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
        SwipeToSettleCallback swipeToSettleCallback = new SwipeToSettleCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                Log.d("Item Position",position+"");
                final ExpenseData item = adapter.getData().get(position);
                final String isSettled = item.getSettled();
                String text; boolean setUndo = true;
                if(item.getSettled().equalsIgnoreCase("true")){
                    text = "Expense already settled";
                    setUndo = false;
                }else{
                    adapter.getData().get(position).setSettled("true");
                    text = "Expense settled";
                }
                adapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar
                        .make(yearlyContainer, text,5000)
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
                                    Log.d("Settling Expense","Amount : "+item.getSettledAmount() + " ID : "+ item.getId());
                                    if(isSettled.equalsIgnoreCase("false")) {
                                        Log.d("Settled Expense",item.getSettledAmount() + " Settled");
                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            expenseDB.updateExpenseSettled(item.getId(), "true");
                                            double totalSettleAmount = expenseDB.getExpenseSettledAmountByGroup(item.getGroup());
                                            groupsDB.updateGroupNetAmountByTitle(item.getGroup(), totalSettleAmount);
                                        });
                                    }
                                }
                            }
                        });
                if(setUndo) {
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.getData().get(position).setSettled("false");
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                snackbar.setActionTextColor(getActivity().getResources().getColor(R.color.theme_yellow));
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchhelper2 = new ItemTouchHelper(swipeToSettleCallback);
        itemTouchhelper2.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
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
                            currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                        } else {
                            if (filterType.equalsIgnoreCase("category")) {
                                expenseData.addAll(expenseDB.findByYearAndCategory(groupBy, selectedYear));
                                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByCategory(selectedYear,groupBy);
                                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                            }else if(filterType.equalsIgnoreCase("payment")) {
                                expenseData.addAll(expenseDB.findByYearAndPayment(groupBy,selectedYear));
                                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByPayment(selectedYear,groupBy);
                                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                            } else {
                                expenseData.addAll(expenseDB.findByYearAndGroup(groupBy, selectedYear));
                                double totalSpendsThisYear = expenseDB.getYearlyExpenseSumByGroup(selectedYear,groupBy);
                                currentYearTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+new DecimalFormat("00.00").format(totalSpendsThisYear));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if(expenseData.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        }else{
                            emptyView.setVisibility(View.GONE);
                        }
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
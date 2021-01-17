package com.example.xpensmanager.MainScreen.Fragments;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.Adapters.SwipeToDeleteCallback;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.snackbar.Snackbar;

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
    public static RelativeLayout emptyView;
    private CoordinatorLayout framelayout;
    public static boolean homeInitializationFlag;

    //SwipeController swipeController = null;

    private GroupDB groupsDB;
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
            if(expenseData.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.GONE);
            }
        });
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        expenseData = new ArrayList<>();
        categoryDB = new CategoryDB(getActivity());
        expenseDB = new ExpenseDB(getActivity());
        groupsDB = new GroupDB(getActivity());
        mExecutor = Executors.newSingleThreadExecutor();
        homeInitializationFlag = true;
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
        emptyView = view.findViewById(R.id.emptyView);
        framelayout = view.findViewById(R.id.frameLayout);

        currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpentThisMonth));
        limit.setText(new DecimalFormat("00.00").format(totalSpentThisMonth)+"/"+new DecimalFormat("00.00").format(totalCategorySum));
        progressBar.setProgress((int)((totalSpentThisMonth/totalCategorySum)*100));
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        //itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.MONTHLY);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(adapter);
        enableSwipeToDeleteAndUndo();
        if(expenseData.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
        viewAllButtom.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), Expense.class);
            intent.putExtra("groupBy","None");
            intent.putExtra("filterType","None");
            startActivity(intent);
        });

        return view;
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(getActivity(), "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Toast.makeText(getActivity(), "on Swiped ", Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            expenseData.remove(position);
            adapter.notifyDataSetChanged();
            if(expenseData.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.GONE);
            }
        }
    };

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
                        .make(framelayout, "Item was removed from the list",10000)
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
                                    double netAmount = groupsDB.getNetAmountByTitle(item.getGroup());
                                    double totalAmount = groupsDB.getTotalAmountByTitle(item.getGroup());
                                    double totalCategoryAmount = categoryDB.getTotalAmountByTitle(item.getCategory());
                                    totalAmount = totalAmount - item.getAmount();
                                    if(item.getPaidBy().equalsIgnoreCase("Me")) {
                                        netAmount = netAmount - (item.getAmount()-item.getSplitAmount());
                                    }else{
                                        netAmount = netAmount + item.getSplitAmount();
                                    }
                                    groupsDB.updateGroupAmountByTitle(item.getGroup(),netAmount,totalAmount);
                                    categoryDB.updateCategoryAmountByTitle(item.getCategory(),(totalCategoryAmount-item.getSplitAmount()));
                                    expenseDB.deleteExpenseById(item.getId());
                                    totalSpentThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
                                    currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(totalSpentThisMonth));
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

    public static void updateRecyclerView(ArrayList<ExpenseData> data,double updatedTotalSpentThisMonth,double updatedTotalCategorySum){
        expenseData.clear();
        expenseData.addAll(data);
        adapter.notifyDataSetChanged();
        currentMonthTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(updatedTotalSpentThisMonth));
        limit.setText(new DecimalFormat("00.00").format(updatedTotalSpentThisMonth)+"/"+new DecimalFormat("00.00").format(updatedTotalCategorySum));
        progressBar.setProgress((int)((updatedTotalSpentThisMonth/updatedTotalCategorySum)*100));
        if(data.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
    }

}
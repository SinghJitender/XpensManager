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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.ExpenseDB;
import com.jitender.xpensmanager.Database.GroupDB;
import com.jitender.xpensmanager.Enums.ViewType;
import com.jitender.xpensmanager.Database.ExpenseData;
import com.jitender.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.jitender.xpensmanager.MainScreen.Adapters.SwipeToDeleteCallback;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllViewFragment extends Fragment {
    private CategoryDB categoryDB;
    private GroupDB groupsDB;
    private ExpenseDB expenseDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private TextView allTimeTotalSpends;
    private ArrayList<ExpenseData> expenseData;
    public static RelativeLayout emptyView;
    private CoordinatorLayout framelayout;
    private ExpenseViewAdapter adapter;
    private TimeLineRecyclerView recyclerView;

    public AllViewFragment() {
        // Required empty public constructor
    }

    public static AllViewFragment newInstance(String param1, String param2) {
        AllViewFragment fragment = new AllViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupBy = getArguments().getString("groupBy");
            filterType = getArguments().getString("filterType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        allTimeTotalSpends = view.findViewById(R.id.allTimeTotalSpends);
        emptyView = view.findViewById(R.id.emptyView);
        framelayout = view.findViewById(R.id.frameLayout);

        expenseDB = new ExpenseDB(getActivity());
        groupsDB = new GroupDB(getActivity());
        categoryDB = new CategoryDB(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));

        if(groupBy.equalsIgnoreCase("None")) {
            expenseData = expenseDB.findAll();
            double totalSpends = expenseDB.getAllExpenseSum();
            allTimeTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpends);
        }
        else{
            if(filterType.equalsIgnoreCase("category")) {
                expenseData = expenseDB.findAllByCategory(groupBy);
                double totalSpends = expenseDB.getAllExpenseSumByCategory(groupBy);
                allTimeTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpends);
            }else{
                expenseData = expenseDB.findAllByGroup(groupBy);
                double totalSpends = expenseDB.getAllExpenseSumByGroup(groupBy);
                allTimeTotalSpends.setText(SplashScreenActivity.cSymbol+ " "+totalSpends);
            }
        }
        if(expenseData.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
        adapter = new ExpenseViewAdapter(expenseData,getActivity(), ViewType.ALL);
        recyclerView.addItemDecoration(getSectionCallback(expenseData,groupBy));
        recyclerView.setAdapter(adapter);
        //enableSwipeToDeleteAndUndo();
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


    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData, String groupBy) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getYear()+"", "", AppCompatResources.getDrawable(getActivity(), R.drawable.dot_yellow));
            }

            @Override
            public boolean isSection(int position) {
                return !(expenseData.get(position).getYear() == expenseData.get(position - 1).getYear());
            }
        };
    }

}
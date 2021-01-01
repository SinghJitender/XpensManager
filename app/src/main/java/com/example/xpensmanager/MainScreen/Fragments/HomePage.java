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
import android.widget.TextView;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.Adapters.SwipeController;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class HomePage extends Fragment {

    private ImageButton viewAllButtom;
    private TextView currentMonthName,currentMonthTotalSpends,currentMonthNetAmount;

    ArrayList<Hashtable<String,String>> results;
    private double totalSpentThisMonth;

    private static boolean toggle = true;
    private static boolean toggleNewExpense = true;

    SwipeController swipeController = null;

    GroupDB groups;
    GenericExpenseDB genericExpenseDB;

    public HomePage() {
    }

    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(GenericExpenseDB.getDayOfMonth(new Date(),Locale.ENGLISH));
        TimeLineRecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        viewAllButtom = view.findViewById(R.id.viewAll);
        currentMonthName = view.findViewById(R.id.currentMonthName);
        currentMonthTotalSpends = view.findViewById(R.id.currentMonthTotalSpends);
        currentMonthNetAmount = view.findViewById(R.id.currentMonthNetAmount);

        //Add New Expense Widgets

        groups = new GroupDB(getActivity());
        results = new ArrayList<>();
        results.addAll(groups.findAll());

        totalSpentThisMonth = new GenericExpenseDB(getActivity()).getMonthlyExpenseSum(GenericExpenseDB.getMonthFromDate(new Date()),GenericExpenseDB.getYearFromDate(new Date()));
        //currentMonthName.setText(GenericExpenseDB.getDayOfMonth(new Date(),Locale.ENGLISH));
        currentMonthTotalSpends.setText("₹ "+ new DecimalFormat("00.00").format(totalSpentThisMonth));
        //currentMonthNetAmount.setText("");
        viewAllButtom.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity(), Expense.class);
            intent.putExtra("groupBy","None");
            intent.putExtra("filterType","None");
            startActivity(intent);
        });

        genericExpenseDB = new GenericExpenseDB(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));

        ArrayList<ExpenseData> expenseData = genericExpenseDB.findByMonth(GenericExpenseDB.getMonthFromDate(new Date()));
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(new ExpenseViewAdapter(expenseData,getActivity(), ViewType.MONTHLY));

        /*ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.theme_light_grey));
            private boolean swipeBack = false;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
               *//* if (direction == ItemTouchHelper.LEFT)
                    adapter.showMenu(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.RIGHT)
                    adapter.hideMenu(viewHolder.getAdapterPosition());*//*
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                background.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/

        return view;
    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getDate(),"", AppCompatResources.getDrawable(getActivity(), R.drawable.dot));
            }

            @Override
            public boolean isSection(int position) {
                return !expenseData.get(position).getDate().equals(expenseData.get(position - 1).getDate());
            }
        };
    }

}
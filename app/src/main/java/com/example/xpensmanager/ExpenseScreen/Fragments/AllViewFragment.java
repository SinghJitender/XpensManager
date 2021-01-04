package com.example.xpensmanager.ExpenseScreen.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ExpenseDB expenseDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private TextView allTimeTotalSpends;
    private ArrayList<ExpenseData> expenseData;

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
        TimeLineRecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        allTimeTotalSpends = view.findViewById(R.id.allTimeTotalSpends);

        expenseDB = new ExpenseDB(getActivity());

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


        recyclerView.addItemDecoration(getSectionCallback(expenseData,groupBy));
        recyclerView.setAdapter(new ExpenseViewAdapter(expenseData,getActivity(), ViewType.ALL));
        return view;
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
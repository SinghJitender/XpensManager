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

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
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
    private GenericExpenseDB genericExpenseDB;
    private String tableName,groupBy,filterType;
    private int filterValue;
    private ArrayList<ExpenseData> expenseData;

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
        TimeLineRecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        genericExpenseDB = new GenericExpenseDB(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));

        if(groupBy.equalsIgnoreCase("None"))
            expenseData = genericExpenseDB.findByYear(filterValue);
        else{
            if(filterType.equalsIgnoreCase("category")) {
                expenseData = genericExpenseDB.findByYearAndCategory(groupBy,filterValue);
            }else{
                expenseData = genericExpenseDB.findByYearAndGroup(groupBy,filterValue);
            }
        }


        recyclerView.addItemDecoration(getSectionCallback(expenseData,groupBy));
        recyclerView.setAdapter(new ExpenseViewAdapter(expenseData,getActivity(), ViewType.YEARLY));
        return view;
    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData, String groupBy) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getTextMonth(), "", AppCompatResources.getDrawable(getActivity(), R.drawable.check));
            }

            @Override
            public boolean isSection(int position) {
                return !(expenseData.get(position).getMonth()  == expenseData.get(position - 1).getMonth());
            }
        };
    }

}
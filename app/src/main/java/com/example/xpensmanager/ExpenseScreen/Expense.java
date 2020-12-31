package com.example.xpensmanager.ExpenseScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseData;
import com.example.xpensmanager.ExpenseScreen.Adapters.ExpenseViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
import java.util.List;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class Expense extends AppCompatActivity {
    private GenericExpenseDB genericExpenseDB;
    private String tableName,groupBy;
    private int filterValue;
    private ViewType viewType;
    private ArrayList<ExpenseData> expenseData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        TimeLineRecyclerView recyclerView = findViewById(R.id.recycler_view);
        viewType = (ViewType) getIntent().getExtras().getSerializable("viewType");
        groupBy = getIntent().getExtras().getString("groupBy");
        filterValue = getIntent().getExtras().getInt("filterValue");

        genericExpenseDB = new GenericExpenseDB(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        if(viewType == ViewType.MONTHLY) {
            if(groupBy.equalsIgnoreCase("None"))
                expenseData = genericExpenseDB.findByMonth(filterValue);
            else
                expenseData = genericExpenseDB.findByMonthAndGroup(groupBy,filterValue);
        }
        else if(viewType == ViewType.YEARLY){
            if(groupBy.equalsIgnoreCase("None"))
                expenseData = genericExpenseDB.findByYear(filterValue);
            else
                expenseData = genericExpenseDB.findByYearAndGroup(groupBy,filterValue);
        }
        else{
            if(groupBy.equalsIgnoreCase("None"))
                expenseData = genericExpenseDB.findAll();
            else
                expenseData = genericExpenseDB.findAllByGroup(groupBy);
        }

        recyclerView.addItemDecoration(getSectionCallback(expenseData,viewType,groupBy));
        recyclerView.setAdapter(new ExpenseViewAdapter(expenseData,getApplicationContext(), viewType));

    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData, ViewType viewType, String groupBy) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                if(viewType == ViewType.MONTHLY){
                    return new SectionInfo(expenseData.get(position).getDate(), "", AppCompatResources.getDrawable(getApplicationContext(), R.drawable.check));
                }
                else if(viewType == ViewType.YEARLY){
                    return new SectionInfo(expenseData.get(position).getTextMonth(), "", AppCompatResources.getDrawable(getApplicationContext(), R.drawable.check));
                }
                else{
                    return new SectionInfo(expenseData.get(position).getYear()+"", "", AppCompatResources.getDrawable(getApplicationContext(), R.drawable.check));
                }

            }

            @Override
            public boolean isSection(int position) {
                if(viewType == ViewType.MONTHLY){
                    return !expenseData.get(position).getDate().equals(expenseData.get(position - 1).getDate());
                }
                else if(viewType == ViewType.YEARLY){
                    return !(expenseData.get(position).getMonth()  == expenseData.get(position - 1).getMonth());
                }
                else{
                    return !(expenseData.get(position).getYear() == expenseData.get(position - 1).getYear());
                }

            }
        };
    }
}
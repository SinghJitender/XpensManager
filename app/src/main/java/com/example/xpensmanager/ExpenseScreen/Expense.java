package com.example.xpensmanager.ExpenseScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

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
    private String tableName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        TimeLineRecyclerView recyclerView = findViewById(R.id.recycler_view);

        tableName = getIntent().getExtras().getString("tableName");
        genericExpenseDB = new GenericExpenseDB(getApplicationContext(),tableName);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        ArrayList<ExpenseData> expenseData = genericExpenseDB.findAll();
        recyclerView.addItemDecoration(getSectionCallback(expenseData));
        recyclerView.setAdapter(new ExpenseViewAdapter(expenseData,getApplicationContext(), ViewType.MONTHLY));

    }

    private SectionCallback getSectionCallback(final List<ExpenseData> expenseData) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                return new SectionInfo(expenseData.get(position).getDate(), expenseData.get(position).getTextMonth(), AppCompatResources.getDrawable(getApplicationContext(), R.drawable.check));
            }

            @Override
            public boolean isSection(int position) {
                return !expenseData.get(position).getDate().equals(expenseData.get(position - 1).getDate());
            }
        };
    }
}
package com.example.xpensmanager.ExpenseScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.ExpenseScreen.Fragments.AllViewFragment;
import com.example.xpensmanager.ExpenseScreen.Fragments.MonthlyViewFragment;
import com.example.xpensmanager.ExpenseScreen.Fragments.YearlyViewFragment;
import com.example.xpensmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

public class Expense extends AppCompatActivity{
    private String groupBy,filterType;
    Fragment fragment1,fragment2,fragment3;
    FragmentManager fm;
    Fragment active;
    BottomNavigationView bottomNavView;
    Bundle monthlyView, yearlyView, allView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        bottomNavView = findViewById(R.id.navigation);

        groupBy = getIntent().getExtras().getString("groupBy");
        filterType = getIntent().getExtras().getString("filterType");

        fragment1 = new MonthlyViewFragment();
        fragment2 = new YearlyViewFragment();
        fragment3 = new AllViewFragment();
        fm = getSupportFragmentManager();
        active = fragment1;

        monthlyView = new Bundle();
        monthlyView.putString("groupBy",groupBy);
        monthlyView.putString("filterType",filterType);
        monthlyView.putInt("filterValue",GenericExpenseDB.getMonthFromDate(new Date()));
        fragment1.setArguments(monthlyView);

        yearlyView = new Bundle();
        yearlyView.putString("groupBy",groupBy);
        yearlyView.putString("filterType",filterType);
        yearlyView.putInt("filterValue",GenericExpenseDB.getYearFromDate(new Date()));
        fragment2.setArguments(yearlyView);

        allView = new Bundle();
        allView.putString("groupBy",groupBy);
        allView.putString("filterType",filterType);
        fragment3.setArguments(allView);

        fm.beginTransaction().add(R.id.monthly_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.monthly_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.monthly_container,fragment1, "1").commit();

        bottomNavView.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.month:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.year:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.all:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        });

    }
}
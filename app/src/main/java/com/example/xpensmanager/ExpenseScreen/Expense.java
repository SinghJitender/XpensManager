package com.example.xpensmanager.ExpenseScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Database.GroupData;
import com.example.xpensmanager.ExpenseScreen.Fragments.AllViewFragment;
import com.example.xpensmanager.ExpenseScreen.Fragments.MonthlyViewFragment;
import com.example.xpensmanager.ExpenseScreen.Fragments.YearlyViewFragment;
import com.example.xpensmanager.MainScreen.Fragments.Category;
import com.example.xpensmanager.MainScreen.Fragments.Group;
import com.example.xpensmanager.MainScreen.Fragments.Home;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;

public class Expense extends AppCompatActivity{
    private String groupBy,filterType;
    Fragment fragment1,fragment2,fragment3;
    FragmentManager fm;
    Fragment active;
    BottomNavigationView bottomNavView;
    Bundle monthlyView, yearlyView, allView;
    //Database Objects
    private ExpenseDB expenseDB;
    private static GroupDB groupsDB;
    private static CategoryDB categoryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        bottomNavView = findViewById(R.id.navigation);
        bottomNavView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        groupBy = getIntent().getExtras().getString("groupBy");
        filterType = getIntent().getExtras().getString("filterType");
        expenseDB = new ExpenseDB(getApplicationContext());
        groupsDB = new GroupDB(getApplicationContext());
        categoryDB = new CategoryDB(getApplicationContext());
        fragment1 = new MonthlyViewFragment();
        fragment2 = new YearlyViewFragment();
        //fragment3 = new AllViewFragment();
        //fm = getSupportFragmentManager();
        //active = fragment1;

        openFragment(fragment1);

        monthlyView = new Bundle();
        monthlyView.putString("groupBy",groupBy);
        monthlyView.putString("filterType",filterType);
        monthlyView.putInt("filterValue", ExpenseDB.getMonthFromDate(new Date()));
        fragment1.setArguments(monthlyView);

        yearlyView = new Bundle();
        yearlyView.putString("groupBy",groupBy);
        yearlyView.putString("filterType",filterType);
        yearlyView.putInt("filterValue", ExpenseDB.getYearFromDate(new Date()));
        fragment2.setArguments(yearlyView);

       /* allView = new Bundle();
        allView.putString("groupBy",groupBy);
        allView.putString("filterType",filterType);
        fragment3.setArguments(allView);*/

        //fm.beginTransaction().add(R.id.monthly_container, fragment3, "3").hide(fragment3).commit();
        //fm.beginTransaction().add(R.id.monthly_container, fragment2, "2").hide(fragment2).commit();
        //fm.beginTransaction().add(R.id.monthly_container,fragment1, "1").commit();

    }

    @Override
    public void onBackPressed() {
        if(filterType.equalsIgnoreCase("None"))
            updateHomePageData();
        else if(filterType.equalsIgnoreCase("group"))
            updateGroupFragmentData();
        else if(filterType.equalsIgnoreCase("category"))
            updateCategoryFragmentData();
        super.onBackPressed();
    }

    public void updateHomePageData() {
            ArrayList<ExpenseData> data = new ArrayList<>();
            data.addAll(expenseDB.findByMonth(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date())));
            double totalSpentThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
            double totalCategorySum = categoryDB.getTotalCategoryLimitSum();
            Home.updateRecyclerView(data, totalSpentThisMonth, totalCategorySum);
    }

    public void updateGroupFragmentData(){
            ArrayList<GroupData> updatedResults = new ArrayList<>();
            ArrayList<Boolean> updateList = new ArrayList<>();
            updatedResults.addAll(groupsDB.findAll());
            for (int i = 0; i < updatedResults.size(); i++) {
                updateList.add(false);
            }
            Group.updateGroupAdapter(updatedResults, updateList);
    }

    public void updateCategoryFragmentData(){
            ArrayList<CategoryData> updatedResults = new ArrayList<>();
            ArrayList<Boolean> updateList = new ArrayList<>();
            updatedResults.addAll(categoryDB.findAll());
            for (int i = 0; i < updatedResults.size(); i++) {
                updateList.add(false);
            }
            Category.updateCategoryAdapter(updatedResults, updateList);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= ((item) -> {
        switch (item.getItemId()) {
            case R.id.month:
                openFragment(fragment1);
                return true;

            case R.id.year:
                openFragment(fragment2);
                return true;

           /* case R.id.all:
                openFragment(fragment3);
                return true;*/
        }
        return false;
    });

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
package com.jitender.xpensmanager.DataTesting;

import android.content.Context;
import android.util.Log;

import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.ExpenseDB;
import com.jitender.xpensmanager.Database.GroupDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class LoadTesting {
    String[] category = {"Fruits","Fuel","Vegetables","Grocery","Travel","Bills"};
    double[] categoryLimit = {3000,2000,2000,4000,2000,3000};
    String[] group = {"Personal","Bangalore Flat","Trio","Quads","Penta","Hexa","Decimala"};
    double[] groupLimit = {20000,13000,10000,5000,3000,2000,1000};
    int[] groupPeps = {1,2,3,4,5,6,10};
    String[] paidBy = {"Me","Others"};
    int[] year = {2016,2017,2018,2019,2020,2021,2020,2020,2020,2020,2019,2019,2016,2016,2016,2016};
    int[] month = {11,11,11,11,11,11,12,12,12,10,10,10,10,9,9,9,9,8,8,8,7,7,6,5,4,4,4,4,4,4,4,4,4,3,3,3,3,3,3,2,2,2,2,1,1,1};
    int[] day = {1,2,3,4,5,6,7,8,9,1,2,3,4,1,2,3,5,6,7,7,7,8,8,9,10,11,12,13,14,4,2,2,2,5,5,5,5,5,5,15,16,17,18,19,20,10,17,18,19,20,17,17,17,17,21,22,23,24,25,26,27,28,29,30,31,31,31,31,28,28,28,28,28,25,25,25,25,25,25,25,25,25,25,25,25,25,25};

    //Database Objects
    private ExpenseDB expenseDB;
    private static GroupDB groupsDB;
    private static CategoryDB categoryDB;

    public LoadTesting(Context context){
        expenseDB = new ExpenseDB(context);
        groupsDB = new GroupDB(context);
        categoryDB = new CategoryDB(context);
    }

    public void populateData(){
        populateCategory();
        populateGroup();
        try {
            populateExpenses();
        }catch (Exception e){
            Log.d("Error","Load Testing Error"+e.toString());
        }
    }

    private void populateCategory(){
        for(int i=0;i<category.length;i++){
            categoryDB.insertNewCategory(category[i],categoryLimit[i]);
        }
    }

    private void populateGroup(){
        for(int i=0;i<group.length;i++){
            groupsDB.insertNewGroup(group[i],groupPeps[i],groupLimit[i]);
        }
    }

    private void populateExpenses() throws ParseException {
        Random r = new Random();
        int noOfRecords = 10000;
        String expenseDescription = "Test Description";
        for(int i=0;i<noOfRecords;i++){
            double expenseAmount = r.nextInt(50)+10;
            int groupIndex = r.nextInt(group.length);
            //String date = (int)(r.nextInt(27)+1)+"/"+(int)(r.nextInt(12)+1)+"/"+year[r.nextInt(year.length)];
            String date = day[r.nextInt(day.length)]+"/"+month[r.nextInt(month.length)]+"/"+year[r.nextInt(year.length)];
            expenseDB.insertNewExpense(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date),expenseAmount,expenseDescription,category[r.nextInt(category.length)],
                    paidBy[r.nextInt(paidBy.length)],groupPeps[groupIndex],group[groupIndex]);
        }
    }
}

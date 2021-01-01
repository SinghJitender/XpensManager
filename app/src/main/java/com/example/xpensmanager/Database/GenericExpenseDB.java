package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.xpensmanager.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GenericExpenseDB extends SQLiteOpenHelper {
    public static String tableName = "self_expense";
    public static String expenseid = "id";
    public static String expensedate = "date";
    public static String expensedayOfWeek = "dayOfWeek";
    public static String expensetextMonth = "textMonth" ;
    public static String expenseday = "day";
    public static String expensemonth = "month" ;
    public static String expenseyear = "year" ;
    public static String expenseamount = "amount" ;
    public static String expensedescription = "description" ;
    public static String expensepaidBy = "paidBy" ;
    public static String expensecategory= "category" ;
    public static String expensedeleted= "deleted" ;
    public static String expensesplitAmount= "splitAmount" ;
    public static String expensegroup = "groupedWith";

    public GenericExpenseDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(tableName+" DB ","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+ tableName +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, dayOfWeek VARCHAR, textMonth INTEGER, month VARCHAR, year INTEGER, day INTEGER," +
                "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL, groupedWith VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(db);
    }

    public String insertNewExpense(Date date, double amount, String description, String category, String paidBy, int splitBetween, String groupedWith) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        double splitAmount = amount/splitBetween;
        String dayOfWeek = getDayOfWeek(date,Locale.ENGLISH);
        String textMonth = getDayOfMonth(date,Locale.ENGLISH);
        int year = getYearFromDate(date);
        int month = getMonthFromDate(date);
        int day = getDayFromDate(date);
        String stringDate = dateToString(date);

        Log.d(tableName+" DB : ","Original Date Object : "+date+ " dayOfWeek : "+dayOfWeek+" dayOfMonth:"+day+
                " textMonth : "+textMonth+" year :"+year+" month :"+month+" stringDate : "+stringDate+ " splitAmount : "+splitAmount);

        contentValues.put(expenseamount, amount);
        contentValues.put(expensedescription, description);
        contentValues.put(expensedate, stringDate);
        contentValues.put(expensedayOfWeek, dayOfWeek);
        contentValues.put(expensetextMonth, textMonth);
        contentValues.put(expensemonth, month);
        contentValues.put(expenseyear, year);
        contentValues.put(expenseday, day);
        contentValues.put(expensesplitAmount,new DecimalFormat("##.00").format(splitAmount));
        contentValues.put(expensepaidBy,paidBy);
        contentValues.put(expensecategory,category);
        contentValues.put(expensegroup,groupedWith);
        contentValues.put(expensedeleted,1); // 0 - true, 1 - false

        //Log.d(tableName+" DB : ","Content Values -" + contentValues.toString());
        try {
            db.insertOrThrow(tableName, null, contentValues);
            Log.d(tableName+" DB : ","Inserted into "+tableName+": Values -" + contentValues.toString());
            return "Created";
        }catch (Exception e){
            Log.d(tableName+" DB : ","Exception Occured : "+e +" \n Content Values : "+ contentValues.toString());
            return "Some error occurred. Try again!";
        }
        //return "NULL";
    }

    public ArrayList<ExpenseData> findAll(){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" order by "+expenseyear+" DESC, "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findAllByGroup(String group){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where groupedWith = '"+group+"' order by "+expenseyear+" DESC, "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }


    public ArrayList<ExpenseData> findAllByCategory(String category){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where category = '"+category+"' order by "+expenseyear+" DESC, "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByYear(int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where year = "+year+" order by "+expensemonth+" DESC , "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByYearAndGroup(String group, int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where year = "+year+" and groupedWith = '"+group+"' order by "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByYearAndCategory(String category, int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where year = "+year+" and category = '"+category+"' order by "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }



    public ArrayList<ExpenseData> findByMonth(int month){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where month = "+month+" order by "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByMonthAndGroup(String group, int month){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where month = "+month+" and groupedWith = '"+group+"' order by "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByMonthAndCategory(String category, int month){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where month = "+month+" and category = '"+category+"' order by "+expenseday+" DESC, "+expenseid+" DESC", null );
        while(cursor.moveToNext()){
            ExpenseData data = new ExpenseData();
            data.setId(cursor.getInt(cursor.getColumnIndex(expenseid)));
            data.setDate(cursor.getString(cursor.getColumnIndex(expensedate)));
            data.setDayOfWeek(cursor.getString(cursor.getColumnIndex(expensedayOfWeek)));
            data.setTextMonth(cursor.getString(cursor.getColumnIndex(expensetextMonth)));
            data.setMonth(cursor.getInt(cursor.getColumnIndex(expensemonth)));
            data.setYear(cursor.getInt(cursor.getColumnIndex(expenseyear)));
            data.setDay(cursor.getInt(cursor.getColumnIndex(expenseday)));
            data.setAmount(cursor.getDouble(cursor.getColumnIndex(expenseamount)));
            data.setDescription(cursor.getString(cursor.getColumnIndex(expensedescription)));
            data.setPaidBy(cursor.getString(cursor.getColumnIndex(expensepaidBy)));
            data.setCategory(cursor.getString(cursor.getColumnIndex(expensecategory)));
            data.setDeleted(cursor.getInt(cursor.getColumnIndex(expensedeleted)));
            data.setSplitAmount(cursor.getDouble(cursor.getColumnIndex(expensesplitAmount)));
            data.setGroup(cursor.getString(cursor.getColumnIndex(expensegroup)));
            list.add(data);
        }
        return list;
    }



    public double getMonthlyExpenseSum(int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year + ");", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getMonthTotalForGroup(String groupName, int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and " + expensegroup + " = '" + groupName + "');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getMonthTotalForCategory(String category, int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and " + expensecategory + " = '" + category + "');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public static String getDayOfWeek(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }

    public static String getDayOfMonth(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("MMMM", locale);
        return formatter.format(date);
    }

    public static int getYearFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getDayFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
}

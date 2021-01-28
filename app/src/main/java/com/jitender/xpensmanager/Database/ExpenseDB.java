package com.jitender.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jitender.xpensmanager.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ExpenseDB extends SQLiteOpenHelper {
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
    public static String expensesettled = "expenseSettled"; //true or false
    public static String expensesettleamount = "settledAmount"; // totalAmount-Split or totalAmount - paidAmount
    public static String expensemodeofpayment = "modeOfPayment"; //Cash, UPI, Debit Card, Credit Card, etc
    public static Calendar calendar = new GregorianCalendar();

    public ExpenseDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(tableName+" DB ","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+ tableName +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, dayOfWeek VARCHAR, textMonth INTEGER, month VARCHAR, year INTEGER, day INTEGER," +
                "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL, groupedWith VARCHAR, expenseSettled VARCHAR, settledAmount REAL, modeOfPayment VARCHAR )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(db);
    }

    public Integer deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, null,null);
    }

    public Integer deleteExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public int getExpenseCount() {
        String countQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public String insertNewExpense(Date date, double amount, String description, String category, String paidBy, int splitBetween, String groupedWith,
                                   String modeOfPayment, String settled, double settleAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        double splitAmount = amount/splitBetween;
        String dayOfWeek = getDayOfWeek(date,Locale.ENGLISH);
        String textMonth = getNameOfMonth(date,Locale.ENGLISH);
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
        contentValues.put(expensemodeofpayment,modeOfPayment);
        contentValues.put(expensesettled,settled);
        contentValues.put(expensesettleamount,settleAmount);

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

    public String insertNewExpenseFromBackup(Date date, double amount, String description, String category, String paidBy, double splitAmount, String groupedWith, String modeOfPayment, String settled, double settleAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String dayOfWeek = getDayOfWeek(date,Locale.ENGLISH);
        String textMonth = getNameOfMonth(date,Locale.ENGLISH);
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
        contentValues.put(expensemodeofpayment,modeOfPayment);
        contentValues.put(expensesettled,settled);
        contentValues.put(expensesettleamount,settleAmount);

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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByYearAndPayment(String mode, int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where year = "+year+" and modeOfPayment = '"+mode+"' order by "+expensemonth+" DESC, "+expenseday+" DESC, "+expenseid+" DESC", null );
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
            list.add(data);
        }
        return list;
    }



    public ArrayList<ExpenseData> findByMonth(int month,int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where (month = "+month+" and year = "+year+") order by "+expenseday+" DESC, "+expenseid+" DESC", null );
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByMonthAndGroup(String group, int month,int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where  (month = "+month+" and year = "+year+" and groupedWith = '"+group+"') order by "+expenseday+" DESC, "+expenseid+" DESC", null );
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByMonthAndCategory(String category, int month,int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where month = "+month+" and year = "+year+" and category = '"+category+"' order by "+expenseday+" DESC, "+expenseid+" DESC", null );
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
            list.add(data);
        }
        return list;
    }

    public ArrayList<ExpenseData> findByMonthAndPayments(String mode, int month,int year){
        ArrayList<ExpenseData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+tableName+" where month = "+month+" and year = "+year+" and modeOfPayment = '"+mode+"' order by "+expenseday+" DESC, "+expenseid+" DESC", null );
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
            data.setSettled(cursor.getString(cursor.getColumnIndex(expensesettled)));
            data.setSettledAmount(cursor.getDouble(cursor.getColumnIndex(expensesettleamount)));
            data.setModeOfPayment(cursor.getString(cursor.getColumnIndex(expensemodeofpayment)));
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

    public double getMonthlyExpenseSumByCategory(int month, int year,String category){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and category = '"+category+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getMonthlyExpenseSumByPayments(int month, int year,String mode){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and modeOfPayment = '"+mode+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }



    public double getMonthlyExpenseSumByGroup(int month, int year,String group){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and groupedWith = '"+group+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getYearlyExpenseSum(int year){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expenseyear + " = " + year + ");", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getYearlyExpenseSumByCategory(int year,String category){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expenseyear + " = " + year + " and "+expensecategory+" = '"+category+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getYearlyExpenseSumByPayment(int year,String mode){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expenseyear + " = " + year + " and "+expensemodeofpayment+" = '"+mode+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getYearlyExpenseSumByGroup(int year,String group){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expenseyear + " = " + year + " and "+expensegroup+" = '"+group+"');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getAllExpenseSum(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName , null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getAllExpenseSumByCategory(String category){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName+" where "+expensecategory+" = '"+category+"'", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getAllExpenseSumByGroup(String group){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName+" where "+expensegroup+" = '"+group+"'", null);
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

    public double getMonthTotalForPayment(String mode, int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum(splitAmount) from " + tableName + " where (" + expensemonth + " = " + month + " and " + expenseyear + " = " + year +" and " + expensemodeofpayment + " = '" + mode + "');", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public HashMap<String,Double> getSumForAllWeekDays(){
        HashMap<String,Double> results = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+expensedayOfWeek+",sum(splitAmount) from " + tableName + " group by " + expensedayOfWeek + ";", null);
            while(cursor.moveToNext()){
                results.put(cursor.getString(0),cursor.getDouble(1));
            }
            return results;
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return null;
        }
    }

    public HashMap<String,Double> getSumForAllMonths(){
        HashMap<String,Double> results = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+expensetextMonth+",sum(splitAmount) from " + tableName + " group by " + expensetextMonth + ";", null);
            while(cursor.moveToNext()){
                results.put(cursor.getString(0),cursor.getDouble(1));
            }
            return results;
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return null;
        }
    }

    public HashMap<Integer,Double> getSumForAllDays(){
        HashMap<Integer,Double> results = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+expenseday+",sum(splitAmount) from " + tableName + " group by " + expenseday + ";", null);
            while(cursor.moveToNext()){
                results.put(cursor.getInt(0),cursor.getDouble(1));
            }
            return results;
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return null;
        }
    }

    public LinkedHashMap<String,Double> getSumByCategory(){
        LinkedHashMap<String,Double> results = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+expensecategory+",sum(splitAmount) as sumOfCategories from " + tableName + " group by " + expensecategory + " order by sumOfCategories desc;", null);
            while(cursor.moveToNext()){
                results.put(cursor.getString(0),cursor.getDouble(1));
            }
            return results;
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return null;
        }
    }

    public HashMap<String,Double> getSumByGroup(){
        HashMap<String,Double> results = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+expensegroup+",sum(splitAmount) from " + tableName + " group by " + expensegroup + ";", null);
            while(cursor.moveToNext()){
                results.put(cursor.getString(0),cursor.getDouble(1));
            }
            return results;
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return null;
        }
    }

    public int getCountOfAllMonthRecords(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("Select count(*) from (select distinct year,month from "+tableName+")",null);
            c.moveToNext();
            Log.d("Distinct Values",""+c.getInt(0));
            return c.getInt(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0;
        }
    }

    public int getCountOfDistinctMonthRecords(String month){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("Select count(*) from (select distinct year from "+tableName+" where "+expensetextMonth+" = '"+month+"')",null);
            c.moveToNext();
            Log.d("Distinct "+month+" Values",""+c.getInt(0));
            return c.getInt(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0;
        }
    }

    public int getCountOfDistinctDayRecords(int day){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("Select count(*) from (select distinct year,month from "+tableName+" where "+expenseday+" = '"+day+"')",null);
            c.moveToNext();
            Log.d("Distinct Day-"+day+" Values",""+c.getInt(0));
            return c.getInt(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0;
        }
    }

    public int getCountOfDistinctWeekDaysRecords(String weekDay){
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            Cursor c = db.rawQuery("Select count(*) from (select distinct year,month,day from self_expense where "+expensedayOfWeek+" = '"+weekDay+"')",null);
            c.moveToNext();
            Log.d("Distinct "+weekDay+" Values",""+c.getInt(0));
            return c.getInt(0);
        }catch (SQLException e){
            Log.d("GenericExpenseDB","Exception : "+e.toString());
            return 0;
        }
    }

    public static String getDayOfWeek(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }

    public static String getNameOfMonth(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("MMMM", locale);
        return formatter.format(date);
    }

    public static int getYearFromDate(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getDayFromDate(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthFromDate(Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static String monthTextFromIntMonth(int mon){
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        return months[mon%12];
    }
}

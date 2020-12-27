package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.xpensmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GenericExpenseDB extends SQLiteOpenHelper {
    private String tableName;
    public static String expenseid = "id";
    public static String expensedate = "date";
    public static String expensedayOfWeek = "dayOfWeek";
    public static String expensetextMonth = "textMonth" ;
    public static String expensemonth = "month" ;
    public static String expenseyear = "year" ;
    public static String expenseamount = "amount" ;
    public static String expensedescription = "description" ;
    public static String expensepaidBy = "paidBy" ;
    public static String expensecategory= "category" ;
    public static String expensedeleted= "deleted" ;
    public static String expensesplitAmount= "splitAmount" ;

    public GenericExpenseDB(Context context,String tableName) {
        super(context,context.getString(R.string.database_name),null,1);
        this.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(tableName+" DB ","Creating DB");
        db.execSQL( "CREATE TABLE "+ tableName +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, dayOfWeek VARCHAR, textMonth INTEGER, month VARCHAR, year INTEGER," +
                "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(db);
    }

    public String insertNewExpense(Date date, double amount, String description, String category, String paidBy, int splitBetween) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        double splitAmount = amount/splitBetween;
        String dayOfWeek = getDayOfWeek(date,Locale.ENGLISH);
        String textMonth = getDayOfMonth(date,Locale.ENGLISH);
        int year = getYearFromDate(date);
        int month = getMonthFromDate(date);
        String stringDate = dateToString(date);

        Log.d(tableName+" DB : ","Original Date Object : "+date+ " dayOfWeek : "+dayOfWeek+
                " textMonth : "+textMonth+" year :"+year+" month :"+month+" stringDate : "+stringDate+ " splitAmount : "+splitAmount);

        contentValues.put(expenseamount, amount);
        contentValues.put(expensedescription, description);
        contentValues.put(expensedate, stringDate);
        contentValues.put(expensedayOfWeek, dayOfWeek);
        contentValues.put(expensetextMonth, textMonth);
        contentValues.put(expensemonth, month);
        contentValues.put(expenseyear, year);
        contentValues.put(expensesplitAmount,splitAmount);
        contentValues.put(expensepaidBy,paidBy);
        contentValues.put(expensecategory,category);
        contentValues.put(expensedeleted,1); // 0 - true, 1 - false

        //Log.d(tableName+" DB : ","Content Values -" + contentValues.toString());
        try {
            db.insertOrThrow(tableName, null, contentValues);
            Log.d(tableName+" DB : ","Inserted into "+tableName+": Values -" + contentValues.toString());
            return "Created";
        }catch (Exception e){
            Log.d(tableName+" DB : ","Exception Occured : "+e);
            return "Some error occurred. Try again!";
        }
        //return "NULL";
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

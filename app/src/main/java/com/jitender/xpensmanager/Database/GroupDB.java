package com.jitender.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jitender.xpensmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class GroupDB extends SQLiteOpenHelper {

    public static String groupdb_table = "groups";

    public static String groupdb_id = "id";
    public static String groupdb_title = "title";
    public static String groupdb_noOfPersons = "noOfPersons";
    public static String groupdb_maxLimit = "maxLimit";
    public static String groupdb_netAmount = "netAmount";
    public static String groupdb_totalAmount = "totalAmount";

    private ExpenseDB expenseDB;

    public GroupDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
        expenseDB = new ExpenseDB(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("GroupDB","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS groups " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR UNIQUE, noOfPersons INTEGER, maxLimit REAL, netAmount REAL, totalAmount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS groups");
        onCreate(db);
    }

    public String insertNewGroup(String title, int noOfPersons, double maxLimit) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", 0.0);
        contentValues.put("totalAmount", 0.0);
        try {
            db.insertOrThrow("groups", null, contentValues);
            Log.d("GroupDB","Inserted into groups: Values -" + contentValues.toString());
            return "Created";
        }catch (SQLiteConstraintException e){
            Log.d("GroupDB","Exception Occured : "+e);
            if(e.toString().contains("UNIQUE constraint failed: groups.title")) {
                Log.d("GroupDB","Title Not Unique");
                return "Please use different title";
            }
            return "Some error occurred. Try again!";
        }
    }

    public String insertNewGroupFromBackup(String title, int noOfPersons, double maxLimit,double netAmount, double totalAmount) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        try {
            db.insertOrThrow("groups", null, contentValues);
            Log.d("GroupDB","Inserted into groups: Values -" + contentValues.toString());
            return "Created";
        }catch (SQLiteConstraintException e){
            Log.d("GroupDB","Exception Occured : "+e);
            if(e.toString().contains("UNIQUE constraint failed: groups.title")) {
                Log.d("GroupDB","Title Not Unique");
                return "Please use different title";
            }
            return "Some error occurred. Try again!";
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, groupdb_table);
        return numRows;
    }

    public boolean updateGroupByTitle(String title, int noOfPersons, double maxLimit, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        db.update("groups", contentValues, "title = ? ", new String[] { title } );
        return true;
    }

    public boolean updateNetAmountByTitle(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("netAmount", 0.00);
        db.update("groups", contentValues, "title = ? ", new String[] { title } );
        return true;
    }

    public boolean updateGroupById(int id,String title, int noOfPersons, double maxLimit, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        db.update("groups", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateGroupAmountByTitle(String title, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        db.update("groups", contentValues, "title = ? ", new String[] { title } );
        return true;
    }

    public boolean updateGroupLimitAndPersons(String title, int noOfPersons, double maxLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        db.update("groups", contentValues, "title = ? ", new String[] { title } );
        return true;
    }

    public double getNetAmountByTitle(String grouptitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+groupdb_netAmount+" from groups where "+groupdb_title+" = '"+grouptitle+"'", null );
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(groupdb_netAmount));
    }

    public double getTotalAmountByTitle(String grouptitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+groupdb_totalAmount+" from groups where "+groupdb_title+" = '"+grouptitle+"'", null );
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(groupdb_totalAmount));
    }

    public double getGroupLimitByTitle(String grouptitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+groupdb_maxLimit+" from groups where "+groupdb_title+" = '"+grouptitle+"'", null );
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(groupdb_maxLimit));
    }

    public Integer deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(groupdb_table, null,null);
    }

    public Integer deleteGroupByTitle(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("groups",
                "title = ? ",
                new String[] { title });
    }

    public Integer deleteGroupById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("groups",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<GroupData> findAll(){
        ArrayList<GroupData> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from groups", null );
        while (cursor.moveToNext()){
            GroupData temp = new GroupData();
            temp.setId(cursor.getInt(cursor.getColumnIndex(groupdb_id)));
            temp.setTitle(cursor.getString(cursor.getColumnIndex(groupdb_title)));
            temp.setNoOfPersons(cursor.getInt(cursor.getColumnIndex(groupdb_noOfPersons)));
            temp.setMaxLimit(cursor.getDouble(cursor.getColumnIndex(groupdb_maxLimit)));
            temp.setNetAmount(cursor.getDouble(cursor.getColumnIndex(groupdb_netAmount)));
            temp.setTotalAmount(cursor.getDouble(cursor.getColumnIndex(groupdb_totalAmount)));
            temp.setGroupTotal(expenseDB.getMonthTotalForGroup(cursor.getString(cursor.getColumnIndex(groupdb_title)), ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date())));
            results.add(temp);
        }
        return results;
    }

    public ArrayList<String> findAllGroups(){
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+groupdb_title+" from groups", null );
        while (cursor.moveToNext()){
            results.add(cursor.getString(cursor.getColumnIndex(groupdb_title)));
        }
        return results;
    }

    public int findSplitBetween(String grouptitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+groupdb_noOfPersons+" from groups where "+groupdb_title+" = '"+grouptitle+"'", null );
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex(groupdb_noOfPersons));
    }

    public Cursor executeQuery(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query,null);
        return res;
    }

    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}

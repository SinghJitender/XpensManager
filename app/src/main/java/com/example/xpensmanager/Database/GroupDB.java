package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.xpensmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import static android.content.Context.MODE_PRIVATE;


public class GroupDB extends SQLiteOpenHelper {

    public static String groupdb_table = "groups";
    public static String groupdb_id = "id";
    public static String groupdb_title = "title";
    public static String groupdb_noOfPersons = "noOfPersons";
    public static String groupdb_maxLimit = "maxLimit";
    public static String groupdb_createdOn = "createdOn";
    public static String groupdb_netAmount = "netAmount";
    public static String groupdb_totalAmount = "totalAmount";
    public static String groupdb_modifiedOn = "modifiedOn";
    public static String groupdb_tableName = "tableName";

    public GroupDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("GroupDB","Creating DB");
        db.execSQL( "CREATE TABLE groups " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR UNIQUE, noOfPersons INTEGER, maxLimit REAL, createdOn VARCHAR, netAmount REAL, totalAmount REAL, modifiedOn VARCHAR, tableName VARCHAR)");
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

        String createdOn = getDate();
        String modifiedOn = getDate();
        String tableName = title.replaceAll(" ","_").toLowerCase();

        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("createdOn", createdOn);
        contentValues.put("netAmount", 0.0);
        contentValues.put("totalAmount", 0.0);
        contentValues.put("modifiedOn", modifiedOn);
        contentValues.put("tableName",tableName);
        try {
            db.insertOrThrow("groups", null, contentValues);

            db.execSQL( "CREATE TABLE "+ tableName +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, dayOfWeek VARCHAR, dayOfMonth INTEGER, month VARCHAR, year INTEGER," +
                    "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL)");

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
        String modifiedOn = getDate();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        contentValues.put("modifiedOn", modifiedOn);
        db.update("groups", contentValues, "title = ? ", new String[] { title } );
        return true;
    }

    public boolean updateGroupById(int id,String title, int noOfPersons, double maxLimit, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String modifiedOn = getDate();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        contentValues.put("modifiedOn", modifiedOn);
        db.update("groups", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
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

    public ArrayList<Hashtable<String,String>> findAll(){
        ArrayList<Hashtable<String,String>> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from groups", null );
        while (cursor.moveToNext()){
            Hashtable<String,String> temp = new Hashtable<>();
            temp.put("id",cursor.getString(cursor.getColumnIndex(groupdb_id)));
            temp.put("title",cursor.getString(cursor.getColumnIndex(groupdb_title)));
            temp.put("noOfPersons",cursor.getString(cursor.getColumnIndex(groupdb_noOfPersons)));
            temp.put("maxLimit",cursor.getString(cursor.getColumnIndex(groupdb_maxLimit)));
            temp.put("netAmount",cursor.getString(cursor.getColumnIndex(groupdb_netAmount)));
            temp.put("totalAmount",cursor.getString(cursor.getColumnIndex(groupdb_totalAmount)));
            temp.put("modifiedOn",cursor.getString(cursor.getColumnIndex(groupdb_modifiedOn)));
            temp.put("createdOn",cursor.getString(cursor.getColumnIndex(groupdb_createdOn)));
            temp.put("tableName",cursor.getString(cursor.getColumnIndex(groupdb_tableName)));
            temp.put("showMenu","false");
            results.add(temp);
        }
        return results;
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

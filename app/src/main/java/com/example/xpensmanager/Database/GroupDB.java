package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xpensmanager.R;

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

    public GroupDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE IF NOT EXISTS groups " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR UNIQUE, noOfPersons INTEGER, maxLimit REAL, createdOn VARCHAR, netAmount REAL, totalAmount REAL, modifiedOn VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS groups");
        onCreate(db);
    }

    public boolean insertNewGroup(String title, int noOfPersons, double maxLimit, String createdOn, String modifiedOn, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("noOfPersons", noOfPersons);
        contentValues.put("maxLimit", maxLimit);
        contentValues.put("createdOn", createdOn);
        contentValues.put("netAmount", netAmount);
        contentValues.put("totalAmount", totalAmount);
        contentValues.put("modifiedOn", modifiedOn);
        db.insert("groups", null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, groupdb_table);
        return numRows;
    }

    public boolean updateGroupByTitle(String title, int noOfPersons, double maxLimit, String modifiedOn, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
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

    public boolean updateGroupById(int id,String title, int noOfPersons, double maxLimit, String modifiedOn, double netAmount, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
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

    public Cursor executeQuery(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query,null);
        return res;
    }

}

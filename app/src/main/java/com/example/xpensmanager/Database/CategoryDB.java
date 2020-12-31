package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.xpensmanager.R;
import java.util.ArrayList;

public class CategoryDB extends SQLiteOpenHelper {

    public static String categorydb_table = "category";
    public static String categorydb_id = "id";
    public static String categorydb_categoryname = "categoryname";


    public CategoryDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("GroupDB","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+categorydb_table+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, categoryname VARCHAR UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+categorydb_table);
        onCreate(db);
    }

    public String insertNewCategory(String categoryname) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("categoryname", categoryname);
        try {
            db.insertOrThrow(categorydb_table, null, contentValues);
            Log.d(categorydb_table,"Inserted into category: Values -" + contentValues.toString());
            return "Created";
        }catch (SQLiteConstraintException e){
            Log.d(categorydb_table,"Exception Occured : "+e);
            if(e.toString().contains("UNIQUE constraint failed: category.categoryname")) {
                Log.d(categorydb_table,"Category Name Not Unique");
                return "Please use different name";
            }
            return "Some error occurred. Try again!";
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, categorydb_table);
        return numRows;
    }

    public Integer deleteCategoryByTitle(String categoryname) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("groups",
                "categoryname = ? ",
                new String[] { categoryname });
    }

    public Integer deleteCategoryById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("groups",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> findAll(){
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+categorydb_table, null );
        while (cursor.moveToNext()){
            results.add(cursor.getString(cursor.getColumnIndex(categorydb_categoryname)));
        }
        return results;
    }

}

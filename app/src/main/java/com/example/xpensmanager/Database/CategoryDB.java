package com.example.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
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
    public static String categorydb_categorylimit = "categorylimit";
    public static String categorydb_totalcategoryspend = "totalcategoryspend";

    public CategoryDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("GroupDB","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+categorydb_table+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, categoryname VARCHAR UNIQUE,categorylimit REAL,totalcategoryspend REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+categorydb_table);
        onCreate(db);
    }

    public String insertNewCategory(String categoryname, double categorylimit) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("categoryname", categoryname);
        contentValues.put("categorylimit",categorylimit);
        contentValues.put("totalcategoryspend",0.0);
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
        return db.delete(categorydb_table,
                "categoryname = ? ",
                new String[] { categoryname });
    }

    public Integer deleteCategoryById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(categorydb_table,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<CategoryData> findAll(){
        ArrayList<CategoryData> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+categorydb_table, null );
        while (cursor.moveToNext()){
            CategoryData data = new CategoryData(cursor.getInt(cursor.getColumnIndex(categorydb_id)),
                    cursor.getString(cursor.getColumnIndex(categorydb_categoryname)),
                    cursor.getDouble(cursor.getColumnIndex(categorydb_categorylimit)),
                    cursor.getDouble(cursor.getColumnIndex(categorydb_totalcategoryspend)));
            results.add(data);
        }
        return results;
    }

    public ArrayList<String> findAllCategories(){
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+categorydb_table, null );
        while (cursor.moveToNext()){
            results.add(cursor.getString(cursor.getColumnIndex(categorydb_categoryname)));
        }
        return results;
    }

    public boolean updateCategoryLimitByCategoryName(String title, double maxLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(categorydb_categoryname, title);
        contentValues.put(categorydb_categorylimit, maxLimit);
        db.update(categorydb_table, contentValues, "categoryname = ? ", new String[] { title } );
        return true;
    }

    public boolean updateCategoryAmountByTitle(String title, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(categorydb_totalcategoryspend, totalAmount);
        db.update(categorydb_table, contentValues, "categoryname = ? ", new String[] { title } );
        return true;
    }

    public double getTotalAmountByTitle(String categoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+categorydb_totalcategoryspend+" from "+categorydb_table +" where "+categorydb_categoryname+" = '"+categoryName+"'", null );
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(categorydb_totalcategoryspend));
    }


    public double getTotalCategoryLimitSum(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum("+categorydb_categorylimit+") from " + categorydb_table + ";", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("CategoryDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getCategoryLimitByTitle(String category){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+categorydb_categorylimit+" from " + categorydb_table + " where "+categorydb_categoryname+" = '"+category+"';", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("CategoryDB","Exception : "+e.toString());
            return 0.0;
        }
    }



}

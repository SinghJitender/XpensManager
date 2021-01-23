package com.jitender.xpensmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jitender.xpensmanager.R;

import java.util.ArrayList;

public class PaymentsDB extends SQLiteOpenHelper {
    public static String paymentsdb_table = "payments";
    public static String paymentsdb_id = "id";
    public static String paymentsdb_modename = "modename";
    public static String paymentsdb_modelimit = "modelimit";
    public static String paymentsdb_totalmodespend = "totalmodespend";

    public PaymentsDB(Context context) {
        super(context,context.getString(R.string.database_name),null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("GroupDB","Creating DB");
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+paymentsdb_table+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, modename VARCHAR UNIQUE,modelimit REAL,totalmodespend REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+paymentsdb_table);
        onCreate(db);
    }

    public String insertNewPaymentMode(String modename, double modelimit) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(paymentsdb_modename, modename);
        contentValues.put(paymentsdb_modelimit,modelimit);
        contentValues.put(paymentsdb_totalmodespend,0.0);
        try {
            db.insertOrThrow(paymentsdb_table, null, contentValues);
            Log.d(paymentsdb_table,"Inserted into payment: Values -" + contentValues.toString());
            return "Created";
        }catch (SQLiteConstraintException e){
            Log.d(paymentsdb_table,"Exception Occured : "+e);
            if(e.toString().contains("UNIQUE constraint failed: payments.modename")) {
                Log.d(paymentsdb_table,"Payment Mode Name Not Unique");
                return "Please use different name";
            }
            return "Some error occurred. Try again!";
        }
    }

    public String insertNewPaymentFromBackup(String modename, double modelimit,double totalmodespent) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(paymentsdb_modename, modename);
        contentValues.put(paymentsdb_modelimit,modelimit);
        contentValues.put(paymentsdb_totalmodespend,totalmodespent);
        try {
            db.insertOrThrow(paymentsdb_table, null, contentValues);
            Log.d(paymentsdb_table,"Inserted into payment: Values -" + contentValues.toString());
            return "Created";
        }catch (SQLiteConstraintException e){
            Log.d(paymentsdb_table,"Exception Occured : "+e);
            if(e.toString().contains("UNIQUE constraint failed: payments.modename")) {
                Log.d(paymentsdb_table,"Payment Mode Name Not Unique");
                return "Please use different name";
            }
            return "Some error occurred. Try again!";
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, paymentsdb_table);
        return numRows;
    }

    public Integer deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(paymentsdb_table, null,null);
    }

    public Integer deletePaymentByTitle(String categoryname) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(paymentsdb_table,
                paymentsdb_modename+" = ? ",
                new String[] { categoryname });
    }

    public Integer deletePaymentById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(paymentsdb_table,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<PaymentsData> findAll(){
        ArrayList<PaymentsData> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+paymentsdb_table, null );
        while (cursor.moveToNext()){
            PaymentsData data = new PaymentsData(cursor.getInt(cursor.getColumnIndex(paymentsdb_id)),
                    cursor.getString(cursor.getColumnIndex(paymentsdb_modename)),
                    cursor.getDouble(cursor.getColumnIndex(paymentsdb_modelimit)),
                    cursor.getDouble(cursor.getColumnIndex(paymentsdb_totalmodespend)));
            results.add(data);
        }
        return results;
    }

    public ArrayList<String> findAllPaymentModes(){
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+paymentsdb_table, null );
        while (cursor.moveToNext()){
            results.add(cursor.getString(cursor.getColumnIndex(paymentsdb_modename)));
        }
        return results;
    }

    public boolean updatePaymentLimitByModeName(String title, double maxLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(paymentsdb_modename, title);
        contentValues.put(paymentsdb_modelimit, maxLimit);
        db.update(paymentsdb_table, contentValues, "modename = ? ", new String[] { title } );
        return true;
    }

    public boolean updatePaymentAmountByMode(String mode, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(paymentsdb_totalmodespend, totalAmount);
        db.update(paymentsdb_table, contentValues, "modename = ? ", new String[] { mode } );
        return true;
    }

    public double getTotalAmountByMode(String modeName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select "+paymentsdb_totalmodespend+" from "+paymentsdb_table +" where "+paymentsdb_modename+" = '"+modeName+"'", null );
        cursor.moveToNext();
        return cursor.getDouble(cursor.getColumnIndex(paymentsdb_totalmodespend));
    }


    public double getTotalPaymentLimitSum(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select sum("+paymentsdb_modelimit+") from " + paymentsdb_table + ";", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("PaymentsDB","Exception : "+e.toString());
            return 0.0;
        }
    }

    public double getPaymentLimitByTitle(String modeName){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select "+paymentsdb_modelimit+" from " + paymentsdb_table + " where "+paymentsdb_modename+" = '"+modeName+"';", null);
            cursor.moveToNext();
            return cursor.getDouble(0);
        }catch (SQLException e){
            Log.d("Payments","Exception : "+e.toString());
            return 0.0;
        }
    }


}

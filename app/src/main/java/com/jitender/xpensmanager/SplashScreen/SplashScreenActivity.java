package com.jitender.xpensmanager.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.jitender.xpensmanager.DataTesting.LoadTesting;
import com.jitender.xpensmanager.MainScreen.MainActivity;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SetupScreen.Setup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private String LOG_TAG = "SplashScreen";
    private CircleImageView imageView;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase mydatabase;
    public static String cSymbol;
    public static Long salary;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.profile_image);
        executorService = Executors.newFixedThreadPool(2);

        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), getApplicationContext().MODE_PRIVATE);
        editor = sharedPref.edit();


        executorService.execute(() ->{
            mydatabase = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS self_expense"+
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, dayOfWeek VARCHAR, textMonth INTEGER, month VARCHAR, year INTEGER, day INTEGER," +
                    "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL, groupedWith VARCHAR, " +
                    "expenseSettled VARCHAR, settledAmount REAL, modeOfPayment VARCHAR)");
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS groups " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR UNIQUE, noOfPersons INTEGER, maxLimit REAL, netAmount REAL, totalAmount REAL)");
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS category"+
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, categoryname VARCHAR UNIQUE, categorylimit REAL,totalcategoryspend REAL)");
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS payments"+
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, modename VARCHAR UNIQUE, modelimit REAL,totalmodespend REAL)");
            mydatabase.close();
            Log.d(LOG_TAG,"Database Ready!");
        });
        new Handler().postDelayed(()->{
            boolean initialSetup = sharedPref.getBoolean("initialSetup",true);
            cSymbol = sharedPref.getString("cSymbol","#");
            salary = sharedPref.getLong("salary",0);
            Log.d(LOG_TAG,"Shared Preference Values Fetched");
            if(initialSetup == false){
                moveToMainActivity();
            }
            else{
                startActivity(new Intent(getApplicationContext(), Setup.class));
                overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
                finish();
            }
        },400);
    }

    public void moveToMainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        finish();
    }
}
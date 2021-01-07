package com.example.xpensmanager.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.xpensmanager.BackupAndRestoreUtils.AutomaticBackup;
import com.example.xpensmanager.BackupAndRestoreUtils.AutomaticBackupManager;
import com.example.xpensmanager.BackupAndRestoreUtils.BackupService;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.victor.loading.rotate.RotateLoading;

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
                    "amount REAL, description VARCHAR, paidBy VARCHAR, category VARCHAR, deleted INTEGER, splitAmount REAL, groupedWith VARCHAR)");
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS groups " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR UNIQUE, noOfPersons INTEGER, maxLimit REAL, netAmount REAL, totalAmount REAL)");
            mydatabase.execSQL( "CREATE TABLE IF NOT EXISTS category"+
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, categoryname VARCHAR UNIQUE, categorylimit REAL,totalcategoryspend REAL)");
            mydatabase.close();
            Log.d(LOG_TAG,"Database Ready!");
        });



        boolean initialSetup = sharedPref.getBoolean("initialSetup",true);
        cSymbol = sharedPref.getString("cSymbol","#");
        salary = sharedPref.getLong("salary",0);
        Log.d(LOG_TAG,"Shared Preference Values Fetched");


        if(initialSetup == false){
            moveToMainActivity();
        }
        else{
           //do something then move to main activity
            editor.putBoolean("initialSetup",false);
            editor.apply();
            moveToMainActivity();
        }

    }

    public void moveToMainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
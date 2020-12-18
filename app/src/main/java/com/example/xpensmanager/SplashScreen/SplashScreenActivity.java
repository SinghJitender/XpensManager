package com.example.xpensmanager.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.victor.loading.rotate.RotateLoading;
import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private String LOG_TAG = "SplashScreen";
    private RotateLoading rotateLoading;
    private FirebaseAuth mAuth;
    private Button signup,skip;
    private TransitionButton login;
    private LinearLayout layout;
    private CircleImageView imageView;
    private EditText username, password;
    private int flag = 1;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        layout = findViewById(R.id.linerLayout);
        imageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        skip = findViewById(R.id.skip);

        if(!rotateLoading.isStart())
            rotateLoading.start();

        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), getApplicationContext().MODE_PRIVATE);
        editor = sharedPref.edit();

        mydatabase = openOrCreateDatabase(getString(R.string.database_name),MODE_PRIVATE,null);
        String query = "create table if not exists expenses(id INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR, description VARCHAR, amount INTEGER, date VARCHAR, paidby VARCHAR, category VARCHAR, month INTEGER, year INTEGER)";
        mydatabase.execSQL(query);
        mydatabase.close();
        Log.d(LOG_TAG,"Database Ready!");

        boolean initialSetup = sharedPref.getBoolean("initialSetup",true);
        String userId = sharedPref.getString("userId",null);
        String userPassword = sharedPref.getString("userPassword",null);
        boolean skipLogin = sharedPref.getBoolean("skipLogin",false);
        boolean userLoggedIn = sharedPref.getBoolean("userLoggedIn",false);
        Log.d(LOG_TAG,"Shared Preference Values Fetched");

        layout.setVisibility(View.INVISIBLE);

        if(initialSetup == false){
            if(userLoggedIn == true) {
                // check login status if true means mAuth is Null.
                if(authCheck(mAuth) == true) {
                    // Move to Main Screen
                    signInWithEmailAndPassword(userId,userPassword);
                }
                else {
                    // Move to Login Screen
                    rotateLoading.stop();
                    moveToMainActivity();
                }
            }
            else if(skipLogin == true) {
                // Move to main activity
                moveToMainActivity();
            }
            else{
                //show login/signup screen
                rotateLoading.stop();
                layout.setVisibility(View.VISIBLE);
                animate();
            }

        }
        else{
            rotateLoading.stop();
            layout.setVisibility(View.VISIBLE);
            animate();
        }

        login.setOnClickListener(v -> {
            // Start the loading animation when the user tap the button
            login.startAnimation();
            // Do your networking task or background work here.
            signInUser(username.getText().toString(),password.getText().toString(),flag);
        });

        signup.setOnClickListener(v -> {
            if(flag == 1) {
                login.setText("Sign Up");
                signup.setText("Already registered? Login");
                flag = 0;
            }else {
                login.setText("Login");
                signup.setText("Not registered yet? Sign up here");
                flag = 1;
            }
        });

        skip.setOnClickListener(v -> {
            editor.putBoolean("skipLogin",true);
            editor.putBoolean("initialSetup",false);
            editor.apply();
            moveToMainActivity();
        });

    }

    public boolean authCheck(FirebaseAuth mAuth){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(LOG_TAG,"User : " +user);
        return (user == null);
    }

    public void signInUser(String email,String password,int flag){
        // if flag == 1 then login with details else signup users
        if(flag == 1) {
            signInWithEmailAndPassword(email,password);
        }
        else {
            signUpWithEmailAndPassword(email,password);
        }
    }

    public void signInWithEmailAndPassword(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            editor.putString("userId",email);
                            editor.putString("userPassword",password);
                            editor.putBoolean("userLoggedIn",true);
                            editor.putBoolean("initialSetup",false);
                            editor.putBoolean("skipLogin",false);
                            editor.apply();
                            FirebaseUser user = mAuth.getCurrentUser();
                            login.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                                moveToMainActivity();
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            login.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }

                        // ...
                    }
                });
    }

    public void signUpWithEmailAndPassword(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "createUserWithEmail:success");
                            editor.putString("userId",email);
                            editor.putString("userPassword",password);
                            editor.putBoolean("userLoggedIn",true);
                            editor.putBoolean("initialSetup",false);
                            editor.putBoolean("skipLogin",false);
                            editor.apply();
                            FirebaseUser user = mAuth.getCurrentUser();
                            login.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                                moveToMainActivity();
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            login.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }

                        // ...
                    }
                });
    }

    public void animate(){
        ObjectAnimator loginAnimation = ObjectAnimator.ofFloat(login,"alpha",0f,1f);
        ObjectAnimator signUpAnimation = ObjectAnimator.ofFloat(signup,"alpha",0f,1f);
        ObjectAnimator profileImageAnimation = ObjectAnimator.ofFloat(imageView,"translationY",-400f);
        ObjectAnimator linearLayoutAnimation = ObjectAnimator.ofFloat(layout,"translationY",-400f);
        linearLayoutAnimation.setDuration(1500);
        linearLayoutAnimation.setInterpolator(new DecelerateInterpolator());
        linearLayoutAnimation.start();
        profileImageAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        profileImageAnimation.setDuration(1500);
        profileImageAnimation.start();
        loginAnimation.setDuration(1500);
        loginAnimation.start();
        signUpAnimation.setDuration(500);
        signUpAnimation.start();
    }

    public void moveToMainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
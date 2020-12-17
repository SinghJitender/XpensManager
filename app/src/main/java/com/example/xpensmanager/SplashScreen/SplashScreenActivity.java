package com.example.xpensmanager.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.xpensmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.victor.loading.rotate.RotateLoading;
import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private String LOG_TAG = "SplashScreen";
    private RotateLoading rotateLoading;
    private FirebaseAuth mAuth;
    private Button signup;
    private TransitionButton login;
    private LinearLayout layout;
    private CircleImageView imageView;

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
        layout.setVisibility(View.INVISIBLE);


        if(!rotateLoading.isStart())
            rotateLoading.start();
        if(authCheck(mAuth) == true) {
            // Move to Main Screen
            rotateLoading.stop();
            layout.setVisibility(View.VISIBLE);
            animate();

        }
        else {
            // Move to Login Screen
            //rotateLoading.stop();
        }
        login.setOnClickListener(v -> {
            // Start the loading animation when the user tap the button
            login.startAnimation();

            // Do your networking task or background work here.
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                boolean isSuccessful = false;

                // Choose a stop animation if your call was succesful or not
                if (isSuccessful) {
                    login.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, () -> {
                        /*Intent intent = new Intent(getBaseContext(), NewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);*/
                    });
                } else {
                    login.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                }
            }, 2000);
        });
        //varCheck();
        //rotateLoading.stop();


        //Intent i = new Intent(this, LoginActivity.class);
        //startActivity(i);
    }

    public boolean authCheck(FirebaseAuth mAuth){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(LOG_TAG,"User : " +user);
        return (user == null);
        //return false;
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
}
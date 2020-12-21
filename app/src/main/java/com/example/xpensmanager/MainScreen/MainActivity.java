package com.example.xpensmanager.MainScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xpensmanager.MainScreen.Adapters.RecyclerViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ArrayList<String> forecast = new ArrayList<>();
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");
        forecast.add("Today - Sunny - 88/63");
        forecast.add("Tomorrow - Foggy - 70/40");
        forecast.add("Weds - Cloudy  - 72/63");
        forecast.add("Thurs - Asteroids - 75/65");
        forecast.add("Fri - Heavy Rain - 65/56");
        forecast.add("Sat - HELP TRAPPED IN WEATHERSTATION - 60/51");
        forecast.add("Sun - Sunny - 80/68");



        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this,forecast);
        recyclerView.setAdapter(adapter);

    }
}
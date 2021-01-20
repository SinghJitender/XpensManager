package com.jitender.xpensmanager.MainScreen.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.CategoryData;
import com.jitender.xpensmanager.MainScreen.Adapters.CategoryViewAdapter;
import com.jitender.xpensmanager.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Category extends Fragment {
    private RecyclerView recyclerView;
    private static CategoryViewAdapter adapter;
    private static CategoryDB categoryDB;
    private static ArrayList<CategoryData> results;
    public static RelativeLayout emptyView;
    private static ArrayList<Boolean> list;
    private ExecutorService mExecutor;
    public static boolean categoryInitializationFlag;

    public Category() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryDB = new CategoryDB(getActivity());
        results = new ArrayList<>();
        list = new ArrayList<>();
        mExecutor = Executors.newSingleThreadExecutor();
        categoryInitializationFlag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);

       mExecutor.execute(()->{
           results.addAll(categoryDB.findAll());
           for(int i=0;i<results.size();i++){
               list.add(false);
           }
           getActivity().runOnUiThread(()->{
               adapter.notifyDataSetChanged();
               if(results.size() == 0) {
                   emptyView.setVisibility(View.VISIBLE);
               }else{
                   emptyView.setVisibility(View.GONE);
               }
           });
       });

        adapter = new CategoryViewAdapter(getActivity(), results, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public static void updateCategoryAdapter(ArrayList<CategoryData> updatedResults,ArrayList<Boolean> updateList){
        results.clear();
        results.addAll(updatedResults);
        list.clear();
        list.addAll(updateList);
        adapter.notifyDataSetChanged();
        if(results.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
    }
}
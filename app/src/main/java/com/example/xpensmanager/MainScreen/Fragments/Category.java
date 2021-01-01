package com.example.xpensmanager.MainScreen.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.MainScreen.Adapters.CategoryViewAdapter;
import com.example.xpensmanager.MainScreen.Adapters.GroupViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
import java.util.Hashtable;

public class Category extends Fragment {
    private RecyclerView recyclerView;
    private CategoryViewAdapter adapter;
    private CategoryDB categoryDB;
    ArrayList<CategoryData> results;
    private TextView emptyView;

    public Category() {
        // Required empty public constructor
    }

    public static Category newInstance(String param1, String param2) {
        Category fragment = new Category();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);

        categoryDB = new CategoryDB(getActivity());
        results = new ArrayList<>();
        results.addAll(categoryDB.findAll());

        if(results.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }
        ArrayList<Boolean> list = new ArrayList<>();
        for(int i=0;i<results.size();i++){
            list.add(false);
        }
        //((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new CategoryViewAdapter(getActivity(), results, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
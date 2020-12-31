package com.example.xpensmanager.MainScreen.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.MainScreen.Adapters.RecyclerViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
import java.util.Hashtable;

public class GroupsView extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private GroupDB groupDB;
    ArrayList<Hashtable<String,String>> results;
    public GroupsView() {
        // Required empty public constructor
    }

    public static GroupsView newInstance(String param1, String param2) {
        GroupsView fragment = new GroupsView();
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
        View view = inflater.inflate(R.layout.fragment_group_all_expense, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        groupDB = new GroupDB(getActivity());
        results = new ArrayList<>();
        results.addAll(groupDB.findAll());
        ArrayList<Boolean> list = new ArrayList<>();
        for(int i=0;i<results.size();i++){
            list.add(false);
        }
        //((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new RecyclerViewAdapter(getActivity(), results,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
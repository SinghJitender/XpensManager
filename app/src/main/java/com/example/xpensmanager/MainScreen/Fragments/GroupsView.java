package com.example.xpensmanager.MainScreen.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.MainScreen.Adapters.GroupViewAdapter;
import com.example.xpensmanager.R;

import java.util.ArrayList;
import java.util.Hashtable;

public class GroupsView extends Fragment {
    private RecyclerView recyclerView;
    private static GroupViewAdapter adapter;
    private static GroupDB groupDB;
    private TextView emptyView;
    private static ArrayList<Hashtable<String,String>> results;
    private static ArrayList<Boolean> list;

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
        emptyView = view.findViewById(R.id.emptyView);

        groupDB = new GroupDB(getActivity());
        results = new ArrayList<>();
        results.addAll(groupDB.findAll());


        if(results.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }

        list = new ArrayList<>();
        for(int i=0;i<results.size();i++){
            list.add(false);
        }
        //((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new GroupViewAdapter(getActivity(), results,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public static void updateGroupAdapter(){
        results.clear();
        results.addAll(groupDB.findAll());
        list.clear();
        for(int i=0;i<results.size();i++){
            list.add(false);
        }
        adapter.notifyDataSetChanged();
    }
}
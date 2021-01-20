package com.jitender.xpensmanager.MainScreen.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jitender.xpensmanager.Database.GroupDB;
import com.jitender.xpensmanager.Database.GroupData;
import com.jitender.xpensmanager.MainScreen.Adapters.GroupViewAdapter;
import com.jitender.xpensmanager.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Group extends Fragment {
    private RecyclerView recyclerView;
    private static GroupViewAdapter adapter;
    private static GroupDB groupDB;
    public static RelativeLayout emptyView;
    private static ArrayList<GroupData> results;
    private static ArrayList<Boolean> list;
    private ExecutorService mExecutor;
    public static boolean groupInitializationFlag;

    public Group() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupDB = new GroupDB(getActivity());
        results = new ArrayList<>();
        list = new ArrayList<>();
        mExecutor = Executors.newSingleThreadExecutor();
        groupInitializationFlag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_all_expense, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);

        mExecutor.execute(()-> {
            results.addAll(groupDB.findAll());
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

        adapter = new GroupViewAdapter(getActivity(), results, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public static void updateGroupAdapter(ArrayList<GroupData> updatedResults, ArrayList<Boolean> updateList){
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
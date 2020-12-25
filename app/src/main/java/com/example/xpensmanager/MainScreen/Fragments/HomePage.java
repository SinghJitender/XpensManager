package com.example.xpensmanager.MainScreen.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.xpensmanager.MainScreen.Adapters.RecyclerViewAdapter;
import com.example.xpensmanager.MainScreen.Adapters.SwipeController;
import com.example.xpensmanager.MainScreen.Adapters.SwipeControllerActions;
import com.example.xpensmanager.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {

    private RecyclerViewAdapter adapter;
    private ArrayList<String> titleData;
    private ArrayList<String> amountData;
    private ArrayList<String> netData;
    private ArrayList<Boolean> showM;
    private CardView createNewExpense, topCardView;
    private FrameLayout frameLayout, framelayoutTopView;

    private boolean toggle = true;

    SwipeController swipeController = null;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePage() {
    }

    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        createNewExpense = view.findViewById(R.id.createnewgroup);
        topCardView = view.findViewById(R.id.cardview);
        frameLayout = view.findViewById(R.id.framelayout);
        framelayoutTopView = view.findViewById(R.id.framelayouttopview);

        titleData = new ArrayList<>();
        amountData = new ArrayList<>();
        netData = new ArrayList<>();
        showM = new ArrayList<>();

        titleData.add("Own Expenses");
        titleData.add("Bangalore Flat Mates");
        titleData.add("Cricket Group");
        titleData.add("Delhi Friends");
        titleData.add("Trips");
        titleData.add("Own Expenses");
        titleData.add("Bangalore Flat Mates");
        titleData.add("Cricket Group");
        titleData.add("Delhi Friends");
        titleData.add("Trips");

        amountData.add("₹ 34700.98");
        amountData.add("₹ 23222.77");
        amountData.add("₹ 1244.00");
        amountData.add("₹ 76876767.72");
        amountData.add("₹ 3332.44");
        amountData.add("₹ 34700.98");
        amountData.add("₹ 23222.77");
        amountData.add("₹ 1244.00");
        amountData.add("₹ 76876767.72");
        amountData.add("₹ 3332.44");

        netData.add("- 9800.67");
        netData.add("+ 2333.78");
        netData.add("+ 45.11");
        netData.add("+ 234376.45");
        netData.add("- 453.00");
        netData.add("- 9800.67");
        netData.add("+ 2333.78");
        netData.add("+ 45.11");
        netData.add("+ 234376.45");
        netData.add("- 453.00");

        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);
        showM.add(false);

        adapter = new RecyclerViewAdapter(getActivity(), titleData, amountData, netData, showM);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.theme_light_grey));
            private boolean swipeBack = false;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT)
                    adapter.showMenu(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.RIGHT)
                    adapter.hideMenu(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                background.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_page_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add) {
            ActionMenuItemView add = getActivity().findViewById(R.id.add);
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(createNewExpense).addTarget(framelayoutTopView);
            TransitionManager.beginDelayedTransition(frameLayout, transition);
            createNewExpense.setVisibility(toggle ? View.VISIBLE : View.GONE);
            framelayoutTopView.setVisibility(!toggle ? View.VISIBLE : View.GONE);
            add.setIcon(toggle ? getActivity().getResources().getDrawable(R.drawable.cross) : getActivity().getResources().getDrawable(R.drawable.fab_white));
            toggle = !toggle;
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.example.xpensmanager.MainScreen.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.MediaRouteButton;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.MainScreen.Adapters.RecyclerViewAdapter;
import com.example.xpensmanager.MainScreen.Adapters.SwipeController;
import com.example.xpensmanager.MainScreen.Adapters.SwipeControllerActions;
import com.example.xpensmanager.R;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class HomePage extends Fragment {

    private RecyclerViewAdapter adapter;
    private static CardView createNewExpense;
    private CardView topCardView;
    private static CardView addNewExpenseView;
    private EditText newGroupTitle, newGroupNoOfPersons, newGroupLimit,newExpenseTotalAmount,newExpenseDescription;
    private Button createNewGroup,newExpenseAdd;
    private static FrameLayout frameLayout;
    private static FrameLayout framelayoutTopView;
    private static TextView addOwnExpense;
    private static TextView expenseTitle;
    private TextView newExpenseDate;
    private CheckBox newExpensePaidBy;
    private AutoCompleteTextView newExpenseSelectCategory;
    private ImageButton newExpenseCancel;
    ArrayList<Hashtable<String,String>> results;
    private ArrayList<String> category;
    private Calendar myCalendar;

    private static boolean toggle = true;
    private static boolean toggleNewExpense = true;

    SwipeController swipeController = null;

    GroupDB groups;
    GenericExpenseDB genericExpenseDB;

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
        newGroupTitle = view.findViewById(R.id.title);
        newGroupNoOfPersons = view.findViewById(R.id.noofpeps);
        newGroupLimit = view.findViewById(R.id.limit);
        createNewGroup = view.findViewById(R.id.create);
        addOwnExpense = view.findViewById(R.id.addOwnExpense);
        addNewExpenseView = view.findViewById(R.id.addNewExpenseView);
        newExpenseCancel = view.findViewById(R.id.newExpenseCancel);

        //Add New Expense Widgets
        expenseTitle= view.findViewById(R.id.addExpenseTitle);
        newExpenseDate = view.findViewById(R.id.newExpenseDate);
        newExpensePaidBy = view.findViewById(R.id.newExpensePaidBy);
        newExpenseSelectCategory =  view.findViewById(R.id.newExpenseSelectCategory);
        newExpenseTotalAmount= view.findViewById(R.id.newExpenseTotalAmount);
        newExpenseDescription= view.findViewById(R.id.newExpenseDescription);
        newExpenseAdd = view.findViewById(R.id.newExpenseAdd);

        category = new ArrayList<>();
        groups = new GroupDB(getActivity());
        results = new ArrayList<>();
        results.addAll(groups.findAll());
        myCalendar = Calendar.getInstance();

        category.add("DMART"); category.add("GROCERY"); category.add("BILLS"); category.add("INTERNET"); category.add("SHOPPING"); category.add("ESSENTIALS");
        category.add("BIG BAZAAR"); category.add("FOOD"); category.add("TRAVEL"); category.add("FUEL"); category.add("MOVIE"); category.add("DAIRY"); category.add("THIS IS A BIG SUGGESTION");
        category.add("DIG BAZAAR"); category.add("DOOD"); category.add("DRAVEL"); category.add("DUEL"); category.add("DOVIE"); category.add("DAIRY"); category.add("DHIS IS A BIG SUGGESTION");

        newExpenseDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        ArrayAdapter autoCompleteTextViewAdapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_item, category);
        newExpenseSelectCategory.setThreshold(1);//will start working from first character
        newExpenseSelectCategory.setAdapter(autoCompleteTextViewAdapter);//setting the adapter data into the AutoCompleteTextView

        createNewGroup.setOnClickListener((v)-> {
            String titleValue = newGroupTitle.getText().toString();
            String noOfPersons = newGroupNoOfPersons.getText().toString();
            String limit = newGroupLimit.getText().toString();
            if(titleValue == null || titleValue.equalsIgnoreCase("")) {
                newGroupTitle.setError("Field Cannot be blank");
            } else{
                if(noOfPersons == null || noOfPersons.equalsIgnoreCase("")){
                    newGroupNoOfPersons.setError("Field Cannot be blank");
                } else{
                    if(Integer.parseInt(noOfPersons)<=0){
                        newGroupNoOfPersons.setError("Cannot be 0 or less");
                    }
                    else {
                        if (limit == null || limit.equalsIgnoreCase("")) {
                            //Optional Field
                            //All Checks Completed
                            String result = groups.insertNewGroup(titleValue,Integer.parseInt(noOfPersons),-999);
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                            if(result.contains("Created")) {
                                newGroupTitle.setText("");
                                newGroupLimit.setText("");
                                newGroupNoOfPersons.setText("");
                                results.clear();
                                results.addAll(groups.findAll());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            if(Double.parseDouble(limit)<=0.0){
                                newGroupLimit.setError("Cannot be 0 or less");
                            }else {
                                //All Checks Completed
                                String result = groups.insertNewGroup(titleValue,Integer.parseInt(noOfPersons),Double.parseDouble(limit));
                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                if(result.contains("Created")) {
                                    newGroupTitle.setText("");
                                    newGroupLimit.setText("");
                                    newGroupNoOfPersons.setText("");
                                    results.clear();
                                    results.addAll(groups.findAll());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        });

        addOwnExpense.setOnClickListener((v) -> {
            mainLayoutToAddExpenseTransition();
            update_add_new_expense_title("Self Expense",false);
        });

        newExpenseCancel.setOnClickListener((v) -> {
            mainLayoutToAddExpenseTransition();
        });

        newExpenseAdd.setOnClickListener((v) -> {
            //Toast.makeText(getActivity(),expenseTitle.getText().toString(),Toast.LENGTH_SHORT).show();
            String tableName = expenseTitle.getText().toString().replaceAll(" ","_").toLowerCase();
            String paidBy = "others";
            if(newExpenseTotalAmount.getText().toString().equalsIgnoreCase("") || newExpenseTotalAmount.getText() == null) {
                newExpenseTotalAmount.setError("Amount cannot be blank");
            }else{
                if(Double.parseDouble(newExpenseTotalAmount.getText().toString()) <= 0) {
                    newExpenseTotalAmount.setError("Amount cannot be 0 or less");
                }else{
                    if(newExpenseDescription.getText().toString().equalsIgnoreCase("") || newExpenseDescription.getText() == null){
                        newExpenseDescription.setError("Description cannot be blank");
                    }else {
                        if(newExpenseSelectCategory.getText().toString().equalsIgnoreCase("") || newExpenseSelectCategory.getText() == null) {
                            newExpenseSelectCategory.setError("Category cannot be blank");
                        }else{
                            if(!category.contains(newExpenseSelectCategory.getText().toString().toUpperCase().trim()) ){
                                newExpenseSelectCategory.setError("Category must be from dropdown-list");
                            }else{
                                //All Checks Done
                                if(newExpensePaidBy.isChecked()){
                                    paidBy = "self";
                                }
                                try {
                                    Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(newExpenseDate.getText().toString());
                                    genericExpenseDB = new GenericExpenseDB(getActivity(),tableName);
                                    genericExpenseDB.insertNewExpense(date,Double.parseDouble(newExpenseTotalAmount.getText().toString()),newExpenseDescription.getText().toString(),newExpenseSelectCategory.getText().toString().toUpperCase().trim(),paidBy,1);
                                    newExpenseTotalAmount.setText("");
                                    newExpenseSelectCategory.setText("");
                                    newExpenseDescription.setText("");
                                    newExpenseTotalAmount.requestFocus();
                                } catch (ParseException e) {
                                    Toast.makeText(getActivity(),"Error in parsing date",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }


        });

        DatePickerDialog.OnDateSetListener datePickerDialogue = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        newExpenseDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog mDatePickerDialogue = new DatePickerDialog(getActivity(), datePickerDialogue, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialogue.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePickerDialogue.show();
            }
        });

        adapter = new RecyclerViewAdapter(getActivity(), results);
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

    private static void mainLayoutToAddExpenseTransition(){
        Transition transition = new Slide(Gravity.RIGHT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(framelayoutTopView);
        TransitionManager.beginDelayedTransition(frameLayout, transition);
        addNewExpenseView.setVisibility(toggleNewExpense ? View.VISIBLE : View.GONE);
        framelayoutTopView.setVisibility(!toggleNewExpense ? View.VISIBLE : View.GONE);
        toggleNewExpense = !toggleNewExpense;
    }


    public static void update_add_new_expense_title(String text,boolean comingFromAdapterView){
        expenseTitle.setText(text);
        if(comingFromAdapterView && toggleNewExpense){
            mainLayoutToAddExpenseTransition();
        }
    }

    void updateLabel(){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        newExpenseDate.setText(sdf.format(myCalendar.getTime()));
    }
}
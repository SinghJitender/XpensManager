package com.example.xpensmanager.MainScreen;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Database.GroupData;
import com.example.xpensmanager.MainScreen.Fragments.Category;
import com.example.xpensmanager.MainScreen.Fragments.Group;
import com.example.xpensmanager.MainScreen.Fragments.Home;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton main;
    private ExtendedFloatingActionButton add,group,category;
    private CardView addView,createnewcategory,createnewgroup,addNewExpenseView;
    private LinearLayout fabHolder;
    private CoordinatorLayout CoordinatorLayout;
    private CheckBox newExpensePaidBy;
    private TextView newExpenseSelectCategory,newExpenseSelectGroup;
    private ImageButton newExpenseCancel;
    private static TextView expenseTitle;
    private TextView newExpenseDate;
    private Button newExpenseAdd;
    private EditText newExpenseTotalAmount,newExpenseDescription;
    private Calendar myCalendar;
    private static ArrayList<String> groupList;
    private static ArrayList<String> categoryList;
    private boolean homePageFlag, groupPageFlag, categoryPageFlag;
    private int totalTimes;

    //Database Objects
    private ExpenseDB expenseDB;
    private static GroupDB groupsDB;
    private static CategoryDB categoryDB;

    //Create New Group
    private EditText newGroupTitle,newGroupNoOfPersons,newGroupLimit;
    private Button createNewGroupButton;

    //Animation toggles
    private boolean isFABOpen = true;
    private boolean isAddViewOpen = true;

    //Create New Category
    private EditText categoryName,categoryLimit;
    private Button createCategory;

    private static ExecutorService mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExecutor = Executors.newCachedThreadPool();
        totalTimes = 0;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        main = findViewById(R.id.fab);
        add = findViewById(R.id.expense);
        group = findViewById(R.id.group);
        category = findViewById(R.id.category);
        fabHolder = findViewById(R.id.fabHolder);
        addView = findViewById(R.id.addView);
        CoordinatorLayout = findViewById(R.id.container);
        addNewExpenseView = findViewById(R.id.addNewExpenseView);
        createnewgroup = findViewById(R.id.createnewgroup);
        createnewcategory = findViewById(R.id.createnewcategory);

        //DB initialization
        expenseDB = new ExpenseDB(getApplicationContext());
        groupsDB = new GroupDB(getApplicationContext());
        categoryDB = new CategoryDB(getApplicationContext());
        mExecutor.execute(()->{
            categoryList = categoryDB.findAllCategories();
            groupList = groupsDB.findAllGroups();
        });

        // Add new expense
        expenseTitle= findViewById(R.id.addExpenseTitle);

        newExpenseDate = findViewById(R.id.newExpenseDate);
        newExpensePaidBy = findViewById(R.id.newExpensePaidBy);
        newExpenseSelectCategory = findViewById(R.id.newExpenseSelectCategory);
        newExpenseTotalAmount= findViewById(R.id.newExpenseTotalAmount);
        newExpenseDescription= findViewById(R.id.newExpenseDescription);
        newExpenseAdd = findViewById(R.id.newExpenseAdd);
        newExpenseSelectGroup = findViewById(R.id.newExpenseSelectGroup);
        myCalendar = Calendar.getInstance();
        newExpenseTotalAmount.setHint(SplashScreenActivity.cSymbol+" Amount");
        newExpenseSelectCategory.setOnClickListener((v)->{
            displayCategoryDialogBox(categoryList);
        });

        newExpenseSelectGroup.setOnClickListener((v)->{
            displayGroupDialogBox(groupList);
        });

        newExpenseDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        newExpenseAdd.setOnClickListener((v) -> {
            //Toast.makeText(getActivity(),expenseTitle.getText().toString(),Toast.LENGTH_SHORT).show();
            String tableName = expenseTitle.getText().toString().replaceAll(" ","_").toLowerCase();
            String paidBy;
            if(newExpenseTotalAmount.getText().toString().equalsIgnoreCase("") || newExpenseTotalAmount.getText() == null) {
                newExpenseTotalAmount.setError("Amount cannot be blank");
            }else{
                if(Double.parseDouble(newExpenseTotalAmount.getText().toString()) <= 0) {
                    newExpenseTotalAmount.setError("Amount cannot be 0 or less");
                }else{
                    if(newExpenseDescription.getText().toString().equalsIgnoreCase("") || newExpenseDescription.getText() == null){
                        newExpenseDescription.setError("Description cannot be blank");
                    }else {
                        if(newExpenseSelectCategory.getText().toString().equalsIgnoreCase("Select Category") || newExpenseSelectCategory.getText() == null) {
                            newExpenseSelectCategory.setError("Select Category");
                        }else{
                            if(!categoryList.contains(newExpenseSelectCategory.getText().toString().trim()) ){
                                newExpenseSelectCategory.setError("Category must be from dropdown-list");
                            }else{
                                if(newExpenseSelectGroup.getText().toString().equalsIgnoreCase("Select Group") || newExpenseSelectGroup.getText() == null) {
                                    newExpenseSelectGroup.setError("Select Group");
                                }else{
                                    if(!groupList.contains(newExpenseSelectGroup.getText().toString().trim()) ){
                                        newExpenseSelectGroup.setError("Group must be from dropdown-list");
                                    }
                                    else{
                                        //All Checks Done
                                        if(newExpensePaidBy.isChecked() || newExpensePaidBy.isSelected() || newExpensePaidBy.isActivated()){
                                            paidBy = "Me";
                                        }else{
                                            paidBy = "Others";
                                        }
                                        try {
                                            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(newExpenseDate.getText().toString());
                                            double tempAmount = Double.parseDouble(newExpenseTotalAmount.getText().toString());
                                            int splitBetween = groupsDB.findSplitBetween(newExpenseSelectGroup.getText().toString().trim());
                                            double netAmount = groupsDB.getNetAmountByTitle(newExpenseSelectGroup.getText().toString().trim());
                                            double totalAmount = groupsDB.getTotalAmountByTitle(newExpenseSelectGroup.getText().toString().trim());
                                            double totalCategoryAmount = categoryDB.getTotalAmountByTitle(newExpenseSelectCategory.getText().toString().trim());
                                            totalAmount = totalAmount + tempAmount;
                                            if(paidBy.equalsIgnoreCase("Me")) {
                                                netAmount = netAmount + (tempAmount - ((tempAmount)/splitBetween));
                                            }else{
                                                netAmount = netAmount - ((tempAmount)/splitBetween);
                                            }
                                            groupsDB.updateGroupAmountByTitle(newExpenseSelectGroup.getText().toString().trim(),netAmount,totalAmount);
                                            categoryDB.updateCategoryAmountByTitle(newExpenseSelectCategory.getText().toString().trim(),(totalCategoryAmount+(tempAmount/splitBetween)));
                                            expenseDB.insertNewExpense(date,Double.parseDouble(newExpenseTotalAmount.getText().toString()),newExpenseDescription.getText().toString(),
                                                    newExpenseSelectCategory.getText().toString().trim(),paidBy,splitBetween,newExpenseSelectGroup.getText().toString());
                                            newExpenseTotalAmount.setText("");
                                            newExpenseSelectCategory.setText("Select Category");
                                            newExpenseDescription.setText("");
                                            newExpenseTotalAmount.requestFocus();
                                            newExpenseSelectGroup.setText("Select Group");
                                            updateHomePageData();
                                            updateGroupFragmentData();
                                            updateCategoryFragmentData();

                                        } catch (ParseException e) {
                                            Toast.makeText(getApplicationContext(),"Error in parsing date",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        });

        newExpenseDate.setOnClickListener((v) -> {
            DatePickerDialog mDatePickerDialogue = new DatePickerDialog(MainActivity.this, datePickerDialogue, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePickerDialogue.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePickerDialogue.show();
        });

        //new group
        newGroupTitle = findViewById(R.id.title);
        newGroupNoOfPersons = findViewById(R.id.noofpeps);
        newGroupLimit = findViewById(R.id.limit);
        createNewGroupButton = findViewById(R.id.create);
        createNewGroupButton.setOnClickListener((v) -> {
            String titleValue = newGroupTitle.getText().toString();
            String noOfPersons = newGroupNoOfPersons.getText().toString();
            String limit = newGroupLimit.getText().toString();
            if(titleValue == null || titleValue.equalsIgnoreCase("")) {
                newGroupTitle.setError("Field Cannot be blank");
            } else{
                if(noOfPersons == null || noOfPersons.equalsIgnoreCase("")){
                    newGroupNoOfPersons.setError("Field Cannot be blank");
                } else{
                    if (Integer.parseInt(noOfPersons)<=0) {
                        newGroupNoOfPersons.setError("Cannot be 0 or less");
                    }
                    else {
                        if(limit == null || limit.equalsIgnoreCase("")){
                            newGroupLimit.setError("Field Cannot be blank");
                        }
                        else {
                            if(Double.parseDouble(limit)<=0.0){
                                newGroupLimit.setError("Cannot be 0 or less");
                            }else {
                                //All Checks Completed
                                String result = groupsDB.insertNewGroup(titleValue,Integer.parseInt(noOfPersons),Double.parseDouble(limit));
                                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                if(result.contains("Created")) {
                                    newGroupTitle.setText("");
                                    newGroupLimit.setText("");
                                    newGroupNoOfPersons.setText("");
                                    newGroupTitle.requestFocus();
                                    groupList.add(titleValue);
                                    updateGroupFragmentData();
                                }
                            }
                        }
                    }
                }
            }
        });

        //new category
        categoryName = findViewById(R.id.categoryName);
        createCategory = findViewById(R.id.createCategory);
        categoryLimit = findViewById(R.id.categoryLimit);
        createCategory.setOnClickListener((v) -> {
            String categoryValue = categoryName.getText().toString();
            String categoryLimitVal = categoryLimit.getText().toString();
            if(categoryValue == null || categoryValue.equalsIgnoreCase("")) {
                categoryName.setError("Field Cannot be blank");
            } else {
                if(categoryLimitVal == null || categoryLimitVal.equalsIgnoreCase("")) {
                    categoryLimit.setError("Field Cannot be blank");
                }else{
                    if(Double.parseDouble(categoryLimitVal) <= 0.0){
                        categoryLimit.setError("Cannot be 0 or less");
                    }
                    else{
                        String result = categoryDB.insertNewCategory(categoryValue,Double.parseDouble(categoryLimitVal));
                        //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                        if(result.contains("Created")) {
                            categoryName.setText("");
                            categoryName.requestFocus();
                            categoryLimit.setText("");
                            categoryList.add(categoryValue);
                            updateCategoryFragmentData();
                        }
                    }
                }
            }
        });

        main.setOnClickListener((v)->{
            toggleFabMenu();
        });

        add.setOnClickListener((v)->{
            toggleExpenseView();
        });

        group.setOnClickListener((v)->{
            toggleGroupView();
        });

        category.setOnClickListener((v)->{
            toggleCategoryView();
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_group,R.id.navigation_category,R.id.navigation_stats, R.id.navigation_settings)
                .build();
        BottomAppBar.LayoutParams layoutParams = (BottomAppBar.LayoutParams) navView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationBehavior());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onBackPressed() {
        if(totalTimes == 0){
            totalTimes++;
            Toast.makeText(getApplicationContext(),"Press again to exit",Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
        super.onBackPressed();
    }

    DatePickerDialog.OnDateSetListener datePickerDialogue = (view1, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    public void displayCategoryDialogBox(ArrayList<String> list){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(list.toArray(new String[list.size()]), (dialog, which) -> {
            newExpenseSelectCategory.setText(list.get(which));
            newExpenseSelectCategory.setError(null);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayGroupDialogBox(ArrayList<String> list){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Group");
        builder.setItems(list.toArray(new String[list.size()]), (dialog, which) -> {
            newExpenseSelectGroup.setText(list.get(which));
            newExpenseSelectGroup.setError(null);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleFabMenu(){
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(200);
        transition.addTarget(add).addTarget(group).addTarget(category);
        TransitionManager.beginDelayedTransition(fabHolder, transition);
        add.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        group.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        category.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        addNewExpenseView.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        toggleAddView();
        isFABOpen=!isFABOpen;
    }

    private void toggleExpenseView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.VISIBLE);
        createnewgroup.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.INVISIBLE);
        newExpenseTotalAmount.requestFocus();
    }

    private void toggleGroupView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.INVISIBLE);
        createnewgroup.setVisibility(View.VISIBLE);
        createnewcategory.setVisibility(View.INVISIBLE);
        newGroupTitle.requestFocus();
    }

    private void toggleCategoryView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.INVISIBLE);
        createnewgroup.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.VISIBLE);
        categoryName.requestFocus();
    }

    private void toggleAddView(){
        Transition transition = new Slide(Gravity.TOP);
        transition.setDuration(200);
        transition.addTarget(addView);
        TransitionManager.beginDelayedTransition(CoordinatorLayout, transition);
        addView.setVisibility(isAddViewOpen?View.VISIBLE:View.INVISIBLE);
        isAddViewOpen=!isAddViewOpen;
    }

    void updateLabel(){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        newExpenseDate.setText(sdf.format(myCalendar.getTime()));
    }

    public static void updateCategoryList(){
        mExecutor.execute(()-> {
            categoryList.clear();
            categoryList.addAll(categoryDB.findAllCategories());
        });
    }

    public static void updateGroupList(){
        mExecutor.execute(()-> {
            groupList.clear();
            groupList.addAll(groupsDB.findAllGroups());
        });
    }

    public void updateHomePageData() {
        if(Home.homeInitializationFlag) {
            ArrayList<ExpenseData> data = new ArrayList<>();
            data.addAll(expenseDB.findByMonth(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date())));
            double totalSpentThisMonth = expenseDB.getMonthlyExpenseSum(ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
            double totalCategorySum = categoryDB.getTotalCategoryLimitSum();
            Home.updateRecyclerView(data, totalSpentThisMonth, totalCategorySum);
        }
    }

    public void updateGroupFragmentData(){
        if(Group.groupInitializationFlag) {
            ArrayList<GroupData> updatedResults = new ArrayList<>();
            ArrayList<Boolean> updateList = new ArrayList<>();
            updatedResults.addAll(groupsDB.findAll());
            for (int i = 0; i < updatedResults.size(); i++) {
                updateList.add(false);
            }
            Group.updateGroupAdapter(updatedResults, updateList);
        }
    }

    public void updateCategoryFragmentData(){
        if(Category.categoryInitializationFlag) {
            ArrayList<CategoryData> updatedResults = new ArrayList<>();
            ArrayList<Boolean> updateList = new ArrayList<>();
            updatedResults.addAll(categoryDB.findAll());
            for (int i = 0; i < updatedResults.size(); i++) {
                updateList.add(false);
            }
            Category.updateCategoryAdapter(updatedResults, updateList);
        }
    }

}
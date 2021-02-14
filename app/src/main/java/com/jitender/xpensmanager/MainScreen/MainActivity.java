package com.jitender.xpensmanager.MainScreen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.CategoryData;
import com.jitender.xpensmanager.Database.ExpenseData;
import com.jitender.xpensmanager.Database.ExpenseDB;
import com.jitender.xpensmanager.Database.GroupDB;
import com.jitender.xpensmanager.Database.GroupData;
import com.jitender.xpensmanager.Database.PaymentsDB;
import com.jitender.xpensmanager.Database.PaymentsData;
import com.jitender.xpensmanager.MainScreen.Fragments.Category;
import com.jitender.xpensmanager.MainScreen.Fragments.Group;
import com.jitender.xpensmanager.MainScreen.Fragments.Home;
import com.jitender.xpensmanager.MainScreen.Fragments.Payment;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton main;
    private ExtendedFloatingActionButton add,group,category,mode;
    private CardView addView,createnewcategory,createnewgroup,addNewExpenseView,createnewpaymentmode;
    private LinearLayout fabHolder;
    private CoordinatorLayout CoordinatorLayout;
    private CheckBox newExpensePaidBy;
    private TextView newExpenseSelectCategory,newExpenseSelectGroup,newExpenseSelectMode;
    private ImageButton newExpenseCancel;
    private static TextView expenseTitle;
    private TextView newExpenseDate;
    private Button newExpenseAdd;
    private EditText newExpenseTotalAmount,newExpenseDescription;
    private Calendar myCalendar;
    private static ArrayList<String> groupList;
    private static ArrayList<String> categoryList;
    private static ArrayList<String> paymentList;
    private boolean homePageFlag, groupPageFlag, categoryPageFlag;
    private int totalTimes;

    //Database Objects
    private ExpenseDB expenseDB;
    private static GroupDB groupsDB;
    private static CategoryDB categoryDB;
    private static PaymentsDB paymentsDB;

    //Create New Group
    private EditText newGroupTitle,newGroupNoOfPersons,newGroupLimit;
    private Button createNewGroupButton;

    //Animation toggles
    private boolean isFABOpen = true;
    private boolean isAddViewOpen = true;

    //Create New Category
    private EditText categoryName,categoryLimit;
    private Button createCategory;

    //create new payment mode
    private EditText modeName, modeLimit;
    private Button createPaymentMode;

    private static ExecutorService mExecutor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean isMainPageIntroShown;

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
        mode = findViewById(R.id.mode);
        fabHolder = findViewById(R.id.fabHolder);
        addView = findViewById(R.id.addView);
        CoordinatorLayout = findViewById(R.id.container);
        addNewExpenseView = findViewById(R.id.addNewExpenseView);
        createnewgroup = findViewById(R.id.createnewgroup);
        createnewcategory = findViewById(R.id.createnewcategory);
        createnewpaymentmode = findViewById(R.id.createnewpaymentmode);

        //Shared Preference Initialization
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMainPageIntroShown = sharedPreferences.getBoolean("mainPageIntro",false);


        //DB initialization
        expenseDB = new ExpenseDB(getApplicationContext());
        groupsDB = new GroupDB(getApplicationContext());
        categoryDB = new CategoryDB(getApplicationContext());
        paymentsDB = new PaymentsDB(getApplicationContext());

        mExecutor.execute(()->{
            categoryList = categoryDB.findAllCategories();
            groupList = groupsDB.findAllGroups();
            paymentList = paymentsDB.findAllPaymentModes();
        });

        if(!isMainPageIntroShown)
            showBottomNavigationIntro("Bottom Navigation View", "Tapping on each item will navigate you to different tabs", R.id.nav_view, 1);

        // Add new expense
        expenseTitle= findViewById(R.id.addExpenseTitle);

        newExpenseDate = findViewById(R.id.newExpenseDate);
        newExpensePaidBy = findViewById(R.id.newExpensePaidBy);
        newExpenseSelectCategory = findViewById(R.id.newExpenseSelectCategory);
        newExpenseTotalAmount= findViewById(R.id.newExpenseTotalAmount);
        newExpenseDescription= findViewById(R.id.newExpenseDescription);
        newExpenseAdd = findViewById(R.id.newExpenseAdd);
        newExpenseSelectGroup = findViewById(R.id.newExpenseSelectGroup);
        newExpenseSelectMode = findViewById(R.id.newExpenseSelectMode);

        myCalendar = Calendar.getInstance();
        newExpenseTotalAmount.setHint(SplashScreenActivity.cSymbol+" Amount");
        newExpenseSelectCategory.setOnClickListener((v)->{
            if(categoryList.isEmpty()){
                new AlertDialog.Builder(this)
                        //.setTitle("Message")
                        .setMessage("Please add at-least one category to proceed")
                        .setPositiveButton("Add", (dialog,which)->{
                            toggleCategoryView();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Close", null)
                        //.setIcon(getResources().getDrawable(R.drawable.info))
                        .show();
            }
            else {
                displayCategoryDialogBox(categoryList);
            }
        });

        newExpenseSelectMode.setOnClickListener((v)->{
            if(paymentList.isEmpty()){
                new AlertDialog.Builder(this)
                        //.setTitle("Message")
                        .setMessage("Please add at-least one mode of payment to proceed")
                        .setPositiveButton("Add", (dialog,which)->{
                            togglePaymentView();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Close", null)
                        //.setIcon(getResources().getDrawable(R.drawable.info))
                        .show();
            }
            else {
                displayPaymentModeDialogBox(paymentList);
            }
        });

        newExpenseSelectGroup.setOnClickListener((v)->{
            if(groupList.isEmpty()){
                new AlertDialog.Builder(this)
                        //.setTitle("Message")
                        .setMessage("Please add at-least one group to proceed")
                        .setPositiveButton("Add", (dialog,which)->{
                            toggleGroupView();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Close", null)
                        //.setIcon(getResources().getDrawable(R.drawable.info))
                        .show();
            }
            else {
                displayGroupDialogBox(groupList);
            }
        });

        newExpenseDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        newExpenseAdd.setOnClickListener((v) -> {
            //Toast.makeText(getActivity(),expenseTitle.getText().toString(),Toast.LENGTH_SHORT).show();
            String tableName = expenseTitle.getText().toString().replaceAll(" ","_").toLowerCase();
            String paidBy;
            if(newExpenseTotalAmount.getText().toString().equalsIgnoreCase("") || newExpenseTotalAmount.getText() == null) {
                newExpenseTotalAmount.setError("Amount cannot be blank");
            }else{
                if(newExpenseTotalAmount.getText().toString().equalsIgnoreCase(".")){
                    newExpenseTotalAmount.setError("Enter valid amount");
                }else {
                    if(Double.parseDouble(newExpenseTotalAmount.getText().toString()) <= 0  ) {
                        newExpenseTotalAmount.setError("Amount cannot be 0 or less");
                    }else{
                        if(newExpenseDescription.getText().toString().equalsIgnoreCase("") || newExpenseDescription.getText() == null){
                            newExpenseDescription.setError("Description cannot be blank");
                        }else {
                            if(newExpenseSelectCategory.getText().toString().equalsIgnoreCase("Category") || newExpenseSelectCategory.getText() == null) {
                                newExpenseSelectCategory.setError("Select Category");
                            }else{
                                if(!categoryList.contains(newExpenseSelectCategory.getText().toString().trim()) ){
                                    newExpenseSelectCategory.setError("Category must be from dropdown-list");
                                }else{
                                    if(newExpenseSelectGroup.getText().toString().equalsIgnoreCase("Group") || newExpenseSelectGroup.getText() == null) {
                                        newExpenseSelectGroup.setError("Select Group");
                                    }else{
                                        if(!groupList.contains(newExpenseSelectGroup.getText().toString().trim()) ){
                                            newExpenseSelectGroup.setError("Group must be from dropdown-list");
                                        }
                                        else{
                                            if(newExpenseSelectMode.getText().toString().equalsIgnoreCase("Payment Mode") || newExpenseSelectMode.getText() == null) {
                                                newExpenseSelectMode.setError("Select Payment Mode");
                                            }else {
                                                if (!paymentList.contains(newExpenseSelectMode.getText().toString().trim())) {
                                                    newExpenseSelectMode.setError("Payment Mode must be from dropdown-list");
                                                } else {
                                                    //All Checks Done
                                                    if (newExpensePaidBy.isChecked() || newExpensePaidBy.isSelected() || newExpensePaidBy.isActivated()) {
                                                        paidBy = "Me";
                                                    } else {
                                                        paidBy = "Others";
                                                    }
                                                    try {
                                                        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(newExpenseDate.getText().toString());
                                                        double tempAmount = Double.parseDouble(newExpenseTotalAmount.getText().toString());
                                                        int splitBetween = groupsDB.findSplitBetween(newExpenseSelectGroup.getText().toString().trim());
                                                        double netAmount = groupsDB.getNetAmountByTitle(newExpenseSelectGroup.getText().toString().trim());
                                                        double totalAmount = groupsDB.getTotalAmountByTitle(newExpenseSelectGroup.getText().toString().trim());
                                                        double totalCategoryAmount = categoryDB.getTotalAmountByTitle(newExpenseSelectCategory.getText().toString().trim());
                                                        double totalModeAmount = paymentsDB.getTotalAmountByMode(newExpenseSelectMode.getText().toString().trim());
                                                        double settlementAmount = 0;
                                                        String settled = "false";
                                                        totalAmount = totalAmount + tempAmount;
                                                        if (paidBy.equalsIgnoreCase("Me")) {
                                                            netAmount = netAmount + (tempAmount - ((tempAmount) / splitBetween));
                                                            settlementAmount = (tempAmount - ((tempAmount) / splitBetween));
                                                        } else {
                                                            netAmount = netAmount - ((tempAmount) / splitBetween);
                                                            settlementAmount = ((tempAmount) / splitBetween);
                                                        }
                                                        if(newExpenseSelectGroup.getText().toString().trim().equalsIgnoreCase("Personal")){
                                                            settled = "true";
                                                        }
                                                        paymentsDB.updatePaymentAmountByMode(newExpenseSelectMode.getText().toString().trim(),(totalModeAmount + (tempAmount / splitBetween)));
                                                        groupsDB.updateGroupAmountByTitle(newExpenseSelectGroup.getText().toString().trim(), netAmount, totalAmount);
                                                        categoryDB.updateCategoryAmountByTitle(newExpenseSelectCategory.getText().toString().trim(), (totalCategoryAmount + (tempAmount / splitBetween)));
                                                        String result = expenseDB.insertNewExpense(date, Double.parseDouble(newExpenseTotalAmount.getText().toString()), newExpenseDescription.getText().toString(),
                                                                newExpenseSelectCategory.getText().toString().trim(), paidBy, splitBetween, newExpenseSelectGroup.getText().toString().trim(),
                                                                newExpenseSelectMode.getText().toString().trim(),settled,settlementAmount);
                                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                                        if(result.contains("Created")) {
                                                            newExpenseTotalAmount.setText("");
                                                            newExpenseSelectCategory.setText("Category");
                                                            newExpenseDescription.setText("");
                                                            newExpenseTotalAmount.requestFocus();
                                                            newExpenseSelectGroup.setText("Group");
                                                            newExpenseSelectMode.setText("Payment Mode");
                                                            newExpensePaidBy.setEnabled(true);
                                                            updateHomePageData();
                                                            updateGroupFragmentData();
                                                            updateCategoryFragmentData();
                                                            updatePaymentFragmentData();
                                                        }

                                                    } catch (ParseException e) {
                                                        Toast.makeText(getApplicationContext(), "Error in parsing date", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
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
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
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

        modeName = findViewById(R.id.modeName);
        createPaymentMode = findViewById(R.id.createPaymentMode);
        modeLimit = findViewById(R.id.modeLimit);
        createPaymentMode.setOnClickListener((v) -> {
            String modeValue = modeName.getText().toString();
            String modeLimitVal = modeLimit.getText().toString();
            if(modeValue == null || modeValue.equalsIgnoreCase("")) {
                modeName.setError("Field Cannot be blank");
            } else {
                if(modeLimitVal == null || modeLimitVal.equalsIgnoreCase("")) {
                    modeLimit.setError("Field Cannot be blank");
                }else{
                    if(Double.parseDouble(modeLimitVal) <= 0.0){
                        modeLimit.setError("Cannot be 0 or less");
                    }
                    else{
                        String result = paymentsDB.insertNewPaymentMode(modeValue,Double.parseDouble(modeLimitVal));
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                        if(result.contains("Created")) {
                            modeName.setText("");
                            modeName.requestFocus();
                            modeLimit.setText("");
                            paymentList.add(modeValue);
                            updatePaymentFragmentData();
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

        mode.setOnClickListener((v)->{
            togglePaymentView();
        });

        category.setOnClickListener((v)->{
            toggleCategoryView();
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_group,R.id.navigation_category,R.id.navigation_payment,R.id.navigation_stats, R.id.navigation_settings)
                .build();
        BottomAppBar.LayoutParams layoutParams = (BottomAppBar.LayoutParams) navView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationBehavior());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onBackPressed() {
        if(!isFABOpen) {
            toggleFabMenu();
            totalTimes = 0;
            return;
        }else {
            if (totalTimes == 0) {
                totalTimes++;
                Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                return;
            }
            finish();
        }
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

    public void displayPaymentModeDialogBox(ArrayList<String> list){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Payment Mode");
        builder.setItems(list.toArray(new String[list.size()]), (dialog, which) -> {
            newExpenseSelectMode.setText(list.get(which));
            newExpenseSelectMode.setError(null);
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
            if(list.get(which).equalsIgnoreCase("Personal")) {
                newExpensePaidBy.setChecked(true);
                newExpensePaidBy.setEnabled(false);
            }else{
                newExpensePaidBy.setEnabled(true);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleFabMenu(){
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(200);
        transition.addTarget(add).addTarget(group).addTarget(category).addTarget(mode);
        TransitionManager.beginDelayedTransition(fabHolder, transition);
        add.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        group.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        category.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        addNewExpenseView.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        mode.setVisibility(isFABOpen?View.VISIBLE:View.INVISIBLE);
        main.setImageDrawable(!isFABOpen ? getResources().getDrawable(R.drawable.menu) : getResources().getDrawable(R.drawable.close));
        toggleAddView();
        isFABOpen=!isFABOpen;
    }

    private void toggleExpenseView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory).addTarget(createnewpaymentmode);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.VISIBLE);
        createnewgroup.setVisibility(View.INVISIBLE);
        createnewpaymentmode.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.INVISIBLE);
        newExpenseTotalAmount.requestFocus();
    }

    private void toggleGroupView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory).addTarget(createnewpaymentmode);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.INVISIBLE);
        createnewgroup.setVisibility(View.VISIBLE);
        createnewpaymentmode.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.INVISIBLE);
        newGroupTitle.requestFocus();
    }

    private void toggleCategoryView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory).addTarget(createnewpaymentmode);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.INVISIBLE);
        createnewgroup.setVisibility(View.INVISIBLE);
        createnewpaymentmode.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.VISIBLE);
        categoryName.requestFocus();
    }

    private void togglePaymentView(){
        Transition transition = new Slide(Gravity.LEFT);
        transition.setDuration(200);
        transition.addTarget(addNewExpenseView).addTarget(createnewgroup).addTarget(createnewcategory).addTarget(createnewpaymentmode);
        TransitionManager.beginDelayedTransition(addView, transition);
        addNewExpenseView.setVisibility(View.INVISIBLE);
        createnewgroup.setVisibility(View.INVISIBLE);
        createnewcategory.setVisibility(View.INVISIBLE);
        createnewpaymentmode.setVisibility(View.VISIBLE);
        modeName.requestFocus();
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

    public void updatePaymentFragmentData() {
        if (Payment.paymentInitializationFlag) {
            ArrayList<PaymentsData> updatedResults = new ArrayList<>();
            ArrayList<Boolean> updateList = new ArrayList<>();
            updatedResults.addAll(paymentsDB.findAll());
            for (int i = 0; i < updatedResults.size(); i++) {
                updateList.add(false);
            }
            Payment.updatePaymentAdapter(updatedResults, updateList);
        }
    }

    private void showBottomNavigationIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(this)
                .setTitle(title)
                .setContentText(text)
                .setTargetView((BottomNavigationView)findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showBottomFabIntro("Tap to view all the options","Add new expense or group or category or payment mode from here.",1);
                })
                .build()
                .show();
    }

    private void showBottomFabIntro(String title,String content, int proceed) {

        new GuideView.Builder(this)
                .setTitle(title)
                .setContentText(content)
                .setTargetView((FloatingActionButton)findViewById(R.id.fab))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    if(proceed==1)
                        showFabHolderIntro();
                    else if(proceed ==2)
                        showHomePageCardViewIntro();
                })
                .build()
                .show();
    }

    private void showFabHolderIntro() {

        new GuideView.Builder(this)
                .setTitle("All Options")
                .setContentText("Tapping on any option will open the respective page for you to add data.")
                .setTargetView((LinearLayout)findViewById(R.id.fabHolder))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showFabHolderExpenseIntro();
                })
                .build()
                .show();
    }

    private void showFabHolderExpenseIntro() {

        new GuideView.Builder(this)
                .setTitle("Add Expense")
                .setContentText("Click here to view more")
                .setTargetView((ExtendedFloatingActionButton)findViewById(R.id.expense))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseIntro() {

        new GuideView.Builder(this)
                .setTitle("Adding an expense")
                .setContentText("You can add an new expense here. Click anywhere to proceed.")
                .setTargetView((CardView)findViewById(R.id.addNewExpenseView))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.anywhere) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseDateIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseDateIntro() {

        new GuideView.Builder(this)
                .setTitle("Expense Date")
                .setContentText("Tap on the date to change it.")
                .setTargetView((TextView)findViewById(R.id.newExpenseDate))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseAmountIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseAmountIntro() {

        new GuideView.Builder(this)
                .setTitle("Total Expense Amount")
                .setContentText("Add the total amount of expense here.")
                .setTargetView((EditText)findViewById(R.id.newExpenseTotalAmount))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseCheckBoxIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseCheckBoxIntro() {

        new GuideView.Builder(this)
                .setTitle("Who paid the amount?")
                .setContentText("If you paid the amount then select 'Paid By Me' otherwise don't")
                .setTargetView((CheckBox)findViewById(R.id.newExpensePaidBy))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseDescriptionIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseDescriptionIntro() {

        new GuideView.Builder(this)
                .setTitle("A Little Description")
                .setContentText("Add a small description. Like what you bought")
                .setTargetView((EditText)findViewById(R.id.newExpenseDescription))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseItemsIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseItemsIntro() {

        new GuideView.Builder(this)
                .setTitle("Segregation")
                .setContentText("Select Category, Group and Payment Mode here to segregate expenses further")
                .setTargetView((LinearLayout)findViewById(R.id.itemsHolder))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showAddExpenseAddIntro();
                })
                .build()
                .show();
    }

    private void showAddExpenseAddIntro() {

        new GuideView.Builder(this)
                .setTitle("Save your expense!")
                .setContentText("Don't forget to click here to finally save everything")
                .setTargetView((Button)findViewById(R.id.newExpenseAdd))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    showBottomFabIntro("Click!","Press here to close",2);
                })
                .build()
                .show();
    }

    private void showHomePageCardViewIntro() {

        new GuideView.Builder(this)
                .setTitle("Current Month Info")
                .setContentText("All the important info for current month will be displayed here")
                .setTargetView((CardView)findViewById(R.id.cardview))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                .setGuideListener((v)->{
                    new GuideView.Builder(this)
                            .setTitle("Your current month total")
                            .setContentText("This will show the total expense for the current month")
                            .setTargetView((TextView)findViewById(R.id.currentMonthTotalSpends))
                            .setContentTextSize(12)//optional
                            .setTitleTextSize(14)//optional
                            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                            .setGuideListener((x)->{
                                new GuideView.Builder(this)
                                        .setTitle("Limit Used So Far / Total Month's Limit")
                                        .setContentText("Month's total limit is calculated as the sum of limits of all the categories")
                                        .setTargetView((TextView)findViewById(R.id.homePageLimit))
                                        .setContentTextSize(12)//optional
                                        .setTitleTextSize(14)//optional
                                        .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                                        .setGuideListener((y)->{
                                            new GuideView.Builder(this)
                                                    .setTitle("View Old Expenses")
                                                    .setContentText("Tapping here will view expense for any other month or year")
                                                    .setTargetView((ImageButton)findViewById(R.id.viewAll))
                                                    .setContentTextSize(12)//optional
                                                    .setTitleTextSize(14)//optional
                                                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                                                    .setGuideListener((z)->{
                                                        //End here and set homeInfo as false in sharedPreferences
                                                        editor.putBoolean("mainPageIntro",true);
                                                        editor.apply();
                                                    })
                                                    .build()
                                                    .show();
                                        })
                                        .build()
                                        .show();
                            })
                            .build()
                            .show();
                })
                .build()
                .show();
    }

}
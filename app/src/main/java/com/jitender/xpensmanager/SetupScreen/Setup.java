package com.jitender.xpensmanager.SetupScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jitender.xpensmanager.BackupAndRestoreUtils.BackupExportRestoreUtil;
import com.jitender.xpensmanager.BackupAndRestoreUtils.RestoreService;
import com.jitender.xpensmanager.Database.CategoryDB;
import com.jitender.xpensmanager.Database.GroupDB;
import com.jitender.xpensmanager.Database.PaymentsDB;
import com.jitender.xpensmanager.MainScreen.MainActivity;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Setup extends AppCompatActivity {
    private static String[] combinedList = {"Rupee - ₹","Yen - ¥","Ruble - ₽","Korean Won - ₩","Dollar - $","Pound - £","Euro - €","Other - #"};
    private EditText salary,categoryLimit,categoryName,newGroupTitle,newGroupNoOfPersons,newGroupLimit;
    private Button createCategory,create;
    private TextView currency, restoreInfo,age;
    private static GroupDB groupsDB;
    private static CategoryDB categoryDB;
    private static PaymentsDB paymentsDB;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private LinearLayout restoringLayout;
    private Calendar today;
    private LinearLayout quickSetup;
    private LinearLayout restore;
    private CardView addgroupholder,addcategoryholder,addpaymentholder;
    private LinearLayout setupLayoutHolder,restorelayout,categorylayout,grouplayout,paymentlayout,restoreFromBackup, restoreFromFile,backFromRestore,completeSetup,back;
    private ScrollView setuplayout;
    //create new payment mode
    private EditText modeName, modeLimit;
    private Button createPaymentMode;

    private int gflag = 0, cflag = 0, pflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        //EditTexts
        age = findViewById(R.id.age);
        salary = findViewById(R.id.salary);
        categoryLimit = findViewById(R.id.categoryLimit);
        categoryName = findViewById(R.id.categoryName);
        newGroupTitle = findViewById(R.id.title);
        newGroupNoOfPersons = findViewById(R.id.noofpeps);
        newGroupLimit = findViewById(R.id.limit);
        restoreFromBackup = findViewById(R.id.restoreFromBackup);
        restoreFromFile = findViewById(R.id.restoreFromFile);
        restoreInfo = findViewById(R.id.restoreInfo);
        restoringLayout = findViewById(R.id.restoringLayout);
        restore = findViewById(R.id.restore);
        quickSetup = findViewById(R.id.quickSetup);
        setupLayoutHolder = findViewById(R.id.setupLayoutHolder);
        restorelayout = findViewById(R.id.restorelayout);
        setuplayout = findViewById(R.id.setuplayout);
        back = findViewById(R.id.back);
        backFromRestore = findViewById(R.id.backFromRestore);
        categorylayout = findViewById(R.id.categorylayout);
        grouplayout = findViewById(R.id.grouplayout);
        paymentlayout = findViewById(R.id.paymentlayout);
        addpaymentholder = findViewById(R.id.addpaymentholder);
        addcategoryholder = findViewById(R.id.addcategoryholder);
        addgroupholder = findViewById(R.id.addgroupholder);


        restore.setOnClickListener((v)->{
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(setupLayoutHolder).addTarget(restorelayout).addTarget(setuplayout);
            TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
            setupLayoutHolder.setVisibility(View.GONE);
            restorelayout.setVisibility(View.VISIBLE);
            setuplayout.setVisibility(View.GONE);
        });

        quickSetup.setOnClickListener((v)->{
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(setupLayoutHolder).addTarget(restorelayout).addTarget(setuplayout);
            TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
            setupLayoutHolder.setVisibility(View.GONE);
            restorelayout.setVisibility(View.GONE);
            setuplayout.setVisibility(View.VISIBLE);
        });

        back.setOnClickListener((v)->{
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(setupLayoutHolder).addTarget(restorelayout).addTarget(setuplayout);
            TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
            setupLayoutHolder.setVisibility(View.VISIBLE);
            restorelayout.setVisibility(View.GONE);
            setuplayout.setVisibility(View.GONE);
        });

        backFromRestore.setOnClickListener((v)->{
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(setupLayoutHolder).addTarget(restorelayout).addTarget(setuplayout);
            TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
            setupLayoutHolder.setVisibility(View.VISIBLE);
            restorelayout.setVisibility(View.GONE);
            setuplayout.setVisibility(View.GONE);
        });

        addpaymentholder.setOnClickListener((v)->{
            if(pflag == 0) {
                Transition transition = new Fade(Fade.IN);
                transition.setDuration(200);
                transition.addTarget(categorylayout).addTarget(grouplayout).addTarget(paymentlayout);
                TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
                categorylayout.setVisibility(View.GONE);
                grouplayout.setVisibility(View.GONE);
                paymentlayout.setVisibility(View.VISIBLE);
                pflag = 1;
            }else{
                pflag = 0;
                paymentlayout.setVisibility(View.GONE);
            }
            cflag = 0;
            gflag = 0;
        });

        addcategoryholder.setOnClickListener((v)->{
            if(cflag == 0) {
                Transition transition = new Fade(Fade.IN);
                transition.setDuration(200);
                transition.addTarget(categorylayout).addTarget(grouplayout).addTarget(paymentlayout);
                TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
                categorylayout.setVisibility(View.VISIBLE);
                grouplayout.setVisibility(View.GONE);
                paymentlayout.setVisibility(View.GONE);
                cflag = 1;
            }else{
                cflag = 0;
                categorylayout.setVisibility(View.GONE);
            }
            pflag = 0;
            gflag = 0;
        });

        addgroupholder.setOnClickListener((v)->{
            if(gflag == 0) {
                Transition transition = new Fade(Fade.IN);
                transition.setDuration(200);
                transition.addTarget(categorylayout).addTarget(grouplayout).addTarget(paymentlayout);
                TransitionManager.beginDelayedTransition(findViewById(R.id.mainLayout), transition);
                categorylayout.setVisibility(View.GONE);
                grouplayout.setVisibility(View.VISIBLE);
                paymentlayout.setVisibility(View.GONE);
                gflag = 1;
            }else{
                gflag = 0;
                grouplayout.setVisibility(View.GONE);
            }
            cflag = 0;
            pflag = 0;
        });


        //Buttons
        createCategory = findViewById(R.id.createCategory);
        create = findViewById(R.id.create);
        completeSetup = findViewById(R.id.completeSetup);

        //TextViews
        currency = findViewById(R.id.currency);

        groupsDB = new GroupDB(getApplicationContext());
        categoryDB = new CategoryDB(getApplicationContext());
        paymentsDB = new PaymentsDB(getApplicationContext());
        groupsDB.insertNewGroup("Personal",1,20000);

        today = Calendar.getInstance();
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), getApplicationContext().MODE_PRIVATE);
        editor = sharedPref.edit();

        getPermissions();
        Executors.newSingleThreadExecutor().execute(checkBackUp);

        createCategory.setOnClickListener(v -> {
            String categoryValue = categoryName.getText().toString();
            String categoryLimitVal = categoryLimit.getText().toString();
            if(categoryValue == null || categoryValue.equalsIgnoreCase("")) {
                categoryName.setError("Field cannot be blank");
            } else {
                if(categoryLimitVal == null || categoryLimitVal.equalsIgnoreCase("")) {
                    categoryLimit.setError("Field cannot be blank");
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
                        }
                    }
                }
            }
        });

        create.setOnClickListener(v -> {
            String titleValue = newGroupTitle.getText().toString();
            String noOfPersons = newGroupNoOfPersons.getText().toString();
            String limit = newGroupLimit.getText().toString();
            if(titleValue == null || titleValue.equalsIgnoreCase("")) {
                newGroupTitle.setError("Field cannot be blank");
            } else{
                if(noOfPersons == null || noOfPersons.equalsIgnoreCase("")){
                    newGroupNoOfPersons.setError("Field cannot be blank");
                } else{
                    if (Integer.parseInt(noOfPersons)<=0) {
                        newGroupNoOfPersons.setError("Cannot be 0 or less");
                    }
                    else {
                        if(limit == null || limit.equalsIgnoreCase("")){
                            newGroupLimit.setError("Field cannot be blank");
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
                                }
                            }
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
                        }
                    }
                }
            }
        });


        age.setOnClickListener( (v)->{
            displayGroupDialogBox();
        });

        completeSetup.setOnClickListener(v -> {
            String currentAge = age.getText().toString();
            String currentSalary = salary.getText().toString();
           /* if(currentAge == null || currentAge.equalsIgnoreCase("")) {
                age.setError("Field cannot be blank");
            } else {
                if(Integer.parseInt(currentAge)<=0 || Integer.parseInt(currentAge)>120){
                    age.setError("You must be kidding");
                }
                else {*/
                    if(currency.getText().toString().equalsIgnoreCase("") || currency.getText().toString().equalsIgnoreCase(null)  || currency.getText().toString().equalsIgnoreCase("Select Currency") ){
                        currency.setError("Please select a value");
                    }else {
                       /* if (currentSalary == null || currentSalary.equalsIgnoreCase("")) {
                            salary.setError("Field cannot be blank");
                        } else {
                            if (Double.parseDouble(currentSalary) <= 0.0) {
                                salary.setError("We're not judging, but..");
                            } else {*/
                                int tempAge=2000,tempSalary=0;
                                if(currentAge !=null && !currentAge.equalsIgnoreCase(""))
                                    tempAge = Integer.parseInt(currentAge);
                                if(currentSalary !=null && !currentSalary.equalsIgnoreCase(""))
                                    tempSalary = Integer.parseInt(currentSalary);
                                editor.putInt("age", tempAge);
                                editor.putLong("salary", tempSalary);
                                editor.putBoolean("initialSetup", false);
                                editor.apply();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                     //   }
                   // }
                //}
            //}

        });

        currency.setOnClickListener((v)->{
            displayCurrencyDialogBox();
        });

        restoreFromBackup.setOnClickListener((v)->{
            restoringLayout.setVisibility(View.VISIBLE);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(()->{
                startService(new Intent(getApplicationContext(), RestoreService.class));
                while(RestoreService.isServiceRunning){}
                runOnUiThread(()->{
                    restoringLayout.setVisibility(View.GONE);
                    SplashScreenActivity.cSymbol = sharedPref.getString("cSymbol","#");
                    SplashScreenActivity.salary = sharedPref.getLong("salary",0);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });

            });
        });

        restoreFromFile.setOnClickListener((v) ->{
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("text/plain");
            chooseFile = Intent.createChooser(chooseFile, "Select backup file");
            startActivityForResult(chooseFile, 201);
        });

    }

    public void getPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 201 :
                if(resultCode == -1){
                    Uri fileUri =data.getData();
                    //String filePath = fileUri.getPath();
                    //File newFile = new File(fileUri.get);
                    BackupExportRestoreUtil restoreFile = new BackupExportRestoreUtil(getApplicationContext());
                    try {
                        restoringLayout.setVisibility(View.VISIBLE);
                        String result = restoreFile.restoreFromUri(fileUri);
                        restoringLayout.setVisibility(View.GONE);
                        if(result.equalsIgnoreCase("Successfully restored data from backup")){
                            //move to main activity
                            SplashScreenActivity.cSymbol = sharedPref.getString("cSymbol","#");
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                           restoreInfo.setText(result);
                           restoreInfo.setTextColor(getResources().getColor(R.color.theme_red_variant));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Log.d("File Path",filePath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //check if backup is already present and do few things
                    restoreInfo.setText("Checking if backup file already exist");
                    Executors.newSingleThreadExecutor().execute(checkBackUp);
                } else {
                    restoreInfo.setText("Storage Permission Denied. Go to app settings and give storage access to restore from backup");
                    restoreFromFile.setEnabled(false);
                    restoreFromFile.setAlpha(0.5f);
                    restoreFromBackup.setEnabled(false);
                    restoreFromBackup.setAlpha(0.5f);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void displayCurrencyDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Currency");
        builder.setItems(combinedList, (dialog, which) -> {
            currency.setText(combinedList[which]);
            if(currency.getError()!=null)
                currency.setError(null);
            String symbol = combinedList[which].split("-")[1];
            String name = combinedList[which].split("-")[0];
            editor.putString("cSymbol",symbol);
            editor.putString("cName",name);
            editor.apply();
            SplashScreenActivity.cSymbol = symbol;
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    Runnable checkBackUp = () ->{
        if(new BackupExportRestoreUtil(getApplicationContext()).isBackupFilePresent()){
            runOnUiThread(()->{
                restoreInfo.setText("Backup found. To restore from existing backup click 'Restore From Backup' button.");
                restoreFromBackup.setEnabled(true);
                restoreFromBackup.setAlpha(1.0f);
                restoreFromFile.setAlpha(1.0f);
                restoreFromFile.setEnabled(true);
            });
        }else{
            runOnUiThread(() -> {
                restoreInfo.setText("No existing backup found. To restore from backup file click 'Select Restore File' button.");
                restoreFromBackup.setEnabled(false);
                restoreFromBackup.setAlpha(0.5f);
                restoreFromFile.setAlpha(1.0f);
                restoreFromFile.setEnabled(true);
            });
        }
    };

    public void displayGroupDialogBox(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this,
                (selectedMonth, selectedYear) -> {
                    age.setText(selectedYear+"");
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setTitle("Select date of birth")
                .setYearRange(1920, today.get(Calendar.YEAR))
                .showYearOnly()
                .setOnMonthChangedListener(selectedMonth -> { /* on month selected*/ })
                .setOnYearChangedListener(selectedYear -> { /* on year selected*/ })
                .build().show();
    }
}
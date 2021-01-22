package com.jitender.xpensmanager.MainScreen.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jitender.xpensmanager.BackupAndRestoreUtils.BackupExportRestoreUtil;
import com.jitender.xpensmanager.BackupAndRestoreUtils.BackupService;
import com.jitender.xpensmanager.BackupAndRestoreUtils.ExportToExcelService;
import com.jitender.xpensmanager.BackupAndRestoreUtils.RestoreService;
import com.jitender.xpensmanager.MainScreen.MainActivity;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Settings extends Fragment {
    private static String[] combinedList = {"Rupee - ₹","Yen - ¥","Ruble - ₽","Korean Won - ₩","Dollar - $","Pound - £","Euro - €","Other - #"};
    private TextView currencySymbol, currencyName, salary,age,version ,backUpfrequency, restoreInfo, lastBackUpDate,backupLocation,exportLocation;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String currentCurrencySymbol,currentCurrencyName,currentBackupFrequency, currentLastBackupDate;
    private long currentSalary;
    private int currentAge;
    private Button createBackup,exportToExcel,restoreBackup,restoreFromFile,uploadBackup;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private BackupExportRestoreUtil backupExportRestoreUtilUtils;
    private LinearLayout restoringLayout;
    private Calendar today;

    public Settings() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
        editor = sharedPref.edit();
        currentCurrencySymbol = sharedPref.getString("cSymbol","#");
        currentCurrencyName = sharedPref.getString("cName","Others");
        currentSalary = sharedPref.getLong("salary",0);
        currentAge = sharedPref.getInt("age",0);
        currentBackupFrequency = sharedPref.getString("frequency","None");
        currentLastBackupDate = sharedPref.getString("lastBackupDate","None");
        backupExportRestoreUtilUtils = new BackupExportRestoreUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        currencySymbol = view.findViewById(R.id.currencySymbol);
        currencyName = view.findViewById(R.id.currencyName);
        salary = view.findViewById(R.id.salary);
        age = view.findViewById(R.id.age);
        version = view.findViewById(R.id.version);
        createBackup = view.findViewById(R.id.createBackup);
        exportToExcel = view.findViewById(R.id.exportToExcel);
        restoreBackup = view.findViewById(R.id.restoreBackup);
        restoreFromFile = view.findViewById(R.id.restoreFromFile);
        restoringLayout = view.findViewById(R.id.restoringLayout);
        backUpfrequency = view.findViewById(R.id.backUpfrequency);
        restoreInfo = view.findViewById(R.id.restoreInfo);
        lastBackUpDate = view.findViewById(R.id.lastBackUpDate);
        uploadBackup = view.findViewById(R.id.uploadBackup);
        backupLocation = view.findViewById(R.id.backupLocation);
        exportLocation = view.findViewById(R.id.exportLocation);

        today = Calendar.getInstance();

        backupLocation.setOnClickListener((v)->{
            backupLocation.setText(getActivity().getExternalFilesDir(null).getAbsolutePath()+"/XpensManager/Backup/");
        });

        exportLocation.setOnClickListener((v)-> {
            exportLocation.setText(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/XpensManager/ExcelExports/");
        });

        uploadBackup.setOnClickListener((v)->{
            Intent backupUpload = new Intent(Intent.ACTION_SEND);
            backupUpload.putExtra(Intent.EXTRA_STREAM,backupExportRestoreUtilUtils.getBackupFilePath());
            backupUpload.setType("text/plain");
            startActivity( Intent.createChooser(backupUpload,"Upload/Share Backup"));
        });

        if(currentLastBackupDate.equals("None")){
            lastBackUpDate.setVisibility(View.GONE);
        }else {
            lastBackUpDate.setVisibility(View.VISIBLE);
            lastBackUpDate.setText("Last backup created on " + currentLastBackupDate);
        }

        backUpfrequency.setText(currentBackupFrequency);
        backUpfrequency.setOnClickListener((v)->{
            displayFrequencyDialogBox(new String[]{"None","Daily","Weekly","Monthly"});
        });

        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            int versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
            version.setText(versionCode+"."+versionName);
        } catch (Exception e) {
            version.setVisibility(View.GONE);
            e.printStackTrace();
        }

        age.setText(currentAge+"");
        age.setOnClickListener((v)->{
            displayGroupDialogBox();
        });

        salary.setText(currentCurrencySymbol+" "+currentSalary);
        salary.setOnClickListener((v)->{
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View dialogView = factory.inflate(R.layout.update_details_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            TextView title = dialogView.findViewById(R.id.title);
            EditText maxLimit = dialogView.findViewById(R.id.categoryLimit);
            title.setText("Update Salary");
            maxLimit.setText(currentSalary+"");
            maxLimit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            dialog.setView(dialogView);
            dialogView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //your business logic
                    if(maxLimit.getText().toString().equalsIgnoreCase("") || maxLimit.getText().toString() == null) {
                        maxLimit.setError("Cannot be blank");
                    }else{
                        long tempLimit = Long.parseLong(maxLimit.getText().toString());
                        if(tempLimit <= 0 ){
                            maxLimit.setError("Cannot be 0 or less");
                        }else{
                            //update
                            editor.putLong("salary",tempLimit);
                            editor.apply();
                            SplashScreenActivity.salary = tempLimit;
                            salary.setText(SplashScreenActivity.cSymbol+" "+tempLimit);
                            dialog.dismiss();
                        }
                    }
                }
            });
            dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        currencyName.setText(currentCurrencyName);
        currencySymbol.setText(currentCurrencySymbol);
        currencySymbol.setOnClickListener((v)-> {
            displayCurrencyDialogBox();
        });
        currencyName.setOnClickListener((v)-> {
            displayCurrencyDialogBox();
        });

        createBackup.setOnClickListener((v) -> {
            if(checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)){
                getActivity().startService(new Intent(getActivity(), BackupService.class));
            }
        });

        exportToExcel.setOnClickListener((v)->{
            if(checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)){
                getActivity().startService(new Intent(getActivity(), ExportToExcelService.class));
            }
        });

        restoreBackup.setOnClickListener((v)->{
            if(checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)) {

                restoringLayout.setVisibility(View.VISIBLE);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(()->{
                    getActivity().startService(new Intent(getActivity(), RestoreService.class));
                    while(RestoreService.isServiceRunning){}
                    getActivity().runOnUiThread(()->{
                        restoringLayout.setVisibility(View.GONE);
                        SplashScreenActivity.cSymbol = sharedPref.getString("cSymbol","#");
                        SplashScreenActivity.salary = sharedPref.getLong("salary",0);
                        age.setText(sharedPref.getInt("age",0)+"");
                        salary.setText(SplashScreenActivity.cSymbol+" "+sharedPref.getLong("salary",0));
                        currencyName.setText(sharedPref.getString("cName","#"));
                        currencySymbol.setText(SplashScreenActivity.cSymbol);
                    });

                });
            }
        });

        restoreFromFile.setOnClickListener((v)->{
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("text/plain");
            chooseFile = Intent.createChooser(chooseFile, "Select backup file");
            startActivityForResult(chooseFile, 201);
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 201 :
                if(resultCode == -1){
                    Uri fileUri =data.getData();
                    BackupExportRestoreUtil restoreFile = new BackupExportRestoreUtil(getActivity());
                    try {
                        restoringLayout.setVisibility(View.VISIBLE);
                        String result = restoreFile.restoreFromUri(fileUri);
                        restoringLayout.setVisibility(View.GONE);
                        if(result.equalsIgnoreCase("Successfully restored data from backup")){
                            //move to main activity
                            SplashScreenActivity.cSymbol = sharedPref.getString("cSymbol","#");
                            SplashScreenActivity.salary = sharedPref.getLong("salary",0);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            restoreInfo.setText(result);
                            restoreInfo.setVisibility(View.VISIBLE);
                            restoreInfo.setTextColor(getResources().getColor(R.color.theme_red_variant));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void displayCurrencyDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Currency");
        builder.setItems(combinedList, (dialog, which) -> {
            String symbol = combinedList[which].split("-")[1];
            String name = combinedList[which].split("-")[0];
            currencySymbol.setText(symbol);
            currencyName.setText(name);
            editor.putString("cSymbol",symbol);
            editor.putString("cName",name);
            editor.apply();
            SplashScreenActivity.cSymbol = symbol;
            salary.setText(SplashScreenActivity.cSymbol+" "+currentSalary);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean checkStoragePermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);
            return false;
        }
        else {
            return true;
        }
    }

    public void displayFrequencyDialogBox(String[] list){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Category");
        builder.setItems(list, (dialog, which) -> {
            backUpfrequency.setText(list[which]);
            editor.putString("frequency",list[which]);
            editor.apply();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void displayGroupDialogBox(){
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(),
                (selectedMonth, selectedYear) -> {
                    age.setText(selectedYear+"");
                    editor.putInt("age",selectedYear);
                    editor.apply();
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setTitle("Select date of birth")
                .setYearRange(1920, today.get(Calendar.YEAR))
                .showYearOnly()
                .setOnMonthChangedListener(selectedMonth -> { /* on month selected*/ })
                .setOnYearChangedListener(selectedYear -> { /* on year selected*/ })
                .build().show();
    }

}
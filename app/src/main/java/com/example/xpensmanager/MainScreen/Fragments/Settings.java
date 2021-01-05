package com.example.xpensmanager.MainScreen.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpensmanager.BackupAndRestoreUtils.Backup;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;


public class Settings extends Fragment {
    private static String[] combinedList = {"Rupee - ₹","Yen - ¥","Ruble - ₽","Korean Won - ₩","Dollar - $","Pound - £","Euro - €","Other - #"};
    private TextView currencySymbol, currencyName, salary,age,version;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String currentCurrencySymbol,currentCurrencyName;
    private long currentSalary;
    private int currentAge;
    private Button createBackup,exportToExcel,restoreBackup;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private Backup backupUtils;

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
        backupUtils = new Backup(getActivity());
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
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View dialogView = factory.inflate(R.layout.update_details_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            TextView title = dialogView.findViewById(R.id.title);
            EditText maxLimit = dialogView.findViewById(R.id.categoryLimit);
            title.setText("Update Age");
            maxLimit.setText(currentAge+"");
            maxLimit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            dialog.setView(dialogView);
            dialogView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //your business logic
                    if(maxLimit.getText().toString().equalsIgnoreCase("") || maxLimit.getText().toString() == null) {
                        maxLimit.setError("Cannot be blank");
                    }else{
                        int tempLimit = Integer.parseInt(maxLimit.getText().toString());
                        if(tempLimit <= 0 ){
                            maxLimit.setError("Cannot be 0 or less");
                        }
                        else if(tempLimit>100){
                            maxLimit.setError("Woah! Which planet are you from?");
                        }else{
                            //update
                            editor.putInt("age",tempLimit);
                            editor.apply();
                            age.setText(tempLimit+"");
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

        salary.setText(currentCurrencySymbol+" "+currentSalary);
        salary.setOnClickListener((v)->{
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View dialogView = factory.inflate(R.layout.update_details_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            TextView title = dialogView.findViewById(R.id.title);
            EditText maxLimit = dialogView.findViewById(R.id.categoryLimit);
            title.setText("Update Salary");
            maxLimit.setText(currentSalary+"");
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
                backupUtils.createBackUp();
            }
        });

        exportToExcel.setOnClickListener((v)->{
            if(checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)){
                backupUtils.exportToExcel();
            }
        });

        restoreBackup.setOnClickListener((v)->{
            if(checkStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)){
                backupUtils.restoreFromBackUp();
            }
        });


        return view;
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

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else {
                Toast.makeText(getActivity(), "Permission denied : Cannot create backup", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

}
package com.example.xpensmanager.BackupAndRestoreUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Database.GroupData;
import com.example.xpensmanager.R;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import se.simbio.encryption.Encryption;

public class BackupExportRestoreUtil {

    private ExpenseDB expenseDB;
    private CategoryDB categoryDB;
    private GroupDB groupDB;
    private ArrayList<ExpenseData> expenseData = new ArrayList<>();
    private ArrayList<CategoryData> categoryData = new ArrayList<>();
    private ArrayList<GroupData> groupData = new ArrayList<>();
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String filename = "backup.txt";
    private Encryption encrypt;
    private static String salt= "sugar";
    private static String key = "love";
    private byte[] iv = new byte[16];

    public BackupExportRestoreUtil(Context context){
        this.context = context;
        expenseDB = new ExpenseDB(context);
        categoryDB = new CategoryDB(context);
        groupDB = new GroupDB(context);
        expenseData.addAll(expenseDB.findAll());
        categoryData.addAll(categoryDB.findAll());
        groupData.addAll(groupDB.findAll());
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        encrypt = Encryption.getDefault(key,salt,iv);
    }

    public String createBackUp() {
        String encrypted = encrypt.encryptOrNull(String.valueOf(backup()));
        try {
            if (isExternalStorageWritable() && isExternalStorageReadable()) {
                File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(),"/XpensManager/Backup/");
                if(!dir.exists())
                    dir.mkdir();

                File output = new File(dir, filename);
                if (!output.exists()) {
                    try {
                        output.getParentFile().mkdirs();
                        output.createNewFile();
                    } catch (Exception e) {
                        Log.d("Error", e.toString());
                        e.printStackTrace();
                        //Toast.makeText(context, "Unable to make backup file. ", Toast.LENGTH_SHORT).show();
                        return "Please check folder permission";
                    }
                } else {
                    output.delete();
                    try {
                        output.createNewFile();
                    } catch (Exception e) {
                        Log.d("Error", e.toString());
                        e.printStackTrace();
                        //Toast.makeText(context, "Unable to make backup file. ", Toast.LENGTH_SHORT).show();
                        return "Please check folder permission";
                    }
                }
                FileWriter writer = new FileWriter(output);
                writer.append(encrypted);
                writer.flush();
                writer.close();
                return output.getCanonicalPath();
            } else {
                Toast.makeText(context, "Storage Not Accessible", Toast.LENGTH_LONG).show();
                return "Access to storage Denied";
            }
        }
        catch(IOException e){
            Log.e("Exception While backup", "File write failed: " + e.toString());
            e.printStackTrace();
            return "Try Again!";
        }
    }

    public String exportToExcel(){
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet expenseSheet = workbook.createSheet("Expenses");

        HSSFRow row;
        Cell cell;

        row = expenseSheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("Date");
        cell = row.createCell(2);
        cell.setCellValue("Description");
        cell = row.createCell(3);
        cell.setCellValue("Total Amount Paid");
        cell = row.createCell(4);
        cell.setCellValue("Amount You Paid");
        cell = row.createCell(5);
        cell.setCellValue("Category");
        cell = row.createCell(6);
        cell.setCellValue("Group");
        cell = row.createCell(7);
        cell.setCellValue("Paid By");
        for(int i=0;i<expenseData.size();i++){
            row = expenseSheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(expenseData.get(i).getId());

            cell = row.createCell(1);
            cell.setCellValue(expenseData.get(i).getDate());

            cell = row.createCell(2);
            cell.setCellValue(expenseData.get(i).getDescription());

            cell = row.createCell(3);
            cell.setCellValue(expenseData.get(i).getAmount());

            cell = row.createCell(4);
            cell.setCellValue(expenseData.get(i).getSplitAmount());

            cell = row.createCell(5);
            cell.setCellValue(expenseData.get(i).getCategory());

            cell = row.createCell(6);
            cell.setCellValue(expenseData.get(i).getGroup());

            cell = row.createCell(7);
            cell.setCellValue(expenseData.get(i).getPaidBy());

        }


        HSSFSheet groupSheet = workbook.createSheet("Groups");

        row = groupSheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell = row.createCell(2);
        cell.setCellValue("No of Persons");
        cell = row.createCell(3);
        cell.setCellValue("Group Limit");
        cell = row.createCell(4);
        cell.setCellValue("Total Amount You Paid");
        cell = row.createCell(5);
        cell.setCellValue("Total Group Amount");

        for(int i=0;i<groupData.size();i++){
            row = groupSheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(groupData.get(i).getId());

            cell = row.createCell(1);
            cell.setCellValue(groupData.get(i).getTitle());

            cell = row.createCell(2);
            cell.setCellValue(groupData.get(i).getNoOfPersons());

            cell = row.createCell(3);
            cell.setCellValue(groupData.get(i).getMaxLimit());

            cell = row.createCell(4);
            cell.setCellValue(groupData.get(i).getGroupTotal());

            cell = row.createCell(5);
            cell.setCellValue(groupData.get(i).getTotalAmount());

        }


        HSSFSheet categorySheet = workbook.createSheet("Category");

        row = categorySheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell = row.createCell(2);
        cell.setCellValue("Total Spend On Category");
        cell = row.createCell(3);
        cell.setCellValue("Category Limit");

        for(int i=0;i<categoryData.size();i++){
            row = categorySheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(categoryData.get(i).getId());

            cell = row.createCell(1);
            cell.setCellValue(categoryData.get(i).getCategory());

            cell = row.createCell(2);
            cell.setCellValue(categoryData.get(i).getTotalCategorySpend());

            cell = row.createCell(3);
            cell.setCellValue(categoryData.get(i).getLimit());

        }

        try{
            if (isExternalStorageWritable() && isExternalStorageReadable()) {
                File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(), "/XpensManager/ExcelExports/");
                if (!dir.exists())
                    dir.mkdir();
                String filename = "XpenseManagerExport_" + new SimpleDateFormat("dd_MM_yyyy_HH_MM", Locale.ENGLISH).format(new Date()) + ".xlsx";
                File output = new File(dir, filename);
                if (!output.exists()) {
                    output.getParentFile().mkdirs();
                    output.createNewFile();
                }
                else {
                    output.delete();
                    output.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(output);
                workbook.write(out);
                out.close();
                return output.getCanonicalPath();
            }else{
                return "Storage access denied";
            }
        }catch(Exception e){
            Log.d("ExportToExcel",e.toString());
            return "Failed to export. Try Again!";
        }
    }

    public String restoreFromBackUp(){
        try {
            if (isExternalStorageWritable() && isExternalStorageReadable()) {
                File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(),"/XpensManager/Backup/");
                if(!dir.exists())
                    return "Backup directory doesn't not exist";

                File output = new File(dir, filename);
                if (!output.exists()) {
                    return "Backup doesn't exist";
                } else {
                   //Code to restore from backup
                    return restoreFromFile(output);
                }

            } else {
                return "Storage Not Accessible";
            }
        }
        catch(IOException | ParseException e){
            return "Error while restoring";
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isBackupFilePresent(){
        if (isExternalStorageWritable() && isExternalStorageReadable()) {
            File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(), "/XpensManager/Backup/");
            if (!dir.exists())
                return false;

            File output = new File(dir, filename);
            if (!output.exists()) {
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    public Uri getBackupFilePath() {
        File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(), "/XpensManager/Backup/");
        File output = new File(dir, filename);
        Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",output);//Uri.fromFile(output);
        return uri;
    }

    public String restoreFromFile(File output) throws IOException,ParseException {
        try {
            BufferedReader result = new BufferedReader(new FileReader(output));
            String temp = "";
            String x;
            while ((x = result.readLine()) != null) {
                temp += x;
            }
            return restore(temp);
        }catch (Exception e) {
            Log.d("Restore Error",e.toString());
            return "Some error while reading from file";
        }
    }

    public String restoreFromUri(Uri uri) throws IOException,ParseException {
        try {
            BufferedReader result = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri)));
            String temp = "";
            String x;
            while ((x = result.readLine()) != null) {
                temp += x;
            }
            return restore(temp);
        }catch (Exception e) {
            Log.d("Restore Error",e.toString());
            return "Unable to restore. Some error while reading from file";
        }
    }

    private String restore(String temp) throws IOException,ParseException{
        //Log.d("FileData",temp);
        String tempData = encrypt.decryptOrNull(temp);
        Log.d("FileData",tempData);
        BufferedReader data = new BufferedReader(new StringReader(tempData));
        String line;
        int flag = -1;
        if (data.readLine().equals("<Xpens_Manager_Backup>")) {
            while ((line = data.readLine()) != null) {
                if (!(line.contains("***END***") || line.contains("***START***"))) {
                    if (line.contains("Tablename -:- ")) {
                        String tablename = line.split("-:-")[1].trim();
                        if (tablename.equals(ExpenseDB.tableName)) {
                            flag = 0;
                            expenseDB.deleteAll();
                        } else if (tablename.equals(GroupDB.groupdb_table)) {
                            flag = 1;
                            groupDB.deleteAll();
                        } else if (tablename.equals(CategoryDB.categorydb_table)) {
                            flag = 2;
                            categoryDB.deleteAll();
                        } else if (tablename.equals("Shared-Preferences")) {
                            Log.d("Restore Data", "Shared Preferences");
                            flag = 3;
                        } else {
                            Log.d("Restore Data", "Some Issue in data");
                        }
                    } else {
                        System.out.println(line);
                        String[] actualData = line.split("\\|\\|");
                        if (flag == 0)
                            expenseDB.insertNewExpenseFromBackup(new SimpleDateFormat("dd/MM/yyyy").parse(actualData[1]), Double.parseDouble(actualData[2]), actualData[3], actualData[5], actualData[4], Double.parseDouble(actualData[6]), actualData[7]);
                        else if (flag == 1)
                            groupDB.insertNewGroupFromBackup(actualData[1], Integer.parseInt(actualData[2]), Double.parseDouble(actualData[3]), Double.parseDouble(actualData[4]), Double.parseDouble(actualData[5]));
                        else if (flag == 2)
                            categoryDB.insertNewCategoryFromBackup(actualData[1], Double.parseDouble(actualData[2]), Double.parseDouble(actualData[3]));
                        else if (flag == 3) {
                            //shared preferences
                            if (actualData[2].contains("Boolean")) {
                                editor.putBoolean(actualData[0], Boolean.parseBoolean(actualData[1]));
                                editor.apply();
                            } else if (actualData[2].contains("String")) {
                                editor.putString(actualData[0], actualData[1]);
                                editor.apply();
                            } else if (actualData[2].contains("Integer")) {
                                editor.putInt(actualData[0], Integer.parseInt(actualData[1]));
                                editor.apply();
                            } else if (actualData[2].contains("Long")) {
                                editor.putLong(actualData[0], Long.parseLong(actualData[1]));
                                editor.apply();
                            } else if (actualData[2].contains("Float")) {
                                editor.putFloat(actualData[0], Float.parseFloat(actualData[1]));
                                editor.apply();
                            } else {
                                //Ignore the row.
                                Log.d("Shared_preferences", "Error in restoring shared-preferences");
                            }
                        }
                    }

                }
            }
            data.close();
            return "Successfully restored data from backup";
        } else {
            data.close();
            return "Either backup file is corrupted or wrong backup file selected";
        }
    }

    private StringBuffer backup(){
        StringBuffer data = new StringBuffer();
        data.append("<Xpens_Manager_Backup>\n");
        data.append("Tablename -:- " + ExpenseDB.tableName);
        data.append("\n***START***\n");
        for (int i = 0; i < expenseData.size(); i++) {
            data.append(expenseData.get(i).getId() + "||" +
                    expenseData.get(i).getDate() + "||" +
                    expenseData.get(i).getAmount() + "||" +
                    expenseData.get(i).getDescription() + "||" +
                    expenseData.get(i).getPaidBy() + "||" +
                    expenseData.get(i).getCategory() + "||" +
                    expenseData.get(i).getSplitAmount() + "||" +
                    expenseData.get(i).getGroup() + "\n");
        }
        data.append("***END***\n");
        data.append("Tablename -:- " + GroupDB.groupdb_table);
        data.append("\n***START***\n");
        for (int i = 0; i < groupData.size(); i++) {
            data.append(groupData.get(i).getId() + "||" +
                    groupData.get(i).getTitle() + "||" +
                    groupData.get(i).getNoOfPersons() + "||" +
                    groupData.get(i).getMaxLimit() + "||" +
                    groupData.get(i).getNetAmount() + "||" +
                    groupData.get(i).getTotalAmount() + "\n");
        }
        data.append("***END***\n");
        data.append("Tablename -:- " + CategoryDB.categorydb_table);
        data.append("\n***START***\n");
        for (int i = 0; i < categoryData.size(); i++) {
            data.append(categoryData.get(i).getId() + "||" +
                    categoryData.get(i).getCategory() + "||" +
                    categoryData.get(i).getLimit() + "||" +
                    categoryData.get(i).getTotalCategorySpend() + "\n");
        }
        data.append("***END***\n");
        Map<String,?> sharedPref = sharedPreferences.getAll();
        data.append("Tablename -:- Shared-Preferences");
        data.append("\n***START***\n");
        for(String key : sharedPref.keySet()){
            data.append(key+"||"+sharedPref.get(key).toString()+"||"+sharedPref.get(key).getClass().toString()+"\n");
        }
        data.append("***END***\n");
        Log.d("Backup data - ", String.valueOf(data));
        return data;
    }
    
}

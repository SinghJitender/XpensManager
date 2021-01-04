package com.example.xpensmanager.BackupAndRestoreUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.ExpenseData;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Database.GroupData;
import com.example.xpensmanager.ExpenseScreen.Expense;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Backup {

    private ExpenseDB expenseDB;
    private CategoryDB categoryDB;
    private GroupDB groupDB;
    private ArrayList<ExpenseData> expenseData = new ArrayList<>();
    private ArrayList<CategoryData> categoryData = new ArrayList<>();
    private ArrayList<GroupData> groupData = new ArrayList<>();
    private Context context;

    public Backup(Context context){
        this.context = context;
        expenseDB = new ExpenseDB(context);
        categoryDB = new CategoryDB(context);
        groupDB = new GroupDB(context);
        expenseData.addAll(expenseDB.findAll());
        categoryData.addAll(categoryDB.findAll());
        groupData.addAll(groupDB.findAll());
    }

    public void createBackUp() {
        StringBuffer data = new StringBuffer();
        data.append("Tablename : " + ExpenseDB.tableName);
        data.append("\n***START***\n");
        for (int i = 0; i < expenseData.size(); i++) {
            data.append(expenseData.get(i).getId() + "||" +
                    expenseData.get(i).getDate() + "||" +
                    expenseData.get(i).getDayOfWeek() + "||" +
                    expenseData.get(i).getTextMonth() + "||" +
                    expenseData.get(i).getDay() + "||" +
                    expenseData.get(i).getMonth() + "||" +
                    expenseData.get(i).getYear() + "||" +
                    expenseData.get(i).getAmount() + "||" +
                    expenseData.get(i).getDescription() + "||" +
                    expenseData.get(i).getPaidBy() + "||" +
                    expenseData.get(i).getCategory() + "||" +
                    expenseData.get(i).getDeleted() + "||" +
                    expenseData.get(i).getSplitAmount() + "||" +
                    expenseData.get(i).getGroup() + "\n");
        }
        data.append("***END***\n");
        data.append("Tablename : " + GroupDB.groupdb_table);
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
        data.append("Tablename : " + CategoryDB.categorydb_table);
        data.append("\n***START***\n");
        for (int i = 0; i < categoryData.size(); i++) {
            data.append(categoryData.get(i).getId() + "||" +
                    categoryData.get(i).getCategory() + "||" +
                    categoryData.get(i).getLimit() + "||" +
                    categoryData.get(i).getTotalCategorySpend() + "\n");
        }
        data.append("***END***\n");
        Log.d("Backup data - ", String.valueOf(data));
        try {
            if (isExternalStorageWritable() && isExternalStorageReadable()) {
                String filename = "backup_do_not_delete.txt";
                File dir = new File(Environment.getExternalStorageDirectory(),"XpensManager/Backup/");
                if(!dir.exists())
                    dir.mkdir();

                File output = new File(dir, filename);
                if (!output.exists()) {
                    try {
                        output.createNewFile();
                    } catch (Exception e) {
                        Log.d("Error", e.toString());
                        Toast.makeText(context, "Unable to make backup file. ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    output.delete();
                    try {
                        output.createNewFile();
                    } catch (Exception e) {
                        Log.d("Error", e.toString());
                        Toast.makeText(context, "Unable to make backup file. ", Toast.LENGTH_SHORT).show();
                    }
                }
                FileWriter writer = new FileWriter(output);
                writer.append(data);
                writer.flush();
                writer.close();
            } else {
                Toast.makeText(context, "Storage Not Accessible", Toast.LENGTH_LONG).show();
            }
        }
        catch(IOException e){
                Log.e("Exception While backup", "File write failed: " + e.toString());
            }
    }

    public void exportToExcel(){
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
                File dir = new File(Environment.getExternalStorageDirectory(), "XpensManager/ExcelExports/");
                if (!dir.exists())
                    dir.mkdir();
                String filename = "XpenseManagerExport_"+new SimpleDateFormat("dd_MM_yyyy_HH_MM", Locale.ENGLISH).format(new Date())+".xlsx";
                File output = new File(dir, filename);
                if (!output.exists())
                    output.createNewFile();
                else {
                    output.delete();
                    output.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(output);
                workbook.write(out);
                out.close();
            }
        }catch(Exception e){
            Log.d("ExportToExcel",e.toString());
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

}

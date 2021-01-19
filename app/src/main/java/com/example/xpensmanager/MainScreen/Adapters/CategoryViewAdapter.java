package com.example.xpensmanager.MainScreen.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensmanager.Database.CategoryDB;
import com.example.xpensmanager.Database.CategoryData;
import com.example.xpensmanager.Database.ExpenseDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.Fragments.Category;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;
import com.example.xpensmanager.SplashScreen.SplashScreenActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class CategoryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<CategoryData> results;
    private LayoutInflater mInflater;
    private GroupViewAdapter.ItemClickListener mClickListener;
    private Context context;
    private ArrayList<Boolean> list;
    private CategoryDB categoryDB;
    private ExpenseDB expenseDB;

    // data is passed into the constructor
    public CategoryViewAdapter(Context context, ArrayList<CategoryData> results, ArrayList<Boolean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.results = results;
        this.context = context;
        this.list = list;
        categoryDB = new CategoryDB(context);
        expenseDB = new ExpenseDB(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_view_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        double currentMonthTotal = expenseDB.getMonthTotalForCategory(results.get(position).getCategory(), ExpenseDB.getMonthFromDate(new Date()), ExpenseDB.getYearFromDate(new Date()));
        ((ViewHolder) holder).title.setText(results.get(position).getCategory());
        ((ViewHolder) holder).totalAmount.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(currentMonthTotal));
        ((ViewHolder) holder).lifeTimeSpend.setText(SplashScreenActivity.cSymbol+ " "+ new DecimalFormat("00.00").format(results.get(position).getTotalCategorySpend()));
        double limit = results.get(position).getLimit();
        double percentageLimit = ((currentMonthTotal/limit)*100);
        if(percentageLimit<=50){
            //((ViewHolder) holder).warning.setText(String.format("You have used %s%% of your limit [%s]", new DecimalFormat("00.00").format(percentageLimit),new DecimalFormat("00.00").format(limit)));
            //((ViewHolder) holder).warningIcon.setImageDrawable(context.getDrawable(R.drawable.cool));
            ((ViewHolder) holder).warningIcon.setVisibility(View.GONE);
            ((ViewHolder) holder).warning.setVisibility(View.GONE);
        }else if(percentageLimit>50 && percentageLimit <=75){
            ((ViewHolder) holder).warning.setText(String.format("You have used %s%% of your limit (%s)", new DecimalFormat("00.00").format(percentageLimit),new DecimalFormat("00.00").format(limit)));
            ((ViewHolder) holder).warningIcon.setImageDrawable(context.getDrawable(R.drawable.warning));
        }else if (percentageLimit>75 && percentageLimit <=100){
            ((ViewHolder) holder).warning.setText(String.format("You have used %s%% of your limit (%s)", new DecimalFormat("00.00").format(percentageLimit),new DecimalFormat("00.00").format(limit)));
            ((ViewHolder) holder).warningIcon.setImageDrawable(context.getDrawable(R.drawable.danger));
        }else{
            ((ViewHolder) holder).warning.setText(String.format("You have exceed this month's limit (%s)",new DecimalFormat("00.00").format(limit)));
            ((ViewHolder) holder).warningIcon.setImageDrawable(context.getDrawable(R.drawable.forbidden));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((ViewHolder) holder).progress.setProgress((int)percentageLimit,true);
        }else{
            ((ViewHolder) holder).progress.setProgress((int)percentageLimit);
        }
        ((ViewHolder) holder).limitUsed.setText(String.format("%s/%s",(int)currentMonthTotal,(int)limit));
        ((ViewHolder) holder).bind(list.get(position));
        holder.itemView.setOnClickListener(v -> {
            boolean expanded = list.get(position);
            list.set(position,!expanded);
            notifyItemChanged(position);
        });
        if(list.get(position)) {

            ((ViewHolder) holder).viewXpens.setOnClickListener((v) -> {
                Intent intent = new Intent(context, Expense.class);
                intent.putExtra("tableName","self_expense");
                intent.putExtra("viewType", ViewType.MONTHLY);
                intent.putExtra("groupBy",results.get(position).getCategory());
                intent.putExtra("filterType","category");
                intent.putExtra("filterValue", ExpenseDB.getMonthFromDate(new Date()));
                context.startActivity(intent);
            });

            ((ViewHolder) holder).editXpens.setOnClickListener((v) -> {
                LayoutInflater factory = LayoutInflater.from(context);
                final View dialogView = factory.inflate(R.layout.update_category_details, null);
                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                TextView categoryName = dialogView.findViewById(R.id.categoryName);
                EditText maxLimit = dialogView.findViewById(R.id.categoryLimit);
                categoryName.setText(results.get(position).getCategory());
                maxLimit.setText(results.get(position).getLimit()+"");
                dialog.setView(dialogView);
                dialogView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic
                        if(maxLimit.getText().toString().equalsIgnoreCase("") || maxLimit.getText().toString() == null) {
                                    maxLimit.setError("Cannot be blank");
                                }else{
                                    double tempLimit = Double.parseDouble(maxLimit.getText().toString());
                                    if(tempLimit <= 0 ){
                                        maxLimit.setError("Cannot be 0 or less");
                                    }else{
                                        double originalLimit = results.get(position).getLimit();
                                        if(originalLimit == tempLimit ){
                                            //No change
                                            dialog.dismiss();
                                        }else{
                                            categoryDB.updateCategoryLimitByCategoryName(results.get(position).getCategory(),tempLimit);
                                            results.get(position).setLimit(tempLimit);
                                            notifyItemChanged(position);
                                            dialog.dismiss();
                                        }
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

            ((ViewHolder) holder).deleteXpens.setOnClickListener((v) -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?\nThis will only delete the category, all the related expenses will still be maintained.")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                categoryDB.deleteCategoryByTitle(results.get(position).getCategory());
                                results.remove(position);
                                list.remove(position);
                                notifyDataSetChanged();
                                if(results.size()==0){
                                    Category.emptyView.setVisibility(View.VISIBLE);
                                }
                                MainActivity.updateCategoryList();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(context.getResources().getDrawable(R.drawable.delete))
                        .show();
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView title, totalAmount, lifeTimeSpend, warning,limitUsed;
        ImageView warningIcon;
        LinearLayout linearLayout;
        CardView parentCardView;
        ProgressBar progress;
        AppCompatImageButton viewXpens,editXpens,deleteXpens;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
            totalAmount = itemView.findViewById(R.id.totalSpendsThisMonth);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            parentCardView = itemView.findViewById(R.id.parentCardView);
            lifeTimeSpend = itemView.findViewById(R.id.lifeTimeSpend);
            warning = itemView.findViewById(R.id.warning);
            warningIcon = itemView.findViewById(R.id.warningIcon);
            progress = itemView.findViewById(R.id.progress);
            limitUsed = itemView.findViewById(R.id.limitUsed);
        }

        private void bind(boolean expanded) {
            // Set the visibility based on state
            Transition transition = new Slide(Gravity.TOP);
            transition.setDuration(200);
            transition.addTarget(linearLayout);
            TransitionManager.beginDelayedTransition(parentCardView, transition);
            if(expanded){
                viewXpens = itemView.findViewById(R.id.viewXpens);
                editXpens = itemView.findViewById(R.id.editXpens);
                deleteXpens = itemView.findViewById(R.id.deleteXpens);
            }
            linearLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }
}

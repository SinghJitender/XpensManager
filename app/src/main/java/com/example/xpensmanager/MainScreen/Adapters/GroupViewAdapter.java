package com.example.xpensmanager.MainScreen.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.Fragments.HomePage;
import com.example.xpensmanager.MainScreen.MainActivity;
import com.example.xpensmanager.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class GroupViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Hashtable<String,String>> results;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private ArrayList<Boolean> list;
    private GroupDB groupDB;

    // data is passed into the constructor
    public GroupViewAdapter(Context context, ArrayList<Hashtable<String,String>> results, ArrayList<Boolean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.results = results;
        this.context = context;
        this.list = list;
        groupDB = new GroupDB(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view; //= mInflater.inflate(R.layout.homepage_recycle_view, parent, false);
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        double currentMonthTotal = Double.parseDouble(results.get(position).get("currentMonthTotal"));
        int nPeps = Integer.parseInt(results.get(position).get("noOfPersons"));
        ((ViewHolder) holder).title.setText(results.get(position).get("title"));
        ((ViewHolder) holder).totalAmount.setText("₹ "+currentMonthTotal);
        ((ViewHolder) holder).lifeTimeSpend.setText("₹ "+results.get(position).get("totalAmount"));
        ((ViewHolder) holder).splitBetween.setText(nPeps<=1? nPeps+ " Person" : "Split Between "+ nPeps + " People");
        double net_amount = Double.parseDouble(results.get(position).get("netAmount"));
        if(net_amount>=0){
            ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_green));
            ((ViewHolder) holder).netAmount.setText("+"+net_amount);
        }else {
            ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_red));
            ((ViewHolder) holder).netAmount.setText(""+net_amount);
        }
        double limit = Double.parseDouble(results.get(position).get("maxLimit"));
        double percentageLimit = ((currentMonthTotal/limit)*100);
        if(percentageLimit<=50){
            //((ViewHolder) holder).warning.setText(String.format("You have used %s%% of your limit (%s)", new DecimalFormat("00.00").format(percentageLimit),new DecimalFormat("00.00").format(limit)));
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
            if(net_amount>0){
                ((ViewHolder) holder).info.setText(results.get(position).get("title") + " owes you "+net_amount);
            }else if (net_amount<0){
                ((ViewHolder) holder).info.setText("You owe "+(-1*net_amount)+" to "+results.get(position).get("title"));
            }else{
                ((ViewHolder) holder).info.setText("No Dues");
            }
            ((ViewHolder) holder).viewXpens.setOnClickListener((v) -> {
                Intent intent = new Intent(context, Expense.class);
                intent.putExtra("tableName","self_expense");
                intent.putExtra("viewType", ViewType.MONTHLY);
                intent.putExtra("groupBy",results.get(position).get("title"));
                intent.putExtra("filterType","group");
                intent.putExtra("filterValue", GenericExpenseDB.getMonthFromDate(new Date()));
                context.startActivity(intent);
            });

            ((ViewHolder) holder).editXpens.setOnClickListener((v) -> {
                Toast.makeText(context, "Edit Expense", Toast.LENGTH_SHORT).show();
                LayoutInflater factory = LayoutInflater.from(context);
                final View dialogView = factory.inflate(R.layout.update_group_details, null);
                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                TextView groupName = dialogView.findViewById(R.id.groupName);
                EditText noOfPeps = dialogView.findViewById(R.id.noofpeps);
                EditText maxLimit = dialogView.findViewById(R.id.limit);
                groupName.setText(results.get(position).get("title"));
                noOfPeps.setText(results.get(position).get("noOfPersons"));
                maxLimit.setText(results.get(position).get("maxLimit"));
                dialog.setView(dialogView);
                dialogView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic
                        if(noOfPeps.getText().toString().equalsIgnoreCase("") || noOfPeps.getText().toString() == null) {
                            noOfPeps.setError("Cannot be blank");
                        } else{
                            int tempNoOfPersons = Integer.parseInt(noOfPeps.getText().toString());
                            if(tempNoOfPersons <= 0 ){
                                noOfPeps.setError("Cannot be 0 or less");
                            }else{
                                if(maxLimit.getText().toString().equalsIgnoreCase("") || maxLimit.getText().toString() == null) {
                                    maxLimit.setError("Cannot be blank");
                                }else{
                                    double tempLimit = Double.parseDouble(maxLimit.getText().toString());
                                    if(tempLimit <= 0 ){
                                        maxLimit.setError("Cannot be 0 or less");
                                    }else{
                                        int originalNoOfPersons = Integer.parseInt(results.get(position).get("noOfPersons"));
                                        int originalLimit = Integer.parseInt(results.get(position).get("maxLimit"));
                                        if(originalLimit == tempLimit && originalNoOfPersons == tempNoOfPersons){
                                            //No change
                                            dialog.dismiss();
                                        }else{
                                            groupDB.updateGroupLimitAndPersons(results.get(position).get("title"),tempNoOfPersons,tempLimit);
                                            results.get(position).put("maxLimit",new DecimalFormat("00.00").format(tempLimit));
                                            results.get(position).put("noOfPersons",tempNoOfPersons+"");
                                            notifyItemChanged(position);
                                            dialog.dismiss();
                                        }
                                    }
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

            ((ViewHolder) holder).settleXpens.setOnClickListener((v) -> {
                new AlertDialog.Builder(context)
                        .setTitle("Settle Expense")
                        .setMessage("This will reset the dues. Proceed if you have settled the dues.")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                groupDB.updateNetAmountByTitle(results.get(position).get("title"));
                                results.get(position).put("netAmount","00.00");
                                notifyItemChanged(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(context.getResources().getDrawable(R.drawable.check))
                        .show();
            });

            ((ViewHolder) holder).deleteXpens.setOnClickListener((v) -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Group")
                        .setMessage("Are you sure you want to delete this group?\nThis will only delete the group, all the related expenses will still be maintained.")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                groupDB.deleteGroupByTitle(results.get(position).get("title"));
                                results.remove(position);
                                list.remove(position);
                                notifyItemChanged(position);
                                MainActivity.updateGroupList();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(context.getResources().getDrawable(R.drawable.delete))
                        .show();
            });
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return results.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView title, totalAmount, netAmount, lifeTimeSpend, warning , info,splitBetween,limitUsed;
        ImageView warningIcon;
        LinearLayout linearLayout;
        CardView parentCardView;
        ProgressBar progress;
        AppCompatImageButton viewXpens,editXpens,settleXpens,deleteXpens;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expenseTitle);
            totalAmount = itemView.findViewById(R.id.totalSpendsThisMonth);
            netAmount = itemView.findViewById(R.id.netSpendsThisMonth);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            parentCardView = itemView.findViewById(R.id.parentCardView);
            lifeTimeSpend = itemView.findViewById(R.id.lifeTimeSpend);
            warning = itemView.findViewById(R.id.warning);
            warningIcon = itemView.findViewById(R.id.warningIcon);
            splitBetween = itemView.findViewById(R.id.splitBetween);
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
                settleXpens = itemView.findViewById(R.id.settleXpens);
                deleteXpens = itemView.findViewById(R.id.deleteXpens);
                info = itemView.findViewById(R.id.info);
            }
            linearLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}

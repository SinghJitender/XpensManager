package com.example.xpensmanager.MainScreen.Adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensmanager.Database.GenericExpenseDB;
import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.Enums.ViewType;
import com.example.xpensmanager.ExpenseScreen.Expense;
import com.example.xpensmanager.MainScreen.Fragments.HomePage;
import com.example.xpensmanager.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Hashtable<String,String>> results;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private ArrayList<Boolean> list;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, ArrayList<Hashtable<String,String>> results, ArrayList<Boolean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.results = results;
        this.context = context;
        this.list = list;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view; //= mInflater.inflate(R.layout.homepage_recycle_view, parent, false);
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycle_view, parent, false);
        return new ViewHolder(view);

        //return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).title.setText(results.get(position).get("title"));
        ((ViewHolder) holder).totalAmount.setText("₹ "+results.get(position).get("currentMonthTotal"));
        ((ViewHolder) holder).lifeTimeSpend.setText("₹ "+results.get(position).get("totalAmount"));
        double net_amount = Double.parseDouble(results.get(position).get("netAmount"));
        if(net_amount>=0){
            ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_green));
            ((ViewHolder) holder).netAmount.setText("+"+net_amount);
        }else {
            ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_red));
            ((ViewHolder) holder).netAmount.setText(""+net_amount);
        }
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
                intent.putExtra("filterValue", GenericExpenseDB.getMonthFromDate(new Date()));
                context.startActivity(intent);
            });

            ((ViewHolder) holder).editXpens.setOnClickListener((v) -> {
                Toast.makeText(context, "Edit Expense", Toast.LENGTH_SHORT).show();
            });

            ((ViewHolder) holder).settleXpens.setOnClickListener((v) -> {
                Toast.makeText(context, "Settle Expense", Toast.LENGTH_SHORT).show();
            });

            ((ViewHolder) holder).deleteXpens.setOnClickListener((v) -> {
                Toast.makeText(context, "Delete Expense", Toast.LENGTH_SHORT).show();
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
        TextView title, totalAmount, netAmount, lifeTimeSpend, warning , info;
        ImageView warningIcon;
        LinearLayout linearLayout;
        CardView parentCardView;
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
        }

        private void bind(boolean expanded) {
            // Set the visibility based on state
            Transition transition = new Slide(Gravity.TOP);
            transition.setDuration(200);
            transition.setInterpolator(new AccelerateInterpolator());
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

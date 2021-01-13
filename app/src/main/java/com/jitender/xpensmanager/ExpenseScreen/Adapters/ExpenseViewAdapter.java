package com.jitender.xpensmanager.ExpenseScreen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jitender.xpensmanager.Database.ExpenseData;
import com.jitender.xpensmanager.Enums.ViewType;
import com.jitender.xpensmanager.R;
import com.jitender.xpensmanager.SplashScreen.SplashScreenActivity;

import java.util.ArrayList;

public class ExpenseViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ExpenseData> list;
    Context context;
    ViewType viewType;

    public ExpenseViewAdapter(ArrayList<ExpenseData> list, Context context, ViewType viewType) {
        this.list = list;
        this.context = context;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_view_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).description.setText(list.get(position).getDescription());
        ((ViewHolder) holder).splitamount.setText(SplashScreenActivity.cSymbol+ " "+list.get(position).getSplitAmount());
        if(viewType == ViewType.MONTHLY){
            ((ViewHolder) holder).date.setVisibility(View.GONE);
        }else if (viewType == ViewType.YEARLY){
            ((ViewHolder) holder).date.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).date.setText(list.get(position).getDate());
        }
        else {
            ((ViewHolder) holder).date.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).date.setText(list.get(position).getTextMonth());
        }
        if(list.get(position).getGroup().equalsIgnoreCase("OWN")) {
            ((ViewHolder) holder).amount.setText(SplashScreenActivity.cSymbol+ " "+list.get(position).getAmount() + " paid by "+list.get(position).getPaidBy());
        }
        else{
            ((ViewHolder) holder).amount.setText(SplashScreenActivity.cSymbol+ " "+list.get(position).getAmount()+ " split with "+list.get(position).getGroup()+ " paid by "+list.get(position).getPaidBy());
        }
        ((ViewHolder) holder).category.setText(list.get(position).getCategory());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, splitamount, date,amount,category;

        ViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            splitamount = itemView.findViewById(R.id.splitamount);
            amount = itemView.findViewById(R.id.amount);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void restoreItem(ExpenseData item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<ExpenseData> getData() {
        return list;
    }
}
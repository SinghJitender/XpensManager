package com.example.xpensmanager.MainScreen.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensmanager.Database.GroupDB;
import com.example.xpensmanager.MainScreen.Fragments.HomePage;
import com.example.xpensmanager.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Hashtable<String,String>> results;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    GroupDB groups;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, ArrayList<Hashtable<String,String>> results) {
        this.mInflater = LayoutInflater.from(context);
        this.results = results;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view; //= mInflater.inflate(R.layout.homepage_recycle_view, parent, false);
        if(viewType == 1){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycle_menu, parent, false);
            return new MenuViewHolder(view);
        }else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_recycle_view, parent, false);
            return new ViewHolder(view);
        }
        //return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if( holder instanceof ViewHolder) {
            ((ViewHolder) holder).title.setText(results.get(position).get("title"));
            ((ViewHolder) holder).totalAmount.setText("₹ "+results.get(position).get("totalAmount"));

            double net_amount = Double.parseDouble(results.get(position).get("netAmount"));
            if(net_amount>=0){
                ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_green));
                ((ViewHolder) holder).netAmount.setText("+"+net_amount);
            }else {
                ((ViewHolder) holder).netAmount.setTextColor(context.getResources().getColor(R.color.theme_red));
                ((ViewHolder) holder).netAmount.setText("+"+net_amount);
            }
        }
        if(holder instanceof MenuViewHolder){
            ((MenuViewHolder) holder).add.setOnClickListener((v)->{
                Toast.makeText(context,"Clicked on add button",Toast.LENGTH_SHORT).show();
                HomePage.update_add_new_expense_title(results.get(position).get("title"));
            });

            ((MenuViewHolder) holder).edit.setOnClickListener((v)->{
                Toast.makeText(context,"Clicked on edit button",Toast.LENGTH_SHORT).show();
            });

            ((MenuViewHolder) holder).settle.setOnClickListener((v)->{
                Toast.makeText(context,"Clicked on settle button",Toast.LENGTH_SHORT).show();
            });

            ((MenuViewHolder) holder).delete.setOnClickListener((v)->{
                Toast.makeText(context,"Clicked on delete button",Toast.LENGTH_SHORT).show();
            });
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return results.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, totalAmount, netAmount;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expenseTitle);
            totalAmount = itemView.findViewById(R.id.totalSpendsThisMonth);
            netAmount = itemView.findViewById(R.id.netSpendsThisMonth);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        ImageButton add,edit,settle,delete;
        MenuViewHolder(View view){
            super(view);
            add = itemView.findViewById(R.id.addXpens);
            edit = itemView.findViewById(R.id.editXpens);
            settle = itemView.findViewById(R.id.settleXpens);
            delete = itemView.findViewById(R.id.deleteXpens);
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

    @Override
    public int getItemViewType(int position) {
        if(results.get(position).get("showMenu").equals("true")){
            return SHOW_MENU;
        }else{
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        results.get(position).put("showMenu","true");
        notifyDataSetChanged();
    }

    public void hideMenu(int position) {
        results.get(position).put("showMenu","false");
        notifyDataSetChanged();
    }

}

package com.example.xpensmanager.MainScreen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.xpensmanager.R;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> titleData;
    private List<String> amountData;
    private List<String> netData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, ArrayList<String> titleData,ArrayList<String> totalAmount,ArrayList<String> netAmount) {
        this.mInflater = LayoutInflater.from(context);
        this.titleData = titleData;
        this.amountData = totalAmount;
        this.netData = netAmount;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.homepage_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(titleData.get(position));
        holder.totalAmount.setText(amountData.get(position));
        holder.netAmount.setText(netData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return titleData.size();
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

    // convenience method for getting data at click position
    String getItem(int id) {
        return titleData.get(id);
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
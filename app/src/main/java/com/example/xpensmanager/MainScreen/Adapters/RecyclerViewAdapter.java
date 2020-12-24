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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> titleData;
    private List<String> amountData;
    private List<String> netData;
    private List<Boolean> showM;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, ArrayList<String> titleData,ArrayList<String> totalAmount,ArrayList<String> netAmount, ArrayList<Boolean> showM) {
        this.mInflater = LayoutInflater.from(context);
        this.titleData = titleData;
        this.amountData = totalAmount;
        this.netData = netAmount;
        this.showM = showM;
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

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        MenuViewHolder(View view){
            super(view);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if( holder instanceof ViewHolder) {
            ((ViewHolder) holder).title.setText(titleData.get(position));
            ((ViewHolder) holder).totalAmount.setText(amountData.get(position));
            ((ViewHolder) holder).netAmount.setText(netData.get(position));
        }
        if(holder instanceof MenuViewHolder){

        }
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

    @Override
    public int getItemViewType(int position) {
        if(showM.get(position)){
            return SHOW_MENU;
        }else{
            return HIDE_MENU;
        }
    }


    public void showMenu(int position) {
        showM.set(position,true);
        notifyDataSetChanged();
    }

    public void hideMenu(int position) {
        showM.set(position,false);
        notifyDataSetChanged();
    }


   /* public boolean isMenuShown() {
        for(int i=0; i<showM.size(); i++){
            if(showM.get(i)){
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for(int i=0; i<showM.size(); i++){
            showM.add(i,false);
        }
        notifyDataSetChanged();
    }*/
}

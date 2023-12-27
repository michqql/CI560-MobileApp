package com.example.myapplication.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ItemModel;
import com.example.myapplication.model.ShoppingListModel;

import java.util.ArrayList;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private final ArrayList<ItemModel> needs = new ArrayList<>();
    private final ArrayList<ItemModel> purchased = new ArrayList<>();

    public PurchaseAdapter(ShoppingListModel model) {
        needs.addAll(model.getItemList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_purchase_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Invalid position - will throw index out of bounds exception
        if(position >= getItemCount()) return;

        // If position is >= needs size, then it must be in the purchase list
        if(position >= needs.size()) holder.bind(purchased.get(position - needs.size()), true);
        else holder.bind(needs.get(position), false);
    }

    @Override
    public int getItemCount() {
        return needs.size() + purchased.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.itemName);
            image = itemView.findViewById(R.id.checkBox);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(final ItemModel itemModel, final boolean purchased) {
            text.setText(itemView.getResources().getString(R.string.purchase_item_title, itemModel.getName()));
            text.setTextColor(purchased ? 0xff00ff00 : 0xff000000);

            itemView.setOnClickListener(v -> {
                if(purchased) {
                    PurchaseAdapter.this.purchased.remove(itemModel);
                    PurchaseAdapter.this.needs.add(itemModel);
                    notifyDataSetChanged();
                } else {
                    PurchaseAdapter.this.needs.remove(itemModel);
                    PurchaseAdapter.this.purchased.add(itemModel);
                    notifyDataSetChanged();
                }
            });
        }
    }
}

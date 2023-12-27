package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ItemModel;
import com.example.myapplication.model.ShoppingListModel;

import java.util.function.BiConsumer;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_OBJECT = 1;

    private final ShoppingListModel model;
    private BiConsumer<ItemModel, Integer> deleteItemConsumer;

    public ItemListAdapter(ShoppingListModel model) {
        this.model = model;
    }

    public void setDeleteItemConsumer(BiConsumer<ItemModel, Integer> deleteItemConsumer) {
        this.deleteItemConsumer = deleteItemConsumer;
    }

    @Override
    public int getItemViewType(int position) {
        if(model.getItemList().isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_OBJECT;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_OBJECT ? R.layout.recycler_view_item_row :
                R.layout.recycler_view_empty_item_row;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if(model.getItemList().isEmpty())
            return;

        holder.bind(model.getItemList().get(position), position);
    }

    @Override
    public int getItemCount() {
        return model.getNumItems();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final ImageView category, delete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            category = itemView.findViewById(R.id.category_image);
            delete = itemView.findViewById(R.id.delete_image);
        }

        public void bind(ItemModel itemModel, int index) {
            if(itemModel == null) return;

            name.setText(itemModel.getName());
            if(itemModel.getCategory() != null) {
                category.setImageDrawable(AppCompatResources
                        .getDrawable(itemView.getContext(), itemModel.getCategory().getDrawableId()));
            }
            // Set a click listener to the delete image
            delete.setOnClickListener(v -> {
                if(ItemListAdapter.this.deleteItemConsumer != null) {
                    ItemListAdapter.this.deleteItemConsumer.accept(itemModel, index);
                }
            });
        }
    }
}

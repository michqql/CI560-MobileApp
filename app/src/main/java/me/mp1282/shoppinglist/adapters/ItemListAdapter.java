package me.mp1282.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import me.mp1282.shoppinglist.R;
import me.mp1282.shoppinglist.model.ItemModel;
import me.mp1282.shoppinglist.model.ShoppingListModel;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_OBJECT = 1;

    private final ShoppingListModel model;

    public ItemListAdapter(ShoppingListModel model) {
        this.model = model;
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

        holder.bind(model.getItemList().get(position));
    }

    @Override
    public int getItemCount() {
        return model.getNumItems();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final ImageView category;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            category = itemView.findViewById(R.id.category_image);
        }

        public void bind(ItemModel itemModel) {
            if(itemModel == null) return;

            name.setText(itemModel.getName());
            if(itemModel.getCategory() != null) {
                category.setImageDrawable(AppCompatResources
                        .getDrawable(itemView.getContext(), itemModel.getCategory().getDrawableId()));
            }
        }
    }
}

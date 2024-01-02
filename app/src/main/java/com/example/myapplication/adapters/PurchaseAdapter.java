package com.example.myapplication.adapters;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ItemModel;
import com.example.myapplication.model.ShoppingListModel;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private final LinkedList<ItemModelWrapper> needs = new LinkedList<>();
    private final ArrayList<ItemModelWrapper> purchased = new ArrayList<>();

    public PurchaseAdapter(ShoppingListModel model) {
        List<ItemModel> copy = new ArrayList<>(model.getItemList());
        // Sort copy by purchasing power in reverse order
        copy.sort((o1, o2) -> Math.toIntExact(o2.getPurchasePriority() - o1.getPurchasePriority()));

        // Add items to our linked list and wrap them in the ItemModelWrapper
        for(int i = 0; i < copy.size(); i++) {
            ItemModel item = copy.get(i);

            ItemModelWrapper wrapper = new ItemModelWrapper(item, i);
            needs.addLast(wrapper); // Same method call as LinkedList.add()
        }
    }

    public void updateItemRankings() {
        // Sort the list of purchased items according to which was bought first
        // This sort will be in reverse order (o2 - o1) because smaller timestamps occur
        // first on the timeline
        purchased.sort((o1, o2) -> Math.toIntExact(o2.purchaseTimestamp - o1.purchaseTimestamp));

        for(int i = 0; i < purchased.size(); i++) {
            ItemModel item = purchased.get(i).item;
            item.setPurchasePriority(item.getPurchasePriority() + (i + 1 + needs.size()));
        }
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
        public void bind(final ItemModelWrapper wrapper, final boolean purchased) {
            text.setText(itemView.getResources().getString(
                    R.string.purchase_item_title, wrapper.item.getName()));
            text.setTextColor(getTextColour(purchased));

            if(purchased) {
                image.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.vector_tick));
                image.setColorFilter(itemView.getContext().getColor(R.color.purchasedGreen));
            } else {
                image.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_button));
                image.setColorFilter(itemView.getContext().getColor(R.color.black));
            }

            itemView.setOnClickListener(v -> {
                if(purchased) {
                    // Item was un-purchased
                    wrapper.purchaseTimestamp = 0;
                    PurchaseAdapter.this.purchased.remove(wrapper);
                    insertItemModelIntoToBuyList(wrapper);
                } else {
                    // Item has just been bought
                    wrapper.purchaseTimestamp = System.currentTimeMillis();
                    PurchaseAdapter.this.needs.remove(wrapper);
                    PurchaseAdapter.this.purchased.add(wrapper);
                }
                notifyDataSetChanged();
            });
        }

        /**
         * <p>
         *     Programmatically gets the correct colour for the text, which can changes depending
         *     on whether the device is on light or dark mode. Normally this would not be an issue,
         *     but because the text colour is being set programmatically to green if it has been
         *     purchased, we must get the correct colour to display if it is not purchased.
         * </p>
         * @param purchased {@code True} if the item has been purchased/ticked-off
         * @return the colour as an int value
         */
        private int getTextColour(boolean purchased) {
            if(purchased)
                return itemView.getContext().getColor(R.color.purchasedGreen);

            int nightModeFlags = itemView.getContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                return itemView.getContext().getColor(R.color.white);
            } else {
                return itemView.getContext().getColor(R.color.black);
            }
        }
    }

    private void insertItemModelIntoToBuyList(ItemModelWrapper wrapper) {
        needs.add(wrapper);
        needs.sort(Comparator.comparingInt(o -> o.position));
    }

    private static class ItemModelWrapper {
        final ItemModel item;
        final int position;

        long purchaseTimestamp;

        public ItemModelWrapper(ItemModel item, int position) {
            this.item = item;
            this.position = position;
        }

        public void setPurchaseTimestamp(long purchaseTimestamp) {
            this.purchaseTimestamp = purchaseTimestamp;
        }

        public long getPurchaseTimestamp() {
            return purchaseTimestamp;
        }
    }
}

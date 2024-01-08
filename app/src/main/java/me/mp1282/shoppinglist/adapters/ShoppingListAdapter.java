package me.mp1282.shoppinglist.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.mp1282.shoppinglist.R;
import me.mp1282.shoppinglist.model.ShoppingListModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ListViewHolder> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final long ONE_WEEK_IN_MILLIS = ONE_DAY_IN_MILLIS * 7;
    private static final long ONE_MONTH_IN_MILLIS = ONE_WEEK_IN_MILLIS * 30;

    private final ArrayList<ShoppingListModel> list;
    private final Consumer<ShoppingListModel> shoppingListClickEvent;

    public ShoppingListAdapter(ArrayList<ShoppingListModel> models,
                               Consumer<ShoppingListModel> shoppingListClickEvent) {
        this.list = models;
        this.shoppingListClickEvent = shoppingListClickEvent;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_shopping_list_row, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if(list.size() == 0) return;

        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView, numItemsView, dateView;
        private ShoppingListModel model;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            titleView = itemView.findViewById(R.id.itemTitle);
            numItemsView = itemView.findViewById(R.id.itemNumber);
            dateView = itemView.findViewById(R.id.itemDate);
        }

        public void bind(ShoppingListModel model) {
            Resources res = itemView.getResources();

            this.model = model;
            titleView.setText(model.getName());
            numItemsView.setText(res.getQuantityString(R.plurals.number_of_items, model.getNumItems(), model.getNumItems()));

            String date = res.getString(R.string.never_purchased);
            findResult: if(model.getLastPurchasedDate() > 0) {
                long diff = System.currentTimeMillis() - model.getLastPurchasedDate();
                double result = (double) diff / ONE_MONTH_IN_MILLIS;
                if(result >= 1) {
                    date = res.getString(R.string.months_ago, DECIMAL_FORMAT.format(result));
                    break findResult;
                }

                result = (double) diff / ONE_WEEK_IN_MILLIS;
                if(result >= 1) {
                    date = res.getString(R.string.weeks_ago, DECIMAL_FORMAT.format(result));
                    break findResult;
                }

                result = (double) diff / ONE_DAY_IN_MILLIS;
                date = res.getString(R.string.days_ago, DECIMAL_FORMAT.format(result));
            }

            dateView.setText(itemView.getResources().getString(R.string.last_purchase_date, date));
        }

        @Override
        public void onClick(View v) {
            ShoppingListAdapter.this.shoppingListClickEvent.accept(model);
        }
    }
}

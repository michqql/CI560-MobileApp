package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.adapters.PurchaseAdapter;
import com.example.myapplication.db.DBHelper;
import com.example.myapplication.model.ShoppingListModel;

public class PurchaseListActivity extends AppCompatActivity {

    private ShoppingListModel model;
    private PurchaseAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        long id = getIntent().getLongExtra("shoppingListModelId", -1);
        model = ShoppingListModel.getModelById(id);
        if(model == null) throw new NullPointerException("ShoppingListModel cannot be null!");

        TextView titleText = findViewById(R.id.purchase_title);
        titleText.setText(getString(R.string.purchase_list_title, model.getName()));

        Button backButton = findViewById(R.id.purchase_done_button);
        backButton.setOnClickListener(v -> finishPurchasing());

        RecyclerView recyclerView = findViewById(R.id.purchase_list);
        recyclerView.setAdapter(recyclerAdapter = new PurchaseAdapter(model));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
    }

    private void finishPurchasing() {
        model.setLastPurchasedDate(System.currentTimeMillis());
        recyclerAdapter.updateItemRankings();
        DBHelper.getInstance(this).saveShoppingList(model);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
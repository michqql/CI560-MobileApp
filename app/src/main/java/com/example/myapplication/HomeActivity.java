package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.shopping.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout linearLayout = findViewById(R.id.list);

        for(int i = 0; i < 10; i++) {
            ShoppingList list = new ShoppingList(i);
            list.setName("Shopping List #" + (i + 1));
            List<String> arr = new ArrayList<>();
            for(int j = 0; j < i; j++) {
                arr.add(String.valueOf(j));
            }
            list.setItems(arr);
            linearLayout.addView(createContainer(list));
        }

    }

    private View createContainer(ShoppingList list) {
        Resources res = getResources();

        RelativeLayout container = new RelativeLayout(this);
        TextView nameText = new TextView(this);
        nameText.setText(res.getString(
                R.string.shopping_list_container_text, list.getName()));
        nameText.setLeft(10);
        container.addView(nameText);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_RIGHT);

        TextView itemText = new TextView(this);
        itemText.setText(res.getQuantityString(R.plurals.number_of_items,
                list.getNumberOfItems(), list.getNumberOfItems()));
        itemText.setRight(10);
        container.addView(itemText, params);

        return container;
    }
}
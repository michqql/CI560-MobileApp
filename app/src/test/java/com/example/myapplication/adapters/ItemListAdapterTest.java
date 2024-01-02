package com.example.myapplication.adapters;

import static org.junit.Assert.*;

import com.example.myapplication.model.ItemModel;
import com.example.myapplication.model.ShoppingListModel;

import org.junit.Before;
import org.junit.Test;

public class ItemListAdapterTest {

    private static final int EXPECTED_ITEM_NUMBER = 10;
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_OBJECT = 1;

    private ItemListAdapter adapter;

    @Before
    public void setUp() {
        ShoppingListModel model = new ShoppingListModel("Test");
        model.setId(1);

        for(int i = 0; i < EXPECTED_ITEM_NUMBER; i++) {
            ItemModel itemModel = new ItemModel("Test Item: " + i);
            itemModel.setId(i);
            model.getItemList().add(itemModel);
        }

        this.adapter = new ItemListAdapter(model);
    }

    @Test
    public void getItemViewType() {
        for(int i = 0; i < EXPECTED_ITEM_NUMBER; i++) {
            assertEquals(VIEW_TYPE_OBJECT, adapter.getItemViewType(i));
        }

        assertEquals(VIEW_TYPE_EMPTY, adapter.getItemViewType(EXPECTED_ITEM_NUMBER + 1));
        assertNotEquals(VIEW_TYPE_OBJECT, adapter.getItemViewType(EXPECTED_ITEM_NUMBER + 1));
    }

    @Test
    public void onCreateViewHolder() {
    }

    @Test
    public void onBindViewHolder() {

    }

    @Test
    public void getItemCount() {
        assertEquals(EXPECTED_ITEM_NUMBER, adapter.getItemCount());
        assertNotEquals(EXPECTED_ITEM_NUMBER + 5, adapter.getItemCount());
    }
}
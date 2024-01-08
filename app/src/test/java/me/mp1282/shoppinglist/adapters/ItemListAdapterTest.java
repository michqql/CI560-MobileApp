package me.mp1282.shoppinglist.adapters;

import static org.junit.Assert.*;

import me.mp1282.shoppinglist.model.ItemModel;
import me.mp1282.shoppinglist.model.ShoppingListModel;

import org.junit.Before;
import org.junit.Test;

public class ItemListAdapterTest {

    private static final int EXPECTED_ITEM_NUMBER = 10;
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
    public void getItemCount() {
        assertEquals(EXPECTED_ITEM_NUMBER, adapter.getItemCount());
        assertNotEquals(EXPECTED_ITEM_NUMBER + 5, adapter.getItemCount());
    }

    @Test
    public void getItemViewType() {
        for(int i = 0; i < EXPECTED_ITEM_NUMBER; i++) {
            assertEquals(VIEW_TYPE_OBJECT, adapter.getItemViewType(i));
        }
    }
}
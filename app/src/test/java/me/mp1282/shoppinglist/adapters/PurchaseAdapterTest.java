package me.mp1282.shoppinglist.adapters;

import static org.junit.Assert.*;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.mp1282.shoppinglist.model.ItemModel;
import me.mp1282.shoppinglist.model.ShoppingListModel;

public class PurchaseAdapterTest {

    private PurchaseAdapter adapter;

    @Before
    public void setup() {
        ShoppingListModel model = new ShoppingListModel("Test");
        ItemModel item1 = new ItemModel("Item 1");
        ItemModel item2 = new ItemModel("Item 2");
        model.getItemList().add(item1);
        model.getItemList().add(item2);

        adapter = new PurchaseAdapter(model);
    }

    @Test
    public void getItemCount() {
        Assert.assertEquals(2, adapter.getItemCount());
    }
}
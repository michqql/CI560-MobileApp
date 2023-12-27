package com.example.myapplication.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingListModel {

    // Start of static
    private static int listCounter = 0;
    private static final ArrayList<ShoppingListModel> SHOPPING_LIST_MODELS = new ArrayList<>();
    public static ArrayList<ShoppingListModel> getShoppingListModels() {
        return SHOPPING_LIST_MODELS;
    }

    public static int getIndexById(long id) {
        for(int i = 0; i < SHOPPING_LIST_MODELS.size(); i++) {
            if(SHOPPING_LIST_MODELS.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    public static ShoppingListModel getModelById(long id) {
        for(ShoppingListModel model : SHOPPING_LIST_MODELS)
            if(model.getId() == id)
                return model;

        return null;
    }

    public static void setListCounter(int listCounter) {
        ShoppingListModel.listCounter = listCounter;
    }

    public static int getListCounter() {
        return listCounter;
    }

    public static int getAndIncrementListCounter() {
        return listCounter++;
    }
    // End of static

    private long id;
    private String name;
    private long lastPurchasedDate;
    private boolean deletedFlag;
    private long itemsCounter = 0;

    private ArrayList<ItemModel> itemList = new ArrayList<>();
    private ArrayList<ItemModel> deletedItemList = new ArrayList<>();

    public ShoppingListModel(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumItems() {
        return itemList.size();
    }

    public void setItemsCounter(long itemsCounter) {
        this.itemsCounter = itemsCounter;
    }

    public long getItemsCounter() {
        return itemsCounter;
    }

    public long getAndIncrementItemsCounter() {
        return itemsCounter++;
    }

    public long getLastPurchasedDate() {
        return lastPurchasedDate;
    }

    public void setLastPurchasedDate(long lastPurchasedDate) {
        this.lastPurchasedDate = lastPurchasedDate;
    }

    public List<ItemModel> getItemList() {
        return itemList;
    }

    public boolean isDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    /**
     * <p>
     *     Returns the last {@link ItemModel} in the list.
     *     The last item model will usually be the most recently added model.
     *     Returns as an optional as the list may be empty and therefore
     *     this item model may be null!
     * </p>
     * @return - optional of the item model
     */
    public Optional<ItemModel> getLastItemModel() {
        if(itemList.isEmpty())
            return Optional.empty();

        return Optional.of(itemList.get(itemList.size() - 1));
    }

    public ArrayList<ItemModel> getDeletedItemList() {
        return deletedItemList;
    }
}

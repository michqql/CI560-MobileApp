package com.example.myapplication.shopping;

import java.util.Date;
import java.util.List;

public class ShoppingList {

    private final long id; // Unique id for this shopping list
    private String name; // Name of list
    private Date lastPurchaseDate; // The last time this list was used

    // The number of times this shopping list has been bought
    private int boughtCount;

    private List<String> items;

    public ShoppingList(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public int getBoughtCount() {
        return boughtCount;
    }

    public int getNumberOfItems() {
        return items.size();
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}

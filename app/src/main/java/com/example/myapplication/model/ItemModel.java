package com.example.myapplication.model;

public class ItemModel {

    private long id;
    private String name;
    private CategoryModel category;
    private long purchasePriority;
    private boolean deletedFlag;

    public ItemModel(String name) {
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

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public boolean isEmpty() {
        return name == null || name.isEmpty();
    }

    public long getPurchasePriority() {
        return purchasePriority;
    }

    public void setPurchasePriority(long purchasePriority) {
        this.purchasePriority = purchasePriority;
    }

    public boolean isDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}

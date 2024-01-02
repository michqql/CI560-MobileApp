package com.example.myapplication.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.model.CategoryModel;
import com.example.myapplication.model.ItemModel;
import com.example.myapplication.model.ShoppingListModel;

public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "DBHelper";
    private static DBHelper singletonInstance;

    public static DBHelper getInstance(Context context) {
        if(singletonInstance == null) {
            singletonInstance = new DBHelper(context);
            singletonInstance.onCreate(singletonInstance.getWritableDatabase());
        }

        return singletonInstance;
    }

    private DBHelper(@Nullable Context context) {
        super(context, DBContract.DB_NAME, null, DBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.CategoryTable.SQL_CREATE_TABLE);
        db.execSQL(DBContract.ItemTable.SQL_CREATE_TABLE);
        db.execSQL(DBContract.ListTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.CategoryTable.SQL_DROP_TABLE);
        db.execSQL(DBContract.ItemTable.SQL_DROP_TABLE);
        db.execSQL(DBContract.ListTable.SQL_DROP_TABLE);

        onCreate(db);
    }

    /**
     * <p>
     *     Saves the {@link ShoppingListModel} to the database.
     * </p>
     * <p>
     *     This method also saves all of the model's item models into the other tables and
     *     manages the resources used.
     * </p>
     * @param list the shopping list model to save
     */
    public void saveShoppingList(ShoppingListModel list) {
        Log.v(LOG_TAG, "Saving ShoppingListModel with ID: " + list.getId() + ", name: " + list.getName());
        final SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.ListTable._ID, list.getId());
        values.put(DBContract.ListTable.COLUMN_NAME, list.getName()); // string
        values.put(DBContract.ListTable.COLUMN_LAST_PURCHASE_DATE, list.getLastPurchasedDate()); // long
        values.put(DBContract.ListTable.COLUMN_DELETED_FLAG, list.isDeletedFlag() ? 1 : 0); // int
        values.put(DBContract.ListTable.COLUMN_ITEM_COUNTER, list.getItemsCounter()); // long

        final long id = db.insertWithOnConflict(
                DBContract.ListTable.TABLE_NAME, null,
                values, SQLiteDatabase.CONFLICT_REPLACE);
        list.setId(id);

        // Loop through all of the item models and save them
        for(ItemModel itemModel : list.getItemList()) {
            saveItem(db, id, itemModel);
        }

        // Save the deleted item models
        for(ItemModel deletedItemModel : list.getDeletedItemList()) {
            saveItem(db, id, deletedItemModel);
        }

        // Close the writable database resource
        db.close();
    }

    /**
     * <p>
     *     Loads all of the saved shopping lists that do not have the delete flag set to 1
     *     (shopping lists that have not been deleted), and stores these in the
     *     {@link ShoppingListModel} static shopping list model list. This method clears the list
     *     before loading from the database.
     * </p>
     */
    @SuppressLint("Range")
    public void loadShoppingLists() {
        // Clear the models currently in the list
        ShoppingListModel.getShoppingListModels().clear();

        final SQLiteDatabase db = getReadableDatabase();

        // Get list counter from largest ID
        try(Cursor result = db.rawQuery("SELECT MAX(" + DBContract.ListTable._ID + ") FROM " +
                DBContract.ListTable.TABLE_NAME, null)) {
            if(result.moveToFirst()) {
                int maxId = result.getInt(0);
                ShoppingListModel.setListCounter(maxId + 1);

                Log.v(LOG_TAG, "Loading shopping list counter, currently at: " + (maxId + 1));
            }
        }

        // Try-with-resources block so Cursor result resource is automatically closed
        try (Cursor result = db.rawQuery("SELECT * FROM " + DBContract.ListTable.TABLE_NAME, null)) {
            if(result.getCount() != 0) {
                while(result.moveToNext()) {
                    int deleted = result.getInt(result.getColumnIndex(DBContract.ListTable.COLUMN_DELETED_FLAG));
                    if(deleted > 0) continue; // This shopping list model has been deleted

                    long id = result.getLong(result.getColumnIndex(DBContract.ListTable._ID));
                    String name = result.getString(result.getColumnIndex(DBContract.ListTable.COLUMN_NAME));
                    long lastPurchaseTimestamp = result.getLong(result.getColumnIndex(DBContract.ListTable.COLUMN_LAST_PURCHASE_DATE));
                    long itemCounter = result.getLong(result.getColumnIndex(DBContract.ListTable.COLUMN_ITEM_COUNTER));

                    Log.v(LOG_TAG, "Loading ShoppingListModel with ID: " + id);
                    ShoppingListModel model = new ShoppingListModel(name);
                    model.setId(id);
                    model.setLastPurchasedDate(lastPurchaseTimestamp);
                    model.setItemsCounter(itemCounter);

                    // Load the items
                    loadItems(db, model);

                    ShoppingListModel.getShoppingListModels().add(model);
                }
            }
        }
        db.close();
    }

    /**
     * <p>
     *     Saves an individual {@link ItemModel}.
     * </p>
     * @param db the writable database object
     * @param listId the {@link ShoppingListModel} ID this item is part of
     * @param item the item model to save
     */
    private void saveItem(final SQLiteDatabase db, final long listId, ItemModel item) {
        final long catId = saveCategory(db, item.getCategory());

        ContentValues values = new ContentValues();
        values.put(DBContract.ItemTable.COLUMN_LIST_ID, listId);
        values.put(DBContract.ItemTable._ID, item.getId());
        values.put(DBContract.ItemTable.COLUMN_NAME, item.getName());
        values.put(DBContract.ItemTable.COLUMN_CATEGORY_ID, catId);
        values.put(DBContract.ItemTable.COLUMN_PURCHASE_POWER, item.getPurchasePriority());
        values.put(DBContract.ItemTable.COLUMN_DELETED_FLAG, item.isDeletedFlag() ? 1 : 0);

        db.insertWithOnConflict(DBContract.ItemTable.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * <p>
     *     Loads all of the {@link ItemModel}'s related to a shopping list model and stores
     *     the results into the shopping list model's item list.
     * </p>
     * @param db the readable database
     * @param model the shopping list model the items must relate to
     */
    @SuppressLint("Range")
    private void loadItems(final SQLiteDatabase db, final ShoppingListModel model) {
        Log.v(LOG_TAG, "Loading ItemModel's from list with ID: " + model.getId());

        // Only loads items that have not been deleted
        String sql = "SELECT * FROM " + DBContract.ItemTable.TABLE_NAME + " WHERE " +
                DBContract.ItemTable.COLUMN_DELETED_FLAG + " = 0 AND " +
                DBContract.ItemTable.COLUMN_LIST_ID + " = ?";

        try (Cursor result = db.rawQuery(sql, new String[] { String.valueOf(model.getId()) })) {
            if(result.getCount() != 0) {
                while(result.moveToNext()) {
                    long id = result.getLong(result.getColumnIndex(DBContract.ItemTable._ID));
                    String name = result.getString(result.getColumnIndex(DBContract.ItemTable.COLUMN_NAME));
                    long catId = result.getLong(result.getColumnIndex(DBContract.ItemTable.COLUMN_CATEGORY_ID));
                    long purchasePower = result.getLong(result.getColumnIndex(DBContract.ItemTable.COLUMN_PURCHASE_POWER));

                    ItemModel itemModel = new ItemModel(name);
                    itemModel.setId(id);
                    itemModel.setPurchasePriority(purchasePower);

                    model.getItemList().add(itemModel);
                }
            }
        }
    }

    /**
     * <p>
     *     Saves the {@link CategoryModel}.
     * </p>
     * @param db the writable database object
     * @param category the category to save
     * @return the primary key ID of the category
     */
    private long saveCategory(final SQLiteDatabase db, CategoryModel category) {
        if(category == null) return -1;

        ContentValues values = new ContentValues();
        values.put(DBContract.CategoryTable._ID, category.getId());
        values.put(DBContract.CategoryTable.COLUMN_NAME, category.getName());
        values.put(DBContract.CategoryTable.COLUMN_COLOR, category.getColor());
        values.put(DBContract.CategoryTable.COLUMN_DRAWABLE_RES_ID, category.getDrawableId());

        return db.insertWithOnConflict(DBContract.CategoryTable.TABLE_NAME, null,
                values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * <p>
     *     Loads the {@link CategoryModel} from the given ID
     * </p>
     * @param db the readable database object
     * @param catId the categories ID to load
     * @return the category model object
     */
    @SuppressLint("Range")
    private CategoryModel loadCategory(final SQLiteDatabase db, final long catId) {
        // Try-with-resources block so Cursor result resource is automatically closed
        try (Cursor result = db.rawQuery(
                "SELECT * FROM " + DBContract.CategoryTable.TABLE_NAME + " WHERE _id = ?",
                new String[] { String.valueOf(catId) })) {
            if(result.moveToFirst()) {
                String name = result.getString(result.getColumnIndex(DBContract.CategoryTable.COLUMN_NAME));
                int colour = result.getInt(result.getColumnIndex(DBContract.CategoryTable.COLUMN_COLOR));
                int resId = result.getInt(result.getColumnIndex(DBContract.CategoryTable.COLUMN_DRAWABLE_RES_ID));

                CategoryModel model = new CategoryModel();
                model.setId(catId);
                model.setName(name);
                model.setColor(colour);
                model.setDrawableId(resId);

                return model;
            }
        }

        return null;
    }
}

package me.mp1282.shoppinglist.db;

import android.provider.BaseColumns;

public final class DBContract {

    // If the schema changes, this database version must be incremented
    public static final int DATABASE_VERSION = 7;
    public static final String DB_NAME = "ShoppingList.db";

    // So this class cannot be instantiated
    private DBContract() {}

    public static class CategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "tCategory";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_DRAWABLE_RES_ID = "dResId";
        public static final String COLUMN_DELETED_FLAG = "deletedFlag";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_COLOR + " INTEGER, " +
                        COLUMN_DRAWABLE_RES_ID + " INTEGER, " +
                        COLUMN_DELETED_FLAG + " INTEGER);";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class ItemTable implements BaseColumns {
        public static final String TABLE_NAME = "tItem";
        public static final String COLUMN_LIST_ID = "listId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY_ID = "categoryId";
        public static final String COLUMN_PURCHASE_POWER = "purchasePower";
        public static final String COLUMN_DELETED_FLAG = "deletedFlag";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_LIST_ID + " INTEGER, " +
                        _ID + " INTEGER, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_CATEGORY_ID + " INTEGER, " +
                        COLUMN_PURCHASE_POWER + " INTEGER, " +
                        COLUMN_DELETED_FLAG + " INTEGER, " +
                        "PRIMARY KEY(" + COLUMN_LIST_ID + ", " + _ID + ")" +
                        ");";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class ListTable implements BaseColumns {
        public static final String TABLE_NAME = "tList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAST_PURCHASE_DATE = "lastPurchaseTimestamp";
        public static final String COLUMN_DELETED_FLAG = "deletedFlag";
        public static final String COLUMN_ITEM_COUNTER = "itemCounter";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_LAST_PURCHASE_DATE + " INTEGER, " +
                        COLUMN_DELETED_FLAG + " INTEGER, " +
                        COLUMN_ITEM_COUNTER + " INTEGER);";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

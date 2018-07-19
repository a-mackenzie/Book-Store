package com.example.android.bookstore.data;

import android.provider.BaseColumns;

public class BookContract {

    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME = "Product_Name";
        public static final String PRICE = "Price";
        public static final String QTY = "Qty";
        public static final String SUPPLIER_NAME = "Supplier_Name";
        public static final String SUPPLIER_TEL = "Supplier_Tel";

    }

}

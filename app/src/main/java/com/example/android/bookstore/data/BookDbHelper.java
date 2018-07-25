package com.example.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bookstore.db";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.PRODUCT_TITLE + " TEXT NOT NULL, "
                + BookEntry.PRODUCT_AUTHOR + " TEXT NOT NULL, "
                + BookEntry.PRICE + " INTEGER NOT NULL, "
                + BookEntry.QTY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.SUPPLIER_TEL + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

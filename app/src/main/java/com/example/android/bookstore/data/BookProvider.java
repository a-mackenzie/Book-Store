package com.example.android.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstore.data.BookContract.BookEntry;

import java.net.URI;

public class BookProvider extends ContentProvider {

    // Create and instance of the BookDbHelper
    private BookDbHelper mDbHelper;

    // Tag for log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    // Performs the database query using the given URI, projection, selection, selection arguments, and sort order
    // Returns a cursor object
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
        // Get a readable version of the book database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Create a new cursor object
        Cursor cursor;

        // Get the URI type that matches the input URI
        int match = sUriMatcher.match(uri);

        // Build the cursor object bases on which URI type has been matched
        switch (match) {
            case BOOKS:
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArguments, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArguments = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArguments, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        // Set a notifier to notify all observers of the content that the uri has changed to UI can be updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    // Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // Validate the input URI and Insert new data into the provider with the given ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for invalid URI: " + uri);
        }
    }

    // Insert a new book into the database with the user input data and return the new content uri
    // for the new row in the database
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the title is not null
        String title = values.getAsString(BookEntry.PRODUCT_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book title required");
        }

        // Check that the author is not null
        String author = values.getAsString(BookEntry.PRODUCT_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Author name required");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(BookEntry.PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price required");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer qty = values.getAsInteger(BookEntry.QTY);
        if (qty != null && qty < 0) {
            throw new IllegalArgumentException("Quantity required");
        }

        // Check that the name of the supplier is not null
        String supplier = values.getAsString(BookEntry.SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        // Check that the telephone number of the supplier is not null
        String supplierTel = values.getAsString(BookEntry.SUPPLIER_TEL);
        if (supplierTel == null) {
            throw new IllegalArgumentException("Supplier phone number required");
        }

        // Get a writable version of the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert a new row in the database with the input data
        long id = db.insert(BookEntry.TABLE_NAME, null, values);

        // Log an error if the new row has not been inserted successfully
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Set a notifier to notify all observers of the content that the uri has changed to UI can be updated
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table, return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);

    }

    // Delete the data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArguments) {
        // Get a writable version of the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Match the provided URI using the uri matcher
        final int match = sUriMatcher.match(uri);

        // Create an int variable for the number of rows that were deleted from the database
        int rowsDeleted;

        // Delete the specified data
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArguments);
                // If any rows were deleted, notify all observers to update UI
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArguments = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArguments);
                // If any rows were deleted, notify all observers to update UI
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for URI: " + uri);
        }
    }

    // Validate the input URI and update the data for the selection and selection arguments
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
        // Match the URI using the uri matcher
        final int match = sUriMatcher.match(uri);
        // Update the data based on the selection and selection arguments
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArguments);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArguments = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArguments);
            default:
                throw new IllegalArgumentException("Update is not supported for URI: " + uri);
        }
    }

    // Update the book(s) in the database with the input data, based on any selections and
    // selection arguments
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArguments) {

        // Check that the title is not null
        String title = values.getAsString(BookEntry.PRODUCT_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book title required");
        }

        // Check that the author is not null
        String author = values.getAsString(BookEntry.PRODUCT_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Author name required");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(BookEntry.PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price required");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer qty = values.getAsInteger(BookEntry.QTY);
        if (qty != null && qty < 0) {
            throw new IllegalArgumentException("Quantity required");
        }

        // Check that the name of the supplier is not null
        String supplier = values.getAsString(BookEntry.SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier name required");
        }

        // Check that the telephone number of the supplier is not null
        String supplierTel = values.getAsString(BookEntry.SUPPLIER_TEL);
        if (supplierTel == null) {
            throw new IllegalArgumentException("Supplier phone number required");
        }

        // If no content values have been provided then return
        if (values.size() == 0) {
            return 0;
        }

        // Get a writable version of the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Update the rows based on the selection and selection arguments
        int rowsUpdated = db.update(BookEntry.TABLE_NAME, values, selection, selectionArguments);

        // If any rows have been updated, notify any observers to update the UI
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows that have been updated
        return rowsUpdated;

    }

    // URI matcher code for the content URI for the entire books table
    private static final int BOOKS = 100;

    // URI matcher code for the content URI for a single book in the table
    private static final int BOOK_ID = 101;

    // URI matcher object to match the code to the correct URI
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }
}

package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int BOOK_LOADER = 0;

    // Create a new BookDbHelper
    private BookDbHelper mDbHelper;

    // Create a new CursorAdapter
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new BookDbHelper(this);

        // Set onClickListener for add dummy book button
        Button dummyBookButton = findViewById(R.id.fab);
        dummyBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

        mCursorAdapter = new BookCursorAdapter(this, null);

        final ListView bookListView = (ListView) findViewById(R.id.list_view);

        // Set the cursor adapter on the list view
        bookListView.setAdapter(mCursorAdapter);

        // Set an onClickListener for the list view to open editor activity
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri selectedUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                Intent editIntent = new Intent(MainActivity.this, EditorActivity.class);
                editIntent.setData(selectedUri);
                startActivity(editIntent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertData() {

        // Create the content values for the new book
        ContentValues values = new ContentValues();
        values.put(BookEntry.PRODUCT_TITLE, "A Universe From Nothing");
        values.put(BookEntry.PRODUCT_AUTHOR, "Lawrence Krauss");
        values.put(BookEntry.PRICE, 1099);
        values.put(BookEntry.QTY, 1);
        values.put(BookEntry.SUPPLIER_NAME, "Books R Us");
        values.put(BookEntry.SUPPLIER_TEL, "01234567890");

        // Add the new book to the database
        getContentResolver().insert(BookEntry.CONTENT_URI, values);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.PRODUCT_TITLE,
                BookEntry.PRODUCT_AUTHOR,
                BookEntry.PRICE,
                BookEntry.QTY};
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.changeCursor(null);
    }
}

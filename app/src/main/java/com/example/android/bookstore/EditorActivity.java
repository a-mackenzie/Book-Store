package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int BOOK_LOADER = 0;

    // Create all of the user input views
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText priceEditText;
    private Button qtyMinusButton;
    private Button qtyPlusButton;
    private EditText supplierEditText;
    private EditText supplierTelEditText;
    private TextView qtyDisplayView;

    // Create a new BookDbHelper
    private BookDbHelper mDbHelper;

    // Create a new cursor adapter
    BookCursorAdapter mCursorAdapter;

    // Create a new Uri object for the selected book
    Uri selectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Check for an intent and get the URI if found
        if (getIntent() != null) {
            selectedUri = getIntent().getData();
        }

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Initialise the book db helper
        mDbHelper = new BookDbHelper(this);

        // Initialise all of the user input views
        titleEditText = findViewById(R.id.title_edit_text);
        authorEditText = findViewById(R.id.author_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        qtyMinusButton = findViewById(R.id.qty_minus_button);
        qtyPlusButton = findViewById(R.id.qty_plus_button);
        qtyDisplayView = findViewById(R.id.qty_display);
        supplierEditText = findViewById(R.id.supplier_edit_text);
        supplierTelEditText = findViewById(R.id.supplier_tel_edit_text);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.PRODUCT_TITLE,
                BookEntry.PRODUCT_AUTHOR,
                BookEntry.PRICE,
                BookEntry.QTY,
                BookEntry.SUPPLIER_NAME,
                BookEntry.SUPPLIER_TEL};
        return new CursorLoader(this, selectedUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Get all of the relevant data from the cursor
            String titleString = cursor.getString(cursor.getColumnIndex(BookEntry.PRODUCT_TITLE));
            String authorString = cursor.getString(cursor.getColumnIndex(BookEntry.PRODUCT_AUTHOR));
            int priceInt = cursor.getInt(cursor.getColumnIndex(BookEntry.PRICE));
            String priceString = String.valueOf(priceInt / 100);
            int qtyInt = cursor.getInt(cursor.getColumnIndex(BookEntry.QTY));
            String supplierString = cursor.getString(cursor.getColumnIndex(BookEntry.SUPPLIER_NAME));
            String supplierTelString = cursor.getString(cursor.getColumnIndex(BookEntry.SUPPLIER_TEL));

            // Set the data to the relevant views
            titleEditText.setText(titleString);
            authorEditText.setText(authorString);
            priceEditText.setText(priceString);
            qtyDisplayView.setText(String.valueOf(qtyInt));
            supplierEditText.setText(supplierString);
            supplierTelEditText.setText(supplierTelString);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        titleEditText.getText().clear();
        authorEditText.getText().clear();
        priceEditText.getText().clear();
        qtyDisplayView.setText("0");
        supplierEditText.getText().clear();
        supplierTelEditText.getText().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                // ***************TO DO***********************
                finish();
                return true;
            case R.id.menu_delete:
                // ***************TO DO***********************
                return true;
            case android.R.id.home:
                // ***************TO DO***********************
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

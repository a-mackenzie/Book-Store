package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    // Create a variable for the quantity
    int qtyInt;

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

        if (selectedUri == null) {
            this.setTitle(R.string.editor_title_add);
            invalidateOptionsMenu();
        } else {
            this.setTitle(R.string.editor_title_edit);
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

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

        // Set up an onClickListener for the Qty Minus button
        qtyMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qtyInt > 0) {
                    qtyInt -= 1;
                    qtyDisplayView.setText(String.valueOf(qtyInt));
                } else {
                    Toast zeroQtyToast = Toast.makeText(getBaseContext(), getString(R.string.toast_zero_qty), Toast.LENGTH_SHORT);
                    zeroQtyToast.show();
                }
            }
        });

        // Set up an onClickListener for the Qty Plus button
        qtyPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyInt += 1;
                qtyDisplayView.setText(String.valueOf(qtyInt));
            }
        });

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
            String priceString = String.valueOf(priceInt);
            qtyInt = cursor.getInt(cursor.getColumnIndex(BookEntry.QTY));
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
                saveBook();
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

    // Save the updated/new information to the database
    private void saveBook() {
        String priceString = priceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);

        ContentValues values = new ContentValues();
        values.put(BookEntry.PRODUCT_TITLE, String.valueOf(titleEditText.getText()).trim());
        values.put(BookEntry.PRODUCT_AUTHOR, String.valueOf(authorEditText.getText()).trim());
        values.put(BookEntry.PRICE, priceInt);
        values.put(BookEntry.QTY, qtyInt);
        values.put(BookEntry.SUPPLIER_NAME, String.valueOf(supplierEditText.getText()).trim());
        values.put(BookEntry.SUPPLIER_TEL, String.valueOf(supplierTelEditText.getText()).trim());

        int updateInt;
        Uri insertUri;
        String toastMessage = null;

        // Add a new book to the database or update an existing book
        if (selectedUri == null) {
            insertUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (insertUri == null) {
                toastMessage = getString(R.string.toast_save_error);
            } else {
                toastMessage = getString(R.string.toast_insert_success);
            }
        } else {
            updateInt = getContentResolver().update(selectedUri, values, null, null);
            if (updateInt == 0) {
                toastMessage = getString(R.string.toast_save_error);
            } else {
                toastMessage = getString(R.string.toast_update_success);
            }
        }

        Toast toast = Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_LONG);
        toast.show();

    }

    // Hide the 'Delete' option from the menu if this is a new pet
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (selectedUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
}

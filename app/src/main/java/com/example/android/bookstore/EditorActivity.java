package com.example.android.bookstore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int BOOK_LOADER = 0;

    private final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    // Create all of the user input views
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText priceEditText;
    private ImageButton qtyMinusButton;
    private ImageButton qtyPlusButton;
    private EditText supplierEditText;
    private EditText supplierTelEditText;
    private TextView qtyDisplayView;
    private ImageButton callSupplierButton;

    // Create a variable for the quantity
    int qtyInt = 0;

    // Create a new BookDbHelper
    private BookDbHelper mDbHelper;

    // Create a new Uri object for the selected book
    Uri selectedUri;

    // Create a boolean variable to track if the book information has changed
    private boolean bookEdited = false;

    // Create a touch listener to listen for any user input views being touched
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookEdited = true;
            return false;
        }
    };

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
        callSupplierButton = findViewById(R.id.call_supplier_button);

        // Set the touchlistener on all user input views
        titleEditText.setOnTouchListener(mTouchListener);
        authorEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        qtyMinusButton.setOnTouchListener(mTouchListener);
        qtyPlusButton.setOnTouchListener(mTouchListener);
        supplierEditText.setOnTouchListener(mTouchListener);
        supplierTelEditText.setOnTouchListener(mTouchListener);

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

        // Set up an onClickListener for the Call Supplier button
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierTelString = supplierTelEditText.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierTelString));

                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    startActivity(callIntent);
                }
                };
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
                // Check that all user input fields have been completed before saving
                if (allFieldsCompleted()) {
                    saveBook();
                    finish();
                } else {
                    Toast toast = Toast.makeText(getBaseContext(), getString(R.string.toast_complete_all_fields), Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
            case R.id.menu_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Check if there are unsaved changes and launch dialog if so
                if (!bookEdited) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    showUnsavedChangesDialog();
                    return true;
                }
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

    // Show the Delete confirmation dialog
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_confirm);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Delete the book from the database
    private void deleteBook() {
        String toastMessage;
        int deleteInt = getContentResolver().delete(selectedUri, null, null);
        if (deleteInt == 0) {
            toastMessage = getString(R.string.toast_delete_error);
        } else {
            toastMessage = getString(R.string.toast_delete_success);
            finish();
        }

        Toast toast = Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_LONG);
        toast.show();
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_unsaved_changes_confirm);
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Check that all input fields have been completed
    private boolean allFieldsCompleted() {
        String titleString = titleEditText.getText().toString().trim();
        String authorString = authorEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String supplierTelString = supplierTelEditText.getText().toString().trim();

        if (TextUtils.isEmpty(titleString)
            || TextUtils.isEmpty(authorString)
            || TextUtils.isEmpty(priceString)
            || TextUtils.isEmpty(supplierString)
            || TextUtils.isEmpty(supplierTelString)) {
            return false;
        } else {
            return true;
        }
    }
}

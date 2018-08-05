package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookProvider;

public class BookCursorAdapter extends CursorAdapter {

    // Constructor method
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    ImageButton saleButton;

    BookProvider mBookProvider = new BookProvider();

    // Creates a new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return listItemView;
    }

    // Populates the list item view with data from the cursor
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView titleView = view.findViewById(R.id.title);
        TextView authorView = view.findViewById(R.id.author);
        TextView priceView = view.findViewById(R.id.price);
        TextView qtyView = view.findViewById(R.id.qty);

        String titleString = cursor.getString(cursor.getColumnIndex(BookEntry.PRODUCT_TITLE));
        String authorString = cursor.getString(cursor.getColumnIndex(BookEntry.PRODUCT_AUTHOR));
        int priceInt = cursor.getInt(cursor.getColumnIndex(BookEntry.PRICE));
        int qtyInt = cursor.getInt(cursor.getColumnIndex(BookEntry.QTY));

        titleView.setText(titleString);
        authorView.setText(authorString);
        priceView.setText("£" + (priceInt));
        qtyView.setText(String.valueOf(qtyInt));

        saleButton = view.findViewById(R.id.sale_button);
        saleButton.setTag(cursor.getString(cursor.getColumnIndex(BookEntry._ID)));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    Object obj = view.getTag();
                    int id = Integer.parseInt(obj.toString());
                    sellBook(id, context);
                }
            }
        });
    }

    // Reduces qty by one when a book is sold
    private void sellBook(int id, Context context) {
        Uri selectedUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
        String[] projection = {
                BookEntry._ID,
                BookEntry.QTY};
        Cursor cursor = context.getContentResolver().query(selectedUri, projection, null, null, null, null);
        int qty = 0;
        if (cursor.moveToFirst()) {
            qty = cursor.getInt(cursor.getColumnIndex(BookEntry.QTY));
        }
        if (qty > 0) {
            qty -= 1;
            ContentValues values = new ContentValues();
            values.put(BookEntry.QTY, qty);
            String toastMessage;
            int updateInt = context.getContentResolver().update(selectedUri, values, null, null);
            if (updateInt == 0) {
                toastMessage = context.getResources().getString(R.string.toast_book_sale_error);
            } else {
                toastMessage = context.getResources().getString(R.string.toast_book_sale_success);
            }
            Toast saleToast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
            saleToast.show();
        } else {
            String toastMessage = context.getResources().getString(R.string.toast_out_of_stock);
            Toast outOfStockToast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
            outOfStockToast.show();
        }
    }
}

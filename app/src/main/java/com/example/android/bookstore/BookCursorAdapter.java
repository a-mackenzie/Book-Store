package com.example.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    // Constructor method
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Creates a new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // Populates the list item view with data from the cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
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
        priceView.setText("£" + (priceInt/100));
        qtyView.setText(qtyInt);

    }
}

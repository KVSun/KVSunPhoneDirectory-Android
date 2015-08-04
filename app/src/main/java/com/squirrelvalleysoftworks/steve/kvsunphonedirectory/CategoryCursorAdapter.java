package com.squirrelvalleysoftworks.steve.kvsunphonedirectory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by steve on 8/4/2015.
 */
public class CategoryCursorAdapter extends BaseAdapter {
    Context context;
    Cursor cursor;
    LayoutInflater inflator;
    CategoryCursorAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflator.inflate(R.layout.standard_result_view, null);
        cursor.moveToPosition(position);
        String category = cursor.getString(1);

        TextView viewDisplayNameTextView = (TextView) convertView.findViewById(R.id.displayNameView);
        TextView viewPhoneNumberTextView = (TextView) convertView.findViewById(R.id.phoneNumberView);

        viewDisplayNameTextView.setText(category);
        viewPhoneNumberTextView.setText("...");

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String category = ((TextView) v.findViewById(R.id.displayNameView)).getText().toString();
                ((MainActivity)context).handleCategory(category);
            }
        });
        return convertView;
    }
}

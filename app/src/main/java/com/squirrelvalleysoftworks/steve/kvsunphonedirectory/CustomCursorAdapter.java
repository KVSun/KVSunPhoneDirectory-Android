package com.squirrelvalleysoftworks.steve.kvsunphonedirectory;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Objects;

/**
 * Created by steve on 7/30/2015.
 */
public class CustomCursorAdapter extends BaseAdapter {
    private LayoutInflater inflator = null;
    private Cursor cursor = null;
    private Context context = null;

    //These are necessary for reusing Views within the listView, its good practice!
    //Maintains a representation for the banner views
    private class bannerHolder {

    }

    //Maintains a representation for the standard views
    private class standardHolder {

    }

    private class CursorRow {
        String displayName;
        String associatedNumbers;
        String allLines;
        String bannerPath;
        boolean hasMultipleNumbers;
        boolean hasMultipleLines; //Is this field actually necessary?
    }

    CustomCursorAdapter(Context context, Cursor cursor) {
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
        final CursorRow row = getRowAtPosition(cursor, position);
        //We'll be lazy for now, eventually implement recycling of views, use tags & holders
        if(row.bannerPath == "")
            convertView = generateBannerView(row);
        else
            convertView = generateStandardView(row);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //need to get width and height of parent
                View mainView = ((Activity) context).findViewById(R.id.layout);

                //Try to hide the keyboard when popup displays
                //Also necessary to get correct height (A little hacky here)
                ((MainActivity)context).clearSearchFocus();

                final PopupWindow pw = new PopupWindow(
                        inflator.inflate(R.layout.popup_result_view, null, false),
                        ((Activity) context).findViewById(R.id.resultsView).getWidth(),
                        mainView.getHeight(),
                        true);
                TextView popupTextView = (TextView) pw.getContentView().findViewById(R.id.popupTextView);
                popupTextView.setText(row.allLines);
                popupTextView.setMovementMethod(new ScrollingMovementMethod());
                pw.showAtLocation(mainView, Gravity.CENTER, 0, 0);
                ((MainActivity)context).setDim(true);
                //bind close button
                Button closeButton = (Button) pw.getContentView().findViewById(R.id.closePopupButton);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();
                        ((MainActivity)context).setDim(false);
                    }
                });

                //necessary due to lag time between keyboard closing and height updating
//                pw.update(0, 0, ((Activity) context).findViewById(R.id.resultsView).getWidth(),
//                        mainView.getHeight());
            }
        });
        return convertView;
    }

    View generateBannerView(CursorRow row) {
        return null;
    }

    View generateStandardView(CursorRow row) {
        View returnView = inflator.inflate(R.layout.standard_result_view, null);
        TextView viewDisplayNameTextView = (TextView) returnView.findViewById(R.id.displayNameView);
        TextView viewPhoneNumberTextView = (TextView) returnView.findViewById(R.id.phoneNumberView);

        viewDisplayNameTextView.setText(row.displayName);
        if(row.hasMultipleNumbers)
            viewPhoneNumberTextView.setText("...");
        else
            viewPhoneNumberTextView.setText(row.associatedNumbers);

        //Build and assign popup view


        //Will eventually need to put in place a click listener

        return returnView;
    }

    CursorRow getRowAtPosition(Cursor cursor, int position) {
        CursorRow row = new CursorRow();
        cursor.moveToPosition(position);

        row.displayName = cursor.getString(1);
        row.associatedNumbers = cursor.getString(2);
        row.allLines = cursor.getString(3);
        row.bannerPath = cursor.getString(4);
        row.hasMultipleNumbers = cursor.getInt(5) != 0;
        row.hasMultipleLines = cursor.getInt(6) != 0;

        return row;
    }
}

package com.squirrelvalleysoftworks.steve.kvsunphonedirectory;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
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
        ImageView bannerImageView = null;
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

        //Instantiate if view is empty
        if(convertView != null) {
            System.out.println("NON NULL ENTERED");
            if(convertView.getTag().equals("banner")) {
                System.out.println("RECYCLING BITMAP");
                ImageView imageView = (ImageView) convertView.findViewById(R.id.bannerImageView);
                Drawable imageViewDrawable = imageView.getDrawable();
                ((BitmapDrawable) imageViewDrawable).getBitmap().recycle();//WHY WONT YOU FUCKING DIE
                System.gc();
            } else if(convertView.getTag().equals("standard")) {
                System.out.println("CONVERT STANDARD");
            } else {
                System.out.println("NOT SURE WHAT WE GOT");
            }
        }

        //Now actually create the views
        if(row.bannerPath.equals("no path entered")) {
            if(convertView == null || !(convertView.getTag().equals("standard")))//wrong type of view
                convertView = inflator.inflate(R.layout.standard_result_view, null);
            convertView.setTag("standard");
            convertView = generateStandardView(row, convertView);
        } else {
            if (convertView == null || !(convertView.getTag().equals("banner")))
                convertView = inflator.inflate(R.layout.banner_result_view, null);
            convertView.setTag("banner");
            convertView = generateBannerView(row, convertView);
        }
        System.gc();

        //Display popup when pressing resultsEntry
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //need to get width and height of parent
                View mainView = ((Activity) context).findViewById(R.id.layout);

                //Try to hide the keyboard when popup displays
                //Also necessary to get correct height (A little hacky here)
                ((MainActivity) context).clearSearchFocus();

                final PopupWindow pw = new PopupWindow(
                        inflator.inflate(R.layout.popup_result_view, null, false),
                        ((Activity) context).findViewById(R.id.resultsView).getWidth(),
                        ((MainActivity) context).mainViewHeight / 2, //half as tall as the max of main view
                        true);
                System.out.print("Popup will be using height ");
                System.out.println(((MainActivity) context).mainViewHeight / 2);

                TextView popupTextView = (TextView) pw.getContentView().findViewById(R.id.popupTextView);
                popupTextView.setText(row.allLines);
                popupTextView.setMovementMethod(new ScrollingMovementMethod());
                pw.showAtLocation(mainView, Gravity.CENTER, 0, 0);
                ((MainActivity) context).setDim(true);
                //bind close button
                Button closeButton = (Button) pw.getContentView().findViewById(R.id.closePopupButton);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();
                        ((MainActivity) context).setDim(false);
                    }
                });
            }
        });
        System.gc();
        return convertView;
    }

    View generateBannerView(CursorRow row, View convertView) {
//        able_property_management.jpg
        ImageView bannerImageView = (ImageView) convertView.findViewById(R.id.bannerImageView);

//        bannerHolder holder = new bannerHolder();
//        holder.bannerImageView = bannerImageView;
//        convertView.setTag(holder);

        //Find resource id
        int bannerID = -1;
        try {
            Field idField = R.drawable.class.getDeclaredField(row.bannerPath);
            bannerID = idField.getInt(idField);
        } catch(Exception e) {
            throw new RuntimeException("Crash when trying to get banner R.id");
        }
        //Lets save some bytes
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDensity = 36;
        options.inPurgeable = true;

        Bitmap bitMap = BitmapFactory.decodeResource(context.getResources(), bannerID, options);

        bannerImageView.setImageBitmap(bitMap);
        convertView.setTag("banner");
        return convertView;
    }

    View generateStandardView(CursorRow row, View convertView) {
//        if(!(convertView.getTag() instanceof standardHolder)) {
//            if (convertView.getTag() instanceof bannerHolder)
//                throw new RuntimeException("bannerView passed");
//            else
//                throw new RuntimeException("Something else passed");
//        }



        TextView viewDisplayNameTextView = (TextView) convertView.findViewById(R.id.displayNameView);
        TextView viewPhoneNumberTextView = (TextView) convertView.findViewById(R.id.phoneNumberView);

        if(viewDisplayNameTextView == null)
            throw new RuntimeException("textview was not set");

        viewDisplayNameTextView.setText(row.displayName);
        if(row.hasMultipleNumbers)
            viewPhoneNumberTextView.setText("...");
        else
            viewPhoneNumberTextView.setText(row.associatedNumbers);

        convertView.setTag("standard");
        return convertView;
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

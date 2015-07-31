package com.squirrelvalleysoftworks.steve.kvsunphonedirectory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by steve on 7/26/2015.
 *
 */

//Steps to using the SQLiteAssetHelper with prepopulated database:
//    add the dependency to build.gradle (Module: app)
//    impliment the class with the same first 5 lines as this one
//    place the database in app/src/main/assets/databases
//        to make this folder change view to project, right click main new->folder->assets folder

//This class will return cursors based on the requested search method
//Will still return a cursor even if nothing is found
//This should be a singleton!

public class SearchHandler extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "entries.db";
    private static final int DATABASE_VERSION = 1;
    private final SQLiteDatabase db;

    public SearchHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    public Cursor searchByName(String name){
        String baseQuery = "select rowid as _id,* from Entries where displayName like ";
        String query = baseQuery + "'%" + name + "%'"; //this returns all that has name as a substring

        Cursor c = db.rawQuery(query, null); //_id necessary for adapters

        return c;
    }

    //Need to implement
    public Cursor searchByCategory(String name) {
        return null;
    }

    public Cursor searchByNumber(String number) {
        String baseQuery = "select rowid as _id,* from Entries where associatedNumbers like ";
        String query = baseQuery + "'%" + number + "%'"; //this returns all that has number as a substring

        Cursor c = db.rawQuery(query, null); //_id necessary for adapters

        return c;
    }



}

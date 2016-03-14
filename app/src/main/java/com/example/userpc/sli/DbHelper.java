package com.example.userpc.sli;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * Created by user pc on 3/11/2016.
 */
class DbHelper {


    private static final String TAG = "InterpretPage";
    private static DbHelper dbHelper;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private DbHelper(Context context) {
        this.openHelper = new DbOpenHelper(context);
        Log.i(TAG, "ENTERED IN DBHELPER");
    }

    private static DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
        Log.i(TAG, "GOT THE DATABASE");
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getImages() {
        Log.i(TAG, "BUILDING QUERY");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Log.i(TAG, "QUERY RETURNED CURSOR");
        cursor = database.rawQuery("SELECT * from Image", null);
        Log.i(TAG, "MOVE TO FIRST AND RETURN");
        cursor.moveToFirst();
        return cursor;
    }

    public void closeCursor() {
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }


}

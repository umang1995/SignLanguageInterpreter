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
public class DbHelper {


    private static final String TAG = "InterpretPage";
    private static DbHelper dbHelper;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private DbHelper(Context context) {
        this.openHelper = new DbOpenHelper(context);
        Log.i(TAG, "ENTERED IN DBHELPER");
    }

    public static DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    public void open() {
        Log.i(TAG, "trying to get database");
            this.database = openHelper.getReadableDatabase();
        Log.i(TAG, "GOT THE DATABASE");
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getImages() {
        Log.i(TAG, "QUERY RETURNED CURSOR");
        cursor = database.rawQuery("SELECT * from Image", null);
        Log.i(TAG, "MOVE TO FIRST AND RETURN");
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor getNumber(){
        Log.i(TAG,"getting numbers");
        cursor = database.rawQuery("SELECT * from Image where Name between 1 and 9 order by Name",null);
        Log.i(TAG,"query processed");
        cursor.moveToFirst();
        return cursor;
    }
    public Cursor getAlphabet(){
        Log.i(TAG,"getting numbers");
        cursor = database.rawQuery("SELECT * from Image where Name between 'a' and 'z' order by Name",null);
        Log.i(TAG,"query processed");
        cursor.moveToFirst();
        return cursor;
    }

    public void closeCursor(Cursor cursor) {
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }


}

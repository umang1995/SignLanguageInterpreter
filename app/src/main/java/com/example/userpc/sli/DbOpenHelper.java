package com.example.userpc.sli;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by user pc on 3/14/2016.
 */
public class DbOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "pictures.db";
    private static final int DATABASE_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}

package com.example.userpc.sli;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by user pc on 3/20/2016.
 */
public class Learn extends CursorAdapter {
    public Learn(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.learn,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView image = (ImageView) view.findViewById(R.id.Image);
        TextView Meaning =(TextView) view.findViewById(R.id.Meaning);
        byte[] imageArray = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
        String Meanings = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap LoadBitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
        image.setImageBitmap(Bitmap.createScaledBitmap(LoadBitmap,80,80,false));
        LoadBitmap.recycle();
        Meaning.setText(Meanings);
    }
}

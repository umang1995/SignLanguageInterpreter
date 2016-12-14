package com.example.userpc.sli;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class LearnPage extends AppCompatActivity implements LearnFragment.Callback{
    private GestureDetectorCompat gestureDetector;
    private DbHelper dbHelper;
    private Cursor numberCursor;
    private Cursor AlphabetCursor;
    private  LearnFragment lf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_page);
        gestureDetector = new GestureDetectorCompat(this,new MyGestureListener());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                dbHelper = DbHelper.getInstance(LearnPage.this);
                dbHelper.open();
                numberCursor = dbHelper.getNumber();
                AlphabetCursor = dbHelper.getAlphabet();
                dbHelper.close();
            }
        };
        Thread NumberThread = new Thread(r);
        NumberThread.start();
        FragmentManager fm = getFragmentManager();
        lf = new LearnFragment();
        fm.beginTransaction().replace(R.id.MyFrameLayout,lf,"learnFragment").addToBackStack(null).commit();
    }
     public void FillWithNumbers(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.remove(lf);
        ft.addToBackStack(null);
        ft.commit();
        ListView listView = (ListView) findViewById(R.id.DisplayRes);
        Learn learn = new Learn(this,numberCursor);
        listView.setAdapter(learn);
    }

    @Override
    public void FillWithAlphabets() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.remove(lf);
        ft.addToBackStack(null);
        ft.commit();
        ListView lv = (ListView) findViewById(R.id.DisplayRes);
        Learn learn = new Learn(this,AlphabetCursor);
        lv.setAdapter(learn);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX() > e1.getX()){
                Intent GestureIntent = new Intent(
                        LearnPage.this,MainPage.class
                );
                startActivity(GestureIntent);
            }
            return  true;

        }
    }
}

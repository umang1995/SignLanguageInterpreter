package com.example.userpc.sli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MainPage extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX() < e1.getX()){
                Intent GestureLeftIntent = new Intent(
                        MainPage.this,LearnPage.class
                );
                startActivity(GestureLeftIntent);
            }
            else if(e2.getX() > e1.getX()){
                Intent GestureRightIntent = new Intent(
                        MainPage.this,InterpretPage.class
                );
                startActivity(GestureRightIntent);
            }
            return  true;

        }
    }
}

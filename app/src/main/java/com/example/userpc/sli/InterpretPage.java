package com.example.userpc.sli;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.File;


public class InterpretPage extends AppCompatActivity {
    private Button MyCamera;
    private GestureDetectorCompat gestureDetector;
    static  final int REQUEST_IMAGE_CAPTURE = 1,REQUEST_GALLERY = 2;
    private Bitmap CapturedImage;
    private Bitmap GrayScaleImage;
    private Bitmap ResizedImage;
    private String Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interpret_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MyCamera = (Button)findViewById(R.id.myCamera);
        gestureDetector = new GestureDetectorCompat(this,new MyGestureListener());//navigation model

        // to see if the user has a camera else disable the button;
        if(!hasCamera()) {
            MyCamera.setEnabled(false);
        }

    }
//to check if user has a camera then the button is enabled(during creation)
        private boolean hasCamera() {
            return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        }
    // if the Camera button is clicked
    public void LaunchCamera(View view){
        Intent CameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //To Take a picture and get the pic as the bitmap
        startActivityForResult(CameraIntent,REQUEST_IMAGE_CAPTURE);
    }
//if the gallery button is clicked
    public void OpenGallery(View view){
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent, REQUEST_GALLERY);
    }
    //after the image is clicked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK && data!=null){//image was captured
            Bundle extras = data.getExtras();
             CapturedImage = (Bitmap)extras.get("data");//this is the captured image that is to be processed

        }
        else if(requestCode==REQUEST_GALLERY && resultCode==RESULT_OK && data!=null){

            Uri SelectedImage = data.getData(); // reading picked image data
            String[] filepath ={MediaStore.Images.Media.DATA}; //path of the picked image using content resolver
            Cursor c= getContentResolver().query(SelectedImage, filepath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filepath[0]);
            String picturePath = c.getString(columnIndex);
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            CapturedImage = (BitmapFactory.decodeFile(picturePath));
            c.close();
        }
        ResizedImage = Resize(CapturedImage);
        GrayScaleImage = ConvertToGrayscale(ResizedImage);
        //open images folder and read each file(Image) convert to byte array Subtract each image(byte array)with the GrayScaleImage byte Array and find the minimum Difference image and that will be the output Image and filename will be the Image ans
        Intent MoveOutput = new Intent(this,OutputPage.class);
        MoveOutput.putExtra("FinalImage",GrayScaleImage);//providing output to the output page class
        MoveOutput.putExtra("FinalText",Result);
        startActivity(MoveOutput);
    }
    public Bitmap Resize(Bitmap Original){
        return Original;
    }
    public Bitmap ConvertToGrayscale(Bitmap Original){
        Bitmap grayImage = Bitmap.createBitmap(Original.getWidth(),Original.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayImage);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter grayFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(grayFilter);
        canvas.drawBitmap(Original,0,0,paint);
        return grayImage;
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX() < e1.getX()){
                Intent GestureIntent = new Intent(
                        InterpretPage.this,MainPage.class
                );
                startActivity(GestureIntent);
            }
            return  true;

        }
    }

}

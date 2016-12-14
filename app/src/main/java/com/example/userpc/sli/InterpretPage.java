package com.example.userpc.sli;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class InterpretPage extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_GALLERY = 2;
    private static final int threshold = 124;
    private static final String TAG = "InterpretPage";
    private Button MyCamera;
    private GestureDetectorCompat gestureDetector;
    private String Result;
    private Cursor dbImages;
    private Runnable r;
    private DbHelper helper;

    //gesture recog+db Recovery+check camera
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interpret_page);
        Log.i(TAG, "oncreate");
        MyCamera = (Button) findViewById(R.id.myCamera);
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());//navigation model a listener to go back to main page


        // to see if the user has a camera else disable the button;
        if (!hasCamera()) {
            Log.i(TAG, "Check Camera");
            MyCamera.setEnabled(false);
        }
        r = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                     Log.i(TAG, "THREAD STARTED");
                     helper = DbHelper.getInstance(InterpretPage.this);
                     Log.i(TAG, "DB HELPER INIT");
                     helper.open();
                     Log.i(TAG, "DATABASE OPEN");
                     dbImages = helper.getImages();
                     Log.i(TAG, "CURSOR RETURNED");
                     helper.close();
                     Log.i(TAG, "DATABASE CLOSED");
                 }
                 }
        }; //a runnable object for thread operation
        Log.i(TAG, "thread start");
        Thread ImageThread = new Thread(r);
        ImageThread.start();// perform the db query on a different thread

    }
    //to check if user has a camera then the button is enabled(during creation)
    private boolean hasCamera() {
        Log.i(TAG, "CHECKING CAMERA");
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // if the Camera button is clicked
    public void LaunchCamera(View view) {
        Log.i(TAG, "LAUNCH CAMERA");
        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //To Take a picture and get the pic as the bitmap
        startActivityForResult(CameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    //if the gallery button is clicked
    public void OpenGallery(View view) {
        Log.i(TAG, "OPEN GALLERY");
        Intent GalleryIntent = new Intent();
        GalleryIntent.setType("image/*");
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(GalleryIntent, "Choose a Pic"), REQUEST_GALLERY);
    }

    //methods for pre processing
    //resize a bitmap to 128*128
    public Bitmap Resize(Bitmap Original){
        Log.i(TAG, "RESIZING");
        return Bitmap.createScaledBitmap(Original, 128, 128, true);
    }
    //convert an image to grayscale
    public Bitmap ConvertToGrayscale(Bitmap Original){
        Log.i(TAG, "GRAYSCALE ME ");
        Bitmap grayImage = Bitmap.createBitmap(Original.getWidth(),Original.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayImage);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter grayFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(grayFilter);
        canvas.drawBitmap(Original, 0, 0, paint);
        Log.i(TAG, "done");
        return grayImage;
    }

    // methods for actual algorithm

    //after the image is chosen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "BCK FROM DEAD");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {//image was captured
            Log.i(TAG, "BCK FROM cmra");
            Bundle extras = data.getExtras();
            Bitmap CapturedImage = (Bitmap) extras.get("data");//this is the captured image that is to be processed
            Starting(CapturedImage);
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Log.i(TAG, "BCK FROM glry");
            Uri uri = data.getData();
            try {
                Bitmap CapturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.i(TAG, "selected image handled");
                Starting(CapturedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Context context = getApplicationContext();
            CharSequence text = "SELECT AN IMAGE AND TRY AGAIN";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            setContentView(R.layout.activity_interpret_page);
        }
        // at this point we have the image picked by the user as a bitmap
    }

    //perform pre processing for the captured image
    public void Starting(Bitmap selectedImage) {
        //for providing the captured image to the user the bitmap before pre processing is saved in original
        Bitmap original = selectedImage;
        //---------pre processing--------------//
        //grayscale
        selectedImage = ConvertToGrayscale(selectedImage); // converts resized image to grayscale
        //-------resize----------//
        selectedImage = Resize(selectedImage); // makes the captured image to 128px*128px or 128bytes*128bytes
        //--------converting image to byte array-----------//
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] ByteArray = stream.toByteArray();
        /*int size = selectedImage.getRowBytes() * selectedImage.getHeight(); //get the size of the image(row*height)
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);//allocate the required size byte buffer
        selectedImage.copyPixelsToBuffer(byteBuffer); //copy the pixels to the buffer
        byte[] ByteArray = byteBuffer.array(); //make the buffer as byte array
        */
        //--- pre processed image is saved in the byteArray and that along with the original image is sent to Getoutput--//
        GetOutput(ByteArray, original); // call the function for the final interpretation
    }

    //---------the algorithm------//
    public double Diff(byte[] dbImage, byte[] original) {
        Log.i(TAG, "getting diff");
        int length = dbImage.length;
        byte[] diff = new byte[length];
        for (int i = 0; i < length; i++) {
            diff[i] = (byte)(Math.abs(dbImage[i] - original[i]));
        }
        int differentPixels = 0;
        for (byte b : diff) {
            if (b > threshold) {
                differentPixels++;
            }
        }
        double percentage = (double) differentPixels / (double) length;
        return percentage * 100;
    }


    //----pre processing of dbImage-------//
    public byte[] dbPre(byte[] dbByte){
        //--convert the byte array to bitmap for pre processing of the dbImage-------//
        BitmapFactory.Options opt = new BitmapFactory.Options();
        Bitmap dbIm = BitmapFactory.decodeByteArray(dbByte,0, dbByte.length,opt);
        dbIm = ConvertToGrayscale(dbIm);
        dbIm = Resize(dbIm);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dbIm.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] ByteArray = stream.toByteArray();
        return  ByteArray;
    }
    //intermediate function between selected image and db for output//
    public void GetOutput(byte[] inputArray, Bitmap original) {
        Log.i(TAG, "getting output");
        int height = original.getHeight();
        int width = original.getWidth();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] originalByte = stream.toByteArray();
        //get the blob from the db as a byte array
        dbImages.moveToFirst();
        byte[] image = dbImages.getBlob(1);
        Result = dbImages.getString(2);
        image = dbPre(image);
        //-----calculate ldiff for the first image in the db with the captured image-----//
        double ldiff = Diff(inputArray, image);
        //move to the next image
        dbImages.moveToNext();
        while (!dbImages.isAfterLast()) {
            image = dbImages.getBlob(1);
            image = dbPre(image);
            double diff = Diff(inputArray, image);
            if (diff < ldiff) {
                    ldiff = diff;
                    Result = dbImages.getString(2);
                }
                dbImages.moveToNext();
            }
        Intent outputPage = new Intent(InterpretPage.this, OutputPage.class);
        outputPage.putExtra("FinalText", Result);
        outputPage.putExtra("FinalImage",originalByte);
        outputPage.putExtra("Height", height);
        outputPage.putExtra("Width", width);
        startActivity(outputPage);
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
                helper.closeCursor(dbImages);
                startActivity(GestureIntent);
            }
            return  true;

        }
    }

}

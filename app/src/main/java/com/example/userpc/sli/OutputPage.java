package com.example.userpc.sli;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class OutputPage extends AppCompatActivity {

    private ImageView OutputImageView;
    private TextView FinalText;
    private Button SaveButton;
    private TextToSpeech OutputVoice;
    private Bitmap FinalImageToShow;
    private String Meaning;
    private static String TAG = "output";
    private TextToSpeech tts;
    private Bitmap finalBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_page); // set content view used for getting the xml file on the screen
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(TAG, "in onInit");
                if (status != TextToSpeech.ERROR) {
                    Log.i(TAG, "in if");
                    tts.setLanguage(Locale.UK);
                    Log.i(TAG, "SET Language");
                }
            }
        }
        );
        FinalText = (TextView) findViewById(R.id.OutputTextView); // get reference for the text view
        OutputImageView = (ImageView) findViewById(R.id.OutputImageView);
        Bundle InterpretData = getIntent().getExtras(); // get the extras from the intent of interpret page
        if (InterpretData == null) return;
        else {
            Log.i(TAG, "intent data not null");
            //getting the extra info from intent
            String FinalAns = InterpretData.getString("FinalText"); // make the ans as a strin and set it s text of the textview
            Log.i(TAG, "received String");
            Meaning = FinalAns;
            byte[] FinalImage = InterpretData.getByteArray("FinalImage");//convert this byte array to bitmap and set it as the source of the ImageView
            Log.i(TAG, "received byte array");
            //receiving byte array from intent and converting to Bitmap for display
            BitmapFactory.Options options = new BitmapFactory.Options();
            FinalImageToShow = BitmapFactory.decodeByteArray(FinalImage, 0, FinalImage.length, options); //converting the byte array to the bitmap and setting it as thesource of image view
            Log.i(TAG, "converted byte array to image");
            //setting the data ie img and text
            //OutputImageView.setMinimumHeight(height);
            //OutputImageView.setMinimumWidth(width);
            OutputImageView.setImageBitmap(FinalImageToShow);
            Log.i(TAG, "bitmap shown");
            FinalText.setText(FinalAns);
            Log.i(TAG, "Text shown");
            rendering();
        }
    }

    public void Listen(View view) {
        Log.i(TAG, "in Listen");
        tts.speak(Meaning, TextToSpeech.QUEUE_FLUSH, null);
        Log.i(TAG, "spoken");

    }

    public void save(View view) {
        String filename;
        Date date = new Date(0);
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
        filename =  sdf.format(date);

        try{
            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut;
            File file = new File(path);
            fOut = new FileOutputStream(file);

            finalBit.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getContentResolver()
                    ,file.getAbsolutePath(),file.getName(),file.getName());

        }catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();
    }

    public void rendering() {
        //creating the Bitmap to save from the canvas and setting it to a blank bitmap

        Canvas newCanvas = new Canvas();
        Log.i(TAG, "canvas init");
        newCanvas.drawBitmap(FinalImageToShow, 0, 0, null);
        Log.i(TAG, "bitmap drawn on canvas");
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        Log.i(TAG, "paint init");
        paintText.setColor(Color.RED);
        Log.i(TAG, "color set");
        paintText.setTextSize(50);
        Log.i(TAG, "size set");
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        Log.i(TAG, "style set");
        Rect rectText = new Rect();
        Log.i(TAG, "rect drawn");
        paintText.getTextBounds(Meaning, 0, Meaning.length(), rectText);
        Log.i(TAG, "bounds for the paint text");
        newCanvas.drawText(Meaning, 0, 0, paintText);
        Log.i(TAG, "text drawn on canvas");
        finalBit = Bitmap.createBitmap(FinalImageToShow.getWidth(), FinalImageToShow.getHeight(), Bitmap.Config.RGB_565);
        Log.i(TAG, "Bitmap created");
        newCanvas.setBitmap(finalBit);
        Log.i(TAG, "Bitmap set");
        //Bitmap has been created now trying to save it.
        Log.i(TAG, "CAN Start Saving");

        Log.i(TAG, "back from func");

    }

}
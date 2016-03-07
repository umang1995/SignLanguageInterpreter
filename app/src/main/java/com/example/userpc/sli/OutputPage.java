package com.example.userpc.sli;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OutputPage extends AppCompatActivity {

    private ImageView OutputImageView;
    private TextView FinalText;
    private Button SaveButton;
    private TextToSpeech OutputVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_page); // set content view used for getting the xml file on the screen

        FinalText = (TextView) findViewById(R.id.OutputTextView); // get reference for the textview
        OutputImageView = (ImageView) findViewById(R.id.OutputImageView);
        Bundle InterpretData = getIntent().getExtras(); // get the extras from the intent of interpret page
        if (InterpretData == null) return;
        else {
            //getting the extra info from intent
            String FinalAns = InterpretData.getString("FinalText"); // make the ans as a strin and set it s text of the textview
            byte[] FinalImage = InterpretData.getByteArray("FinalImage");//convert this byte array to bitmap and set it as the source of the ImageView
            int height = InterpretData.getInt("Height"); // height and width for the image view
            int width = InterpretData.getInt("Width");

            //conversion of byte array from intent to bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap FinalImageToShow = BitmapFactory.decodeByteArray(FinalImage, 0, FinalImage.length, options); //converting the byte array to the bitmap and setting it as thesource of image view

            //setting the data ie img and text
            FinalText.setText(FinalAns); // setting the text of text view
            OutputImageView.setMinimumHeight(height);
            OutputImageView.setMinimumWidth(width);
            OutputImageView.setImageBitmap(FinalImageToShow);


        }
    }
}

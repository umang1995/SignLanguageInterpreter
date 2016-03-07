package com.example.userpc.sli;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        setContentView(R.layout.activity_output_page);
        FinalText = (TextView)findViewById(R.id.OutputTextView);
        Bundle InterpretData = getIntent().getExtras();
        if(InterpretData==null)return;
        String FinalAns = InterpretData.getString("FinalText");
        FinalText.setText(FinalAns);
        byte[] FinalImage = InterpretData.getByteArray("FinalImage");//convert this byte array to bitmap and set it as the source of the ImageView
        Bitmap FinalImageToShow = null;
        OutputImageView.setImageBitmap(FinalImageToShow);


    }

}

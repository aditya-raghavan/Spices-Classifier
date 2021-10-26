package com.example.spicesclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    ImageView image;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            Uri uri = Uri.parse(extras.getString("uri"));
            String spice = extras.getString("spice");
            image = (ImageView)findViewById(R.id.spiceImage);
            result = (TextView)findViewById(R.id.resultTextView);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bitmap);
            result.setText(spice);


        }
    }
}
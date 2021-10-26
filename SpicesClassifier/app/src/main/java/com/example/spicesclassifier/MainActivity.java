package com.example.spicesclassifier;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spicesclassifier.ml.Cnn;
import com.example.spicesclassifier.ml.Mobnet;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button openButton;
    Button openGalleryButton;
    Bitmap image;
    TextView result;
    String resultSpice;
    Uri selectedImageUri;



    String fileName = "labels.txt";
    String inputString;
    String[] list;

    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //imageView = (ImageView)findViewById(R.id.image_view);
        openButton = (Button)findViewById(R.id.openButton);
        openGalleryButton = (Button)findViewById(R.id.openGalleryButton);
        result = (TextView)findViewById(R.id.resultTextView);




        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {
                            Manifest.permission.CAMERA
                    },100);
        }

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });


    }



    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            image = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(captureImage);
        }

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    //IVPreviewImage.setImageURI(selectedImageUri);
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }


        Bitmap resized = Bitmap.createScaledBitmap(image,200,200,true);

        try {

            Mobnet model = Mobnet.newInstance(this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 200, 200, 3}, DataType.FLOAT32);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(resized);
            ByteBuffer byteBuffer = tensorImage.getBuffer();

            //TensorImage tbuffer = TensorImage.fromBitmap(resized);
            //ByteBuffer byteBuffer = tbuffer.getBuffer();

            //ByteBuffer byteBuffer = ByteBuffer.allocateDirect(200*200*3*4);

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Mobnet.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


            float[] arr = outputFeature0.getFloatArray();

            if(arr[0]<=0.5){

                resultSpice = "Chilli";
            }
            else {

                resultSpice = "Turmeric";

            }

            // Releases model resources if no longer used.
            model.close();

            GoToResultActivity(selectedImageUri,resultSpice);
        } catch (IOException e) {
            // TODO Handle the exception
        }



    }

    void GoToResultActivity(Uri uri, String spice){
        Intent i = new Intent(this,ResultActivity.class);
        i.putExtra("uri", uri.toString());
        i.putExtra("spice",spice);
        startActivity(i);
    }



}
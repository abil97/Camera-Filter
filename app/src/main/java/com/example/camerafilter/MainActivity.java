package com.example.camerafilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath = null; // Path where photo will be saved
    private ImageView mImageView;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize imageView
        mImageView = findViewById(R.id.imageView);

        // Set some "default" image on the imageView using Bitmap
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bliss);
        mImageView.setImageBitmap(mBitmap);


        // Initialize buttons
        Button displayButton = findViewById(R.id.display);
        Button captureButton = findViewById(R.id.capture);
        Button applyFilter = findViewById(R.id.filter);


        // Taking photos and saving them, after clicking on "Capture Button"
        captureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Taking last captured image from internal storage and displaying it as a BitMap,
        // after clicking "Display Button"
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                mImageView.setImageResource(android.R.color.transparent);
                mImageView.setImageBitmap(mBitmap);
            }
        });

        // Applying Black and White filter
        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Bitmap isn't mutable! So, to change its pixel values, it's necessary to create a copy
                mBitmap = mBitmap.copy( Bitmap.Config.ARGB_8888 , true);

                // Get size of the Bitmap
                int width = mBitmap.getWidth();
                int height = mBitmap.getHeight();


                for(int i = 0; i < width; i++){
                    for(int j = 0; j < height; j++){

                        // Get RGB values of the current pixel
                        int currentPixel = mBitmap.getPixel(i, j);
                        int redValue = Color.red(currentPixel);
                        int blueValue = Color.blue(currentPixel);
                        int greenValue = Color.green(currentPixel);

                        // Get the average value of RGB values
                        int newColorValue = (int) ((redValue + blueValue + greenValue) / 3);

                        // Updating values of the current pixel.
                        // if values of RGB are equal, then resulting color will be Black & White
                        mBitmap.setPixel(i, j, Color.rgb(newColorValue, newColorValue, newColorValue));
                    }
                }

                // Setting the updated Bitmap to the imageView
                mImageView.setImageBitmap(mBitmap);


            }
        });


    }

    static final int REQUEST_TAKE_PHOTO = 1;

    // Function to use camera. Taken from documentation
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    // Function to create image file. Taken From documentation
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

package com.dldud.riceapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import static com.dldud.riceapp.CameraFragment.picturefilename;

public class PictureHolderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_holder);

        File imgFile = new File("/storage/emulated/0/LIS/" + picturefilename + ".jpg");
        ImageView iv = (ImageView) findViewById(R.id.bannerImage);
        Button cancel = (Button) findViewById(R.id.videoCancel);
        Button ok = (Button)findViewById(R.id.pictureOk);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(PictureHolderActivity.this, PictureTransActivity.class);
            startActivity(intent);
            finish();
            }

        });

        if(imgFile.exists()){
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                iv.setImageBitmap(myBitmap);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}


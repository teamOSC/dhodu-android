package com.dhodu.android;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.felipecsl.gifimageview.library.GifImageView;

import java.io.IOException;
import java.io.InputStream;

public class NyanCatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyan_cat);
        GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImageView);

        InputStream input;
        AssetManager assetManager = getAssets();
        try {
            input = assetManager.open("nyan.gif");
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            gifImageView.setBytes(buffer);
            gifImageView.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

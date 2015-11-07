package com.dhodu.android;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.felipecsl.gifimageview.library.GifImageView;

import java.io.IOException;
import java.io.InputStream;

public class NyanCatActivity extends AppCompatActivity {

    GifImageView gifImageView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyan_cat);
        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        mediaPlayer = new MediaPlayer();

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

    @Override
    protected void onResume() {
        super.onResume();
        if(!gifImageView.isAnimating())
            gifImageView.startAnimation();
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        playAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gifImageView.isAnimating())
            gifImageView.startAnimation();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void playAudio() {
        try {
            mediaPlayer.reset();
            AssetFileDescriptor descriptor = getAssets().openFd("nyan.ogg");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1f,1f);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

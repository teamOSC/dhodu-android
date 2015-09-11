package com.dhodu.android;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView imageSplash;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dhodu_primary_dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.dhodu_primary_dark));
        }

        imageSplash = (ImageView) findViewById(R.id.loader_image);
        final TextView tagline = (TextView) findViewById(R.id.tagline);
        tagline.setTypeface(Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf"));

        final LinearLayout dhoduLayout = (LinearLayout) findViewById(R.id.splash);

        AnimationDrawable ad = (AnimationDrawable) ContextCompat.getDrawable(this,
                R.drawable.loader_icon);
        imageSplash.setImageDrawable(ad);
        ad.start();

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Display display = getWindowManager().getDefaultDisplay();
                            imageSplash.setVisibility(View.GONE);
                            AnimationSet as = new AnimationSet(true);
                            Animation aa = new ScaleAnimation((float) 0.6
                                    , (float) 0.5, (float) 0.6, (float) 0.5
                                    , display.getWidth() / 2
                                    , display.getHeight() / 2);
                            aa.setDuration(2000);
                            aa.setStartOffset(1000);
                            as.addAnimation(aa);
                            aa = new AlphaAnimation(0, 1);
                            aa.setDuration(2000);
                            aa.setStartOffset(0);
                            as.addAnimation(aa);
                            as.setFillEnabled(true);
                            as.setFillAfter(true);
                            as.setInterpolator(new AccelerateInterpolator());
                            as.setStartTime(1000);
                            dhoduLayout.startAnimation(as);
                            dhoduLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent go = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(go);
                SplashActivity.this.finish();
            }
        };
        new Thread(runnable).start();
    }
}

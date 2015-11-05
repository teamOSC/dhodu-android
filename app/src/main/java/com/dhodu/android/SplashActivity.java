package com.dhodu.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends AppCompatActivity {

    ImageView imageSplash;
    boolean threadFinish = false;
    boolean branchInitFinish = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.activity_splash);

        imageSplash = (ImageView) findViewById(R.id.loader_image);
        final TextView tagline = (TextView) findViewById(R.id.tagline);
        tagline.setTypeface(Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf"));

        final LinearLayout dhoduLayout = (LinearLayout) findViewById(R.id.splash);

        AnimationDrawable ad = (AnimationDrawable) ContextCompat.getDrawable(this,
                R.drawable.loader_icon);
        imageSplash.setImageDrawable(ad);

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
                threadFinish = true;
                if (branchInitFinish)
                    startMainActivity();
            }
        };
        if (!BuildConfig.DEBUG) {
            ad.start();
            new Thread(runnable).start();
        } else {
            threadFinish = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                }
                branchInitFinish = true;
                if (threadFinish)
                    startMainActivity();
            }
        }, true, this.getIntent().getData(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void startMainActivity() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        Intent intent;
        if (sp.getBoolean("app_first_run", true))
            intent = new Intent(SplashActivity.this, DhoduIntro.class);
        else
            intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}

package com.dhodu.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by prempal on 11/9/15.
 */
public class DhoduIntro extends AppIntro {
    @Override
    public void init(Bundle bundle) {
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_1));
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_2));
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_3));
        setBarColor(getResources().getColor(R.color.dhodu_primary_dark));
        setSeparatorColor(Color.parseColor("#2196F3"));
        showSkipButton(true);
    }

    @Override
    public void onSkipPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDonePressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

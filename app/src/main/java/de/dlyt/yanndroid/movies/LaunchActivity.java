package de.dlyt.yanndroid.movies;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Locale;

import static de.dlyt.yanndroid.movies.SettingsActivity.setLocale;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        setLanguageAndTheme();

        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }


    public void setLanguageAndTheme() {

        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        switch (sharedPreferences.getInt("language_spinner", 0)) {
            case 0:
                setLocale(LaunchActivity.this, Locale.getDefault().getLanguage());
                return;
            case 1:
                setLocale(LaunchActivity.this, "en");
                return;
            case 2:
                setLocale(LaunchActivity.this, "de");
                return;
            case 3:
                setLocale(LaunchActivity.this, "fr");
                return;
        }

        switch (sharedPreferences.getInt("theme_spinner", 0)) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                return;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return;
        }

    }

}
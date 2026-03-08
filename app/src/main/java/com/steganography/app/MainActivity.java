package com.steganography.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardEncode = findViewById(R.id.cardEncode);
        CardView cardDecode = findViewById(R.id.cardDecode);
        TextView tvTagline = findViewById(R.id.tvTagline);

        // Animate cards sliding up
        animateSlideUp(tvTagline, 100);
        animateSlideUp(cardEncode, 300);
        animateSlideUp(cardDecode, 500);

        cardEncode.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, EncodeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        cardDecode.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DecodeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void animateSlideUp(View view, long delay) {
        TranslateAnimation slide = new TranslateAnimation(0, 0, 120f, 0f);
        slide.setDuration(600);
        slide.setStartOffset(delay);

        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(600);
        fade.setStartOffset(delay);

        android.view.animation.AnimationSet set = new android.view.animation.AnimationSet(true);
        set.addAnimation(slide);
        set.addAnimation(fade);
        set.setFillAfter(true);

        view.setVisibility(View.VISIBLE);
        view.startAnimation(set);
    }
}

package com.pronetway.customview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pronetway.customview.R;
import com.pronetway.customview.ui.brvah.BrvahActivity;
import com.pronetway.customview.ui.colortextview.ColorTrackTextViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomViewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_views);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_color_track_view, R.id.btn_circle_progress_view, R.id.btn_shape_view,
            R.id.btn_ali_loading_view, R.id.btn_rating_bar_view, R.id.btn_letter_side_bar, R.id.btn_brvah,
            R.id.btn_tag_layout, R.id.btn_kg_sliding, R.id.btn_splash, R.id.btn_data_screen, R.id.btn_wheel_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_color_track_view:
                startActivity(new Intent(this, ColorTrackTextViewActivity.class));
                break;
            case R.id.btn_circle_progress_view:
                startActivity(new Intent(this, CircleProgressBarActivity.class));
                break;
            case R.id.btn_shape_view:
                startActivity(new Intent(this, ElemeLoadingViewActivity.class));
                break;
            case R.id.btn_ali_loading_view:
                startActivity(new Intent(this, AliLoadingActivity.class));
                break;
            case R.id.btn_rating_bar_view:
                startActivity(new Intent(this, RatingBarActivity.class));
                break;
            case R.id.btn_letter_side_bar:
                startActivity(new Intent(this, LetterSideBarActivity.class));
                break;
            case R.id.btn_brvah:
                startActivity(new Intent(this, BrvahActivity.class));
                break;
            case R.id.btn_tag_layout:
                startActivity(new Intent(this, TagLayoutActivity.class));
                break;
            case R.id.btn_kg_sliding:
                startActivity(new Intent(this, SlidingMenu.class));
                break;
            case R.id.btn_splash:
                startActivity(new Intent(this, SplashActivity.class));
                break;
            case R.id.btn_data_screen:
                startActivity(new Intent(this, DataScreenActivity.class));
                break;
            case R.id.btn_wheel_view:
                startActivity(new Intent(this, WheelViewActivity.class));
                break;
            default:
                break;
        }
    }
}

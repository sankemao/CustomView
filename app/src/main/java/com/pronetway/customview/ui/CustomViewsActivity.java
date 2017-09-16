package com.pronetway.customview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pronetway.customview.R;
import com.pronetway.customview.ui.LetterSideBar.LetterSideBarActivity;
import com.pronetway.customview.ui.aliloadingtest.AliLoadingActivity;
import com.pronetway.customview.ui.circleprogressbar.CircleProgressBarActivity;
import com.pronetway.customview.ui.colortextview.ColorTrackTextViewActivity;
import com.pronetway.customview.ui.ratingbar.RatingBarActivity;
import com.pronetway.customview.ui.shapeview.ShapeViewActivity;

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
            R.id.btn_ali_loading_view, R.id.btn_rating_bar_view, R.id.btn_letter_side_bar})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_color_track_view:
                startActivity(new Intent(this, ColorTrackTextViewActivity.class));
                break;
            case R.id.btn_circle_progress_view:
                startActivity(new Intent(this, CircleProgressBarActivity.class));
                break;
            case R.id.btn_shape_view:
                startActivity(new Intent(this, ShapeViewActivity.class));
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
        }
    }
}

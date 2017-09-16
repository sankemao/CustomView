package com.pronetway.customview.ui.circleprogressbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.CircleProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CircleProgressBarActivity extends AppCompatActivity {

    @Bind(R.id.circle_progress_view)
    CircleProgressBar mCircleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_progress_bar);
        ButterKnife.bind(this);

        mCircleProgressView.setMax(4000);
        ValueAnimator animator = ObjectAnimator.ofFloat(0, 4000);
        animator.setDuration(2000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                mCircleProgressView.setProgress((int) progress);
            }
        });
    }
}

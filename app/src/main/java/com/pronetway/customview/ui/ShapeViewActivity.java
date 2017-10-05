package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.ShapeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShapeViewActivity extends AppCompatActivity {

    @Bind(R.id.shape_view)
    ShapeView mShapeView;

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_view);
        ButterKnife.bind(this);
        startChange();

    }

    private void startChange() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mShapeView.exChange();
//                        }
//                    });

                    mShapeView.exChange();

                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag = false;
    }
}

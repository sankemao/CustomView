package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.elemeloadingview.ShapeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ElemeLoadingViewActivity extends AppCompatActivity {

//    @Bind(R.id.shape_view)
//    ShapeView mShapeView;

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_view);
        ButterKnife.bind(this);
    }

}

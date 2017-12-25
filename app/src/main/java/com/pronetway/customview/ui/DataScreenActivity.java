package com.pronetway.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.datascreen.ListDataScreenView;
import com.pronetway.customview.ui.adapter.ListScreenMenuAdapter;

public class DataScreenActivity extends AppCompatActivity {
    private ListDataScreenView mListDataScreenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        mListDataScreenView = (ListDataScreenView) findViewById(R.id.list_data_screen_view);

        mListDataScreenView.setAdapter(new ListScreenMenuAdapter(this));
    }
}

package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.LetterSideBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LetterSideBarActivity extends AppCompatActivity implements LetterSideBar.LetterTouchListener {

    @Bind(R.id.tv_letter)
    TextView mTvLetter;
    @Bind(R.id.letter_side_bar)
    LetterSideBar mLetterSideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_side_bar);
        ButterKnife.bind(this);
        mLetterSideBar.setOnLetterTouchListener(this);
    }

    @Override
    public void touch(CharSequence letter) {
        mTvLetter.setText(letter);
    }
}

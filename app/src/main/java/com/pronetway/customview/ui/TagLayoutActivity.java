package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.taglayout.TagBaseAdapter;
import com.pronetway.customview.custom.taglayout.TagLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagLayoutActivity extends AppCompatActivity {

    @Bind(R.id.tag_layout)
    TagLayout mTagLayout;

    private List<String> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_layout);
        ButterKnife.bind(this);

        mItems = new ArrayList<>();
        mItems.add("吸血鬼");
        mItems.add("桃花妖");
        mItems.add("惠比寿");
        mItems.add("姑或鸟");
        mItems.add("茨木童子");
        mItems.add("雪女");
        mItems.add("般若");
        mItems.add("百鬼目");
        mItems.add("玉藻前");
        mItems.add("荒");
        mItems.add("大天狗");
        mItems.add("辉夜姬");
        mItems.add("雪童子");

        mTagLayout.setAdapter(new TagBaseAdapter() {
            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public View getView(int position, ViewGroup parent) {
                TextView tagTv = (TextView) LayoutInflater.from(TagLayoutActivity.this).inflate(R.layout.item_tag, parent, false);
                tagTv.setText(mItems.get(position));
                return tagTv;
            }
        });
    }
}

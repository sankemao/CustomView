package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronetway.customview.R;

import java.util.ArrayList;
import java.util.List;

public class SlidingMenu extends AppCompatActivity {

    private RecyclerView mRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);
        mRv = (RecyclerView) findViewById(R.id.rv_menu_test);

        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            List<String> testList = new ArrayList<>();
            {

                for (int i = 0; i < 100; i++) {
                    testList.add("我是第" + i + "个条目");
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setGravity(Gravity.CENTER);
                return new MyHolder(textView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(testList.get(position));
            }

            @Override
            public int getItemCount() {
                return 100;
            }

            class MyHolder extends RecyclerView.ViewHolder {
                public MyHolder(View itemView) {
                    super(itemView);
                }
            }

        });
    }
}

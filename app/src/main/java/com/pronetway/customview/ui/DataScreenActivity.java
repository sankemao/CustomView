package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.baselib.recyclerview.JViewHolder;
import com.pronetway.baselib.recyclerview.JrecyAdapter;
import com.pronetway.customview.R;
import com.pronetway.customview.custom.datascreen.ListDataScreenView;
import com.pronetway.customview.ui.adapter.ListScreenMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataScreenActivity extends AppCompatActivity {
    private ListDataScreenView mListDataScreenView;
    private RecyclerView mRvTest;
    private JrecyAdapter<String> mMyAdapter;

    List<String> mShowItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);

        mListDataScreenView = (ListDataScreenView) findViewById(R.id.list_data_screen_view);

        mListDataScreenView.setAdapter(new ListScreenMenuAdapter(this));

        for (int i = 0; i < 100; i++) {
            mShowItems.add("第" + i + "条");
        }


        mRvTest = (RecyclerView) findViewById(R.id.rv_test);

        mRvTest.setLayoutManager(new LinearLayoutManager(this));
//        mRvTest.addLoadViewCreator(new DefaultLoadMoreCreator());
//        mRvTest.addRefreshViewCreator(new DefaultRefreshCreator());
        mMyAdapter = new JrecyAdapter<String>(this, mShowItems, R.layout.item_test) {

            private View lastSelectedItem;

            public void toggleSelect(View itemView) {

                if (lastSelectedItem != itemView) {
                    if (lastSelectedItem != null) {
                        lastSelectedItem.setSelected(false);
                    }
                    itemView.setSelected(true);
                }

                lastSelectedItem = itemView;
                notifyDataSetChanged();
            }

            private int lastPosition = -1;

            @Override
            protected void convert(JViewHolder holder, String itemData, final int position) {
                LogUtils.w("条目地址为: " + holder.itemView);
                holder.setText(R.id.tv_test, itemData)
                        .setSelected(R.id.tv_test, position == lastPosition)
                        .setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShort("点了第" + position + "条");
//                                lastPosition = position;
//                                notifyDataSetChanged();
                                toggleSelect(v);
                            }
                        });
            }
        };
        mRvTest.setAdapter(mMyAdapter);
    }
}

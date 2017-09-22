package com.pronetway.customview.ui.brvah;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pronetway.baselib.http.HttpUtils;
import com.pronetway.customview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BrvahActivity extends AppCompatActivity {

    @Bind(R.id.rv_test)
    RecyclerView mRvTest;
    private TestAdapter mTestAdapter;
    private List<DropInfo> mShowItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brvah);
        ButterKnife.bind(this);



        initData();
    }

    private void initRecy() {
        mTestAdapter = new TestAdapter(R.layout.item_recy_lost, mShowItems);
        mRvTest.setLayoutManager(new LinearLayoutManager(this));
        mRvTest.setAdapter(mTestAdapter);
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("FunName", "postforum");
        params.put("start", 0);
        params.put("limit", 30);
        params.put("tsid", "150589902100171226");
        HttpUtils.with(this)
                .url("http://125.46.108.2:8036/pronline/Msg")
                .addParam(params)
                .enqueue(new SimpleCallBack<BaseResponse<DropInfo>>() {
                    @Override
                    public void onMainError(Exception e) {

                    }

                    @Override
                    public void onMainSuccess(BaseResponse<DropInfo> result) {
                        mShowItems = result.getEimdata();
                        initRecy();

                    }
                });
    }
}

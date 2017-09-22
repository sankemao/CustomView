package com.pronetway.customview.ui.brvah;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pronetway.customview.R;
import com.pronetway.customview.util.FormatUtil;

import java.util.List;

/**
 * Created by jin on 2017/9/20.
 */
public class TestAdapter extends BaseQuickAdapter<DropInfo, BaseViewHolder> {

    public TestAdapter(@LayoutRes int layoutResId, @Nullable List<DropInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DropInfo item) {
        helper.setText(R.id.tv_name, item.getUsername())
                .setText(R.id.tv_time, TimeUtils.getFriendlyTimeSpanByNow(Long.parseLong(item.getAddtime()) * 1000))
                .setText(R.id.tv_type, "0".equals(item.getType()) ? "丢失" : "捡到")
                .setText(R.id.tv_content, FormatUtil.getUTF8(item.getContent()))
                .setText(R.id.tv_clickcount, item.getClick())
                .setImageResource(R.id.iv_click, "0".equals(item.getType()) ?
                        R.drawable.icon_sorry_normal : R.drawable.icon_great_normal);
        if (!TextUtils.isEmpty(item.getUrl())) {
            helper.setVisible(R.id.iv_pic, true);
            Glide.with(mContext).load("http://125.46.108.2:8036/" + item.getUrl())
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_pic));
        } else {
            helper.setGone(R.id.iv_pic, false);
        }

        if (!TextUtils.isEmpty(item.getPhoto())) {
            Glide.with(mContext).load("http://125.46.108.2:8036/" + item.getPhoto())
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_head));
        } else {
            Glide.with(mContext).load(R.drawable.star_selected)
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) helper.getView(R.id.iv_head));
        }
    }
}

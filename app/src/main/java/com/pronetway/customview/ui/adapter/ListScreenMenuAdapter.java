package com.pronetway.customview.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.customview.R;
import com.pronetway.customview.custom.datascreen.BaseMenuAdapter;


/**
 * Description:TODO
 * Create Time:2017/12/25.23:34
 * Author:jin
 * Email:210980059@qq.com
 */
public class ListScreenMenuAdapter extends BaseMenuAdapter {

    private Context mContext;
    private String[] mItems = {"类型", "品牌", "价格", "更多"};

    public ListScreenMenuAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public View getTabView(int position, ViewGroup parent) {
        TextView tabView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.ui_list_data_screen_tab, parent, false);
        tabView.setText(mItems[position]);
        tabView.setTextColor(Color.BLACK);
        return tabView;
    }

    @Override
    public View getMenuView(int position, ViewGroup parent) {
        TextView menuView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.ui_list_data_screen_menu, parent, false);
        menuView.setText(mItems[position]);
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtils.showShort("关闭菜单");
                closeMenu();
            }
        });
        return menuView;
    }

    @Override
    public void menuClose(View tabView) {
        TextView tabText = (TextView) tabView;
        tabText.setTextColor(Color.BLACK);
    }

    @Override
    public void menuOpen(View tabView) {
        TextView tabText = (TextView) tabView;
        tabText.setTextColor(Color.RED);
    }
}

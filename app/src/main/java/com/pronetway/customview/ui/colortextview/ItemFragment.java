package com.pronetway.customview.ui.colortextview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jin on 2017/9/7.
 *
 */
public class ItemFragment extends Fragment{

    public static ItemFragment newInstance(String title) {
        ItemFragment itemFragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        itemFragment.setArguments(bundle);
        return itemFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String title = (String) arguments.get("title");
        TextView textView = new TextView(this.getContext());
        textView.setText(title);
        return textView;
    }
}

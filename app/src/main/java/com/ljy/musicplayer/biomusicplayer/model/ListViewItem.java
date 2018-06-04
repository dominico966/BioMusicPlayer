package com.ljy.musicplayer.biomusicplayer.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wjddp on 2018-03-29.
 */

public abstract class ListViewItem {

    private int layoutId;
    private View view = null;

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    abstract public View getView(LayoutInflater inflater, ViewGroup parent);
}

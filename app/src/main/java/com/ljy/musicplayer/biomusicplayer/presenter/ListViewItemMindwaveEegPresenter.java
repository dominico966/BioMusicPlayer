package com.ljy.musicplayer.biomusicplayer.presenter;

import android.support.v4.app.Fragment;

import com.ljy.musicplayer.biomusicplayer.view.ListViewItemMindwaveEeg;


public class ListViewItemMindwaveEegPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemMindwaveEeg model;
    private Fragment view;

    public ListViewItemMindwaveEegPresenter(ListViewAdapter listViewAdapter, ListViewItemMindwaveEeg model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.view = view;
    }

}

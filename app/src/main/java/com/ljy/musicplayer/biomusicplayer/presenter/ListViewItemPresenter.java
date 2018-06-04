package com.ljy.musicplayer.biomusicplayer.presenter;

public abstract class ListViewItemPresenter {
    private ListViewAdapter listViewAdapter;

    public ListViewItemPresenter(ListViewAdapter listViewAdapter) {
        this.listViewAdapter = listViewAdapter;
    }

    public ListViewAdapter getListViewAdapter(){
        return this.listViewAdapter;
    }

    public abstract void setEvent();
}

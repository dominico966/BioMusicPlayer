package com.ljy.musicplayer.biomusicplayer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

public class ListViewItemSongSuggest extends ListViewItem {
    public ListViewItemSongSuggest() {
        super();
        super.setLayoutId(R.layout.listview_item_song_suggest);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if(view != null) return view;

        view = inflater.inflate(getLayoutId(),parent,false);
        super.setView(view);

        return view;
    }
}

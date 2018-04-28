package com.ljy.musicplayer.biomusicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewItemSong extends ListViewItem {

    private int musicImg;
    private String musicName;

    public ListViewItemSong() {
        super();
        super.setLayoutId(R.layout.listview_view2);
    }

    public int getMusicImg() {
        return musicImg;
    }

    public void setMusicImg(int musicImg) {
        this.musicImg = musicImg;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();

        if(view == null) {
            view = inflater.inflate(getLayoutId(),parent,false);
            super.setView(view);
        }

        //선언
        ImageView musicImg = view.findViewById(R.id.musicImg);
        TextView musicName = view.findViewById(R.id.musicName);

        //설정
        musicImg.setImageResource(this.getMusicImg());
        musicName.setText(this.getMusicName());

        return view;
    }
}

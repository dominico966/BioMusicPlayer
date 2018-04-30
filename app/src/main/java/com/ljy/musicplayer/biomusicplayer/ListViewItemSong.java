package com.ljy.musicplayer.biomusicplayer;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewItemSong extends ListViewItem {

    public static ArrayList<ListViewItemSong> songList = new ArrayList<>();

    private Drawable musicImg;
    private String musicName;
    private String singerName;
    private String filePath;

    public ListViewItemSong() {
        super();
        super.setLayoutId(R.layout.listview_item_song);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public Drawable getMusicImg() {
        return musicImg;
    }

    public void setMusicImg(Drawable musicImg) {
        this.musicImg = musicImg;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();

        if (view == null) {
            view = inflater.inflate(getLayoutId(), parent, false);
            super.setView(view);
        }

        //선언
        ImageView musicImg = view.findViewById(R.id.img_music_album);
        TextView musicName = view.findViewById(R.id.text_name_music);
        TextView musicSinger = view.findViewById(R.id.text_name_singer);

        //설정
        musicImg.setImageDrawable(this.getMusicImg());
        musicName.setText(this.getMusicName());
        musicSinger.setText(this.getSingerName());

        musicName.setEllipsize(TextUtils.TruncateAt.END);
        musicName.setSingleLine(true);

        musicSinger.setEllipsize(TextUtils.TruncateAt.END);
        musicSinger.setSingleLine(true);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BioMusicPlayerApplication.getInstance().getServiceInterface().play(songList.indexOf(ListViewItemSong.this)); // 선택한 오디오재생
            }
        });

        return view;
    }
}

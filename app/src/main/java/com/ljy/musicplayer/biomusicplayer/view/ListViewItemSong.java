package com.ljy.musicplayer.biomusicplayer.view;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewItemSong extends ListViewItem {
    public enum Genre{
        Happiness("happiness"),Surprise("surprise"),Sadness("sadness"),Anger("anger");

        private String genre;
        Genre(String genre) {
            this.genre = genre;
        }

        public String toString() {
            return genre;
        }
    }

    public static ArrayList<ListViewItemSong> songList = new ArrayList<>();

    private Drawable musicImg;
    private String musicName;
    private String singerName;
    private String filePath;
    private long duration;
    private String genre;
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

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

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public View getView(final LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;

        view = inflater.inflate(getLayoutId(), parent, false);
        super.setView(view);

        //선언
        ImageView musicImg = view.findViewById(R.id.img_music_album);
        TextView musicName = view.findViewById(R.id.text_name_music);
        TextView musicSinger = view.findViewById(R.id.text_name_singer);
        TextView musicDuration = view.findViewById(R.id.text_music_duration);

        //설정
        musicImg.setImageDrawable(this.getMusicImg());
        musicName.setText(this.getMusicName());
        musicSinger.setText(this.getSingerName());

        long min = this.getDuration() / 1000 / 60;
        long sec = this.getDuration() / 1000 % 60;
        musicDuration.setText(String.format(inflater.getContext().getString(R.string.time_format), (int) min, (int) sec));

        musicName.setEllipsize(TextUtils.TruncateAt.END);
        musicName.setSingleLine(true);

        musicSinger.setEllipsize(TextUtils.TruncateAt.END);
        musicSinger.setSingleLine(true);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BioMusicPlayerApplication.getInstance().getServiceInterface().play(songList.indexOf(ListViewItemSong.this)); // 선택한 오디오재생
                //Toast.makeText(inflater.getContext(), songList.indexOf(ListViewItemSong.this) + " " + getMusicName(), Toast.LENGTH_SHORT).show();
            }
        };

        view.setOnClickListener(onClickListener);

//        if(isPlaying()) {
//            view.setBackgroundResource(R.drawable.bg_gradient);
//        } else {
//            view.setBackgroundResource(android.R.drawable.list_selector_background);
//        }

        return view;
    }

}

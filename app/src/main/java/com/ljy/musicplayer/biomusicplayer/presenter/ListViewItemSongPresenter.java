package com.ljy.musicplayer.biomusicplayer.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSuggest;

import java.io.File;

public class ListViewItemSongPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemSong model;
    private ListViewItemSuggest modelSuggest;
    private Fragment view;

    // face api
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

    public File file;

    public ListViewItemSongPresenter(ListViewAdapter listViewAdapter, ListViewItemSong model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.modelSuggest = new ListViewItemSuggest();
        this.view = view;


        this.activity = (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();
    }

    @Override
    public void setEvent() {
        Drawable defaultImage = activity.getResources().getDrawable(R.drawable.empty_albumart);
        Bitmap drawableToBitmap = ((BitmapDrawable) defaultImage).getBitmap();
        BitmapDrawable resize = new BitmapDrawable(activity.getResources(), BioMusicPlayerApplication.resizeBitmap(drawableToBitmap, 128, 128 * drawableToBitmap.getHeight() / drawableToBitmap.getWidth()));

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        BitmapDrawable cover = resize;
        mmr.setDataSource(file.getPath());

        // 이미지
        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap resizeBitmap = BioMusicPlayerApplication.resizeBitmap(bitmap, 128, 128);
            cover = new BitmapDrawable(activity.getResources(), resizeBitmap);
            bitmap.recycle();
        }

        // 문자열 데이터
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        String genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

        if (title == null) title = file.getName().split("mp3")[0];
        if (artist == null) artist = "";

        model.setMusicImg(cover);
        model.setMusicName(title);
        model.setSingerName(artist);
        model.setDuration(Long.parseLong(duration));
        model.setFilePath(file.getPath());
        model.setGenre(genre);

        modelSuggest.setMusicImg(cover);
        modelSuggest.setMusicName(title);
        modelSuggest.setSingerName(artist);
        modelSuggest.setDuration(Long.parseLong(duration));
        modelSuggest.setFilePath(file.getPath());
        modelSuggest.setGenre(genre);

        ListViewItemSong.songList.add(model);
        ListViewItemSuggest.suggests.add(modelSuggest);

        this.getListViewAdapter().notifyDataSetChanged();
    }


}

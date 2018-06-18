package com.ljy.musicplayer.biomusicplayer.presenter;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSongSuggest;

import java.util.ArrayList;

public class ListViewItemSongSuggestPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemSongSuggest model;
    private Fragment view;

    // face api
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

    public ListViewItemSongSuggestPresenter(ListViewAdapter listViewAdapter, ListViewItemSongSuggest model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.view = view;


        this.activity = (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();
    }

    @Override
    public void setEvent() {
        if(model.getView() == null) return;

        model.getView().findViewById(R.id.btnHappiness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListViewAdapter().removeSuggest();
                getListViewAdapter().showSuggest(ListViewItemSong.Genre.Happiness);
            }
        });

        model.getView().findViewById(R.id.btnSurprise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListViewAdapter().removeSuggest();
                getListViewAdapter().showSuggest(ListViewItemSong.Genre.Surprise);
            }
        });

        model.getView().findViewById(R.id.btnSadness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListViewAdapter().removeSuggest();
                getListViewAdapter().showSuggest(ListViewItemSong.Genre.Sadness);
            }
        });

        model.getView().findViewById(R.id.btnAnger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListViewAdapter().removeSuggest();
                getListViewAdapter().showSuggest(ListViewItemSong.Genre.Anger);
            }
        });
    }

}

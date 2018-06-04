package com.ljy.musicplayer.biomusicplayer.presenter;

import android.bluetooth.BluetoothAdapter;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.model.Mindwave;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemMindwaveEeg;
import com.neurosky.AlgoSdk.NskAlgoType;


public class ListViewItemMindwaveEegPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemMindwaveEeg model;
    private Fragment view;

    // mindwave
    private Mindwave mMindwave;
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

    public ListViewItemMindwaveEegPresenter(ListViewAdapter listViewAdapter, ListViewItemMindwaveEeg model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.view = view;
        this.activity = (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();
    }

    @Override
    public void setEvent(){
        initMindwave();
    }

    private void initMindwave() {
        mMindwave = new Mindwave(activity,
                BluetoothAdapter.getDefaultAdapter(),
                new NskAlgoType[]{
                        NskAlgoType.NSK_ALGO_TYPE_ATT,
                        NskAlgoType.NSK_ALGO_TYPE_MED,
                        NskAlgoType.NSK_ALGO_TYPE_BLINK,
                        NskAlgoType.NSK_ALGO_TYPE_BP
                }) {
            @Override
            public void onMindwaveConnected() {

            }
        };

        app.setMindwave(mMindwave);
    }

}

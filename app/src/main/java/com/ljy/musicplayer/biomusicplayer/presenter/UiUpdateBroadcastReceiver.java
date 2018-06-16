package com.ljy.musicplayer.biomusicplayer.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSuggest;

public class UiUpdateBroadcastReceiver extends BroadcastReceiver {

    private int previousPosition = -1;
    private int currentPosition = -1;

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "biomusicplayer.music.play": {
                previousPosition = intent.getIntExtra("previousPosition", -1);
                currentPosition = intent.getIntExtra("currentPosition", -1);

                ListViewItemSong previousItem = ListViewItemSong.songList.get(previousPosition);
                ListViewItemSong currentItem = ListViewItemSong.songList.get(currentPosition);

                previousItem.setPlaying(false);
                currentItem.setPlaying(true);

                if (previousItem.getView() != null && currentItem.getView() != null) {
                    previousItem.getView().setBackgroundResource(android.R.drawable.list_selector_background);
                    currentItem.getView().setBackgroundResource(R.drawable.bg_gradient);
                }

                ListViewItemSuggest previousSuggest = ListViewItemSuggest.suggests.get(previousPosition);
                ListViewItemSuggest currentSuggest = ListViewItemSuggest.suggests.get(currentPosition);

                previousSuggest.setPlaying(false);
                currentSuggest.setPlaying(true);

                if (previousSuggest.getView() != null && currentSuggest.getView() != null) {
                    previousSuggest.getView().setBackgroundResource(android.R.drawable.list_selector_background);
                    currentSuggest.getView().setBackgroundResource(R.drawable.bg_gradient);
                }
            }
            break;

        }
    }
}
package com.ljy.musicplayer.biomusicplayer.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ljy.musicplayer.biomusicplayer.model.AudioService;

public class UiUpdateBroadcastReceiver extends BroadcastReceiver {

    private OnStateChangeListener onStateChangeListener;

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AudioService.BroadcastActions.PLAY_STATE_CHANGED :
                if(onStateChangeListener != null) onStateChangeListener.onPlayStateChange();
                break;
        }
    }

    public interface OnStateChangeListener {
        void onPlayStateChange();
    }
}

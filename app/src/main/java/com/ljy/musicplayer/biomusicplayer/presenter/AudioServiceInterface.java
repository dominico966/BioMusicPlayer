package com.ljy.musicplayer.biomusicplayer.presenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ljy.musicplayer.biomusicplayer.model.AudioService;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;

import java.util.ArrayList;

//김준영 part
//AudioService 사용하기 위한 인터페이스 가져다 쓰면됨
public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<ListViewItemSong> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void seek(int msec) {
        if (mService != null) {
            mService.seek(msec);
        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public ListViewItemSong getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }

    public int getDuration() {
        int i = -1;
        if (mService != null) {
            i = mService.getDuration();
        }
        return i;
    }

    public int getCurrentPlayTime() {
        if (mService != null) {
            return mService.getCurrentPlayTime();
        }
        return -1;
    }

}

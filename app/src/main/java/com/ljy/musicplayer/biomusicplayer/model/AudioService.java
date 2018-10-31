package com.ljy.musicplayer.biomusicplayer.model;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import android.util.Log;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.NotificationPlayer;

import java.util.ArrayList;

/**
 * Created by 김준영 on 2017-12-04.
 */

public class AudioService extends Service {
    private boolean isPrepared;
    private int mPreviousPosition;
    private int mCurrentPosition;
    private MediaPlayer mMediaPlayer;
    private ListViewItemSong mAudioItem;
    private final IBinder mBinder = new AudioServiceBinder();
    private ArrayList<ListViewItemSong> mAudioIds = new ArrayList<>();
    private NotificationPlayer mNotificationPlayer;

    public class AudioServiceBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    // 처음 시작했을때 세팅
    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                sendBroadcast(new Intent(BroadcastActions.PREPARED)); // prepared 전송
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();

                Intent intent = new Intent("biomusicplayer.music.play");
                intent.putExtra("previousPosition", mPreviousPosition);
                intent.putExtra("currentPosition",mCurrentPosition);
                sendBroadcast(intent);

            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });

        mNotificationPlayer = new NotificationPlayer(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (CommandActions.TOGGLE_PLAY.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)) {
                rewind();
            } else if (CommandActions.FORWARD.equals(action)) {
                forward();
            } else if (CommandActions.CLOSE.equals(action)) {
                close();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void close() {
        pause();
        if(BioMusicPlayerApplication.getCurrentActivity() == null) {
            BioMusicPlayerApplication.logout();
        }

        removeNotificationPlayer();
    }

    // Music 끝내기
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void updateNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    //재생 가능상태 도움
    private void prepare() {
        try {
            mMediaPlayer.setDataSource(mAudioItem.getFilePath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Stop
    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    // Play list setting
    public void setPlayList(ArrayList<ListViewItemSong> audioIds) {
        if (!mAudioIds.equals(audioIds)) {
            mAudioIds.clear();
            mAudioIds.addAll(audioIds);
        }
    }

    public void play(int position) {
        if(position < 0) position = 0;
        mPreviousPosition = mCurrentPosition;
        mCurrentPosition = position;
        mAudioItem = ListViewItemSong.songList.get(mCurrentPosition);
        stop();
        prepare();
    }

    //Play
    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            updateNotificationPlayer();
        }
    }

    //Pause
    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            updateNotificationPlayer();
        }
    }

    //forward 다음 곡 재생
    public void forward() {
        if (mAudioIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }
        play(mCurrentPosition);
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
    }

    //rewind 이전 곡 재생
    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition);
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
    }

    //seek
    public void seek(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public int getCurrentPlayTime() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    // 진행 뮤직 아이템 반환
    public ListViewItemSong getAudioItem() {
        return mAudioItem;
    }

    //play상태 리턴
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    // 음악 상태 변경 이벤트 발생 시 Broadcast 메시지 전송->UI 출력
    public class BroadcastActions {
        public final static String PREPARED = "com.ljy.musicplayer.PREPARED";
        public final static String PLAY_STATE_CHANGED = "com.ljy.musicplayer.PLAY_STATE_CHANGED";
    }
}

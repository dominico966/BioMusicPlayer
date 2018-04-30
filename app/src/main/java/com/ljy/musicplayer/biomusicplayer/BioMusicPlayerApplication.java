package com.ljy.musicplayer.biomusicplayer;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class BioMusicPlayerApplication extends Application {
    private boolean isStudyMode = false;

    private File musicDir;

    //musicPlayer
    private static BioMusicPlayerApplication mInstance;
    private AudioServiceInterface mInterface;

    @Override
    public void onCreate() {
        Log.d("start", "" + "start application");
        super.onCreate();
        mInstance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
        musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    public static BioMusicPlayerApplication getInstance() {
        return mInstance;
    }

    public AudioServiceInterface getServiceInterface() {
        return mInterface;
    }


    public File getMusicDir() {
        return musicDir;
    }

    public void setMusicDir(File musicDir) {
        this.musicDir = musicDir;
    }

    public Mindwave getMindwave() {
        return mindwave;
    }

    public void setMindwave(Mindwave mindwave) {
        this.mindwave = mindwave;
    }

    private Mindwave mindwave;

    public boolean isStudyMode() {
        return isStudyMode;
    }

    public void setStudyMode(boolean studyMode) {
        isStudyMode = studyMode;
    }

}

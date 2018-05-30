package com.ljy.musicplayer.biomusicplayer;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class BioMusicPlayerApplication extends Application {

    private static volatile BioMusicPlayerApplication obj = null;
    private static volatile Activity currentActivity = null;

    private File musicDir;
    private boolean isStudyMode = false;

    //musicPlayer
    private Mindwave mindwave;
    private AudioServiceInterface mInterface;
    private static BioMusicPlayerApplication mInstance;


    public static BioMusicPlayerApplication getInstance() {
        return mInstance;
    }

    public File getMusicDir() {
        return musicDir;
    }
    public Mindwave getMindwave() {
        return mindwave;
    }
    public boolean isStudyMode() {
        return isStudyMode;
    }
    public AudioServiceInterface getServiceInterface() {
        return mInterface;
    }

    public void setMusicDir(File musicDir) {
        this.musicDir = musicDir;
    }
    public void setMindwave(Mindwave mindwave) {
        this.mindwave = mindwave;
    }
    public void setStudyMode(boolean studyMode) {
        isStudyMode = studyMode;
    }

    @Override
    public void onCreate() {
        Log.d("start", "" + "start application");
        super.onCreate();
        mInstance = this;
        obj = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
        musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        obj=null;
        mInstance = null;
    }

    public static BioMusicPlayerApplication getGlobalApplicationContext(){
        if(obj ==null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");

        return obj;
    }

    public static Activity getCurrentActivity(){
        return  currentActivity;
    }

    //Activity 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        BioMusicPlayerApplication.currentActivity=currentActivity;
    }

}

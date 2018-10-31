package com.ljy.musicplayer.biomusicplayer;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.ljy.musicplayer.biomusicplayer.model.Mindwave;
import com.ljy.musicplayer.biomusicplayer.presenter.AudioServiceInterface;

import java.io.File;

public class BioMusicPlayerApplication extends Application {

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

    public static String getInformationServerUrl() {
        return "http://127.0.0.1";
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
        mInterface = new AudioServiceInterface(getApplicationContext());
        musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    public static Bitmap resizeBitmap(Bitmap src, float newWidth, float newHeight) {
        Matrix matrix = new Matrix();
        int width = src.getWidth();
        int height = src.getHeight();

        float scaledWidth = newWidth / width;
        float scaledHeight = newHeight / height;
        matrix.postScale(scaledWidth, scaledHeight);

        return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
    }

    public static void logout() {
        try {
            LoginManager.getInstance().logOut();
            UserManagement userManagement = UserManagement.getInstance();
            userManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                }
            });
            Session.getCurrentSession().close();
        } catch (ExceptionInInitializerError | NoClassDefFoundError ignore) {
            ignore.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mInstance = null;
        logout();
    }

    public static BioMusicPlayerApplication getGlobalApplicationContext() {
        if (mInstance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");

        return mInstance;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    //Activity 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        BioMusicPlayerApplication.currentActivity = currentActivity;
    }

}

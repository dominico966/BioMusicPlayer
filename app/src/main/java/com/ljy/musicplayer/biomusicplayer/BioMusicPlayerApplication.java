package com.ljy.musicplayer.biomusicplayer;

import android.app.Application;

public class BioMusicPlayerApplication extends Application {
    private boolean isStudyMode = false;

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

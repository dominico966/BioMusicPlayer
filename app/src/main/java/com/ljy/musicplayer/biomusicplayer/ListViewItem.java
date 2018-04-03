package com.ljy.musicplayer.biomusicplayer;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewItem {

    private int type;

    private String modeName;
    private int musicImg;
    private String musicName;
    private Boolean modeState;

    public Boolean getModeState() {
        return modeState;
    }

    public void setModeState(Boolean modeState) {
        this.modeState = modeState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getMusicImg() {
        return musicImg;
    }

    public void setMusicImg(int musicImg) {
        this.musicImg = musicImg;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
}

package com.ljy.musicplayer.biomusicplayer;



public class ListViewItemSuggest extends ListViewItem {

    private int image;
    private String title;
    private String singer;

    ListViewItemSuggest(int image, String title, String singer){
        this.image = image;
        this.title = title;
        this.singer = singer;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }




}

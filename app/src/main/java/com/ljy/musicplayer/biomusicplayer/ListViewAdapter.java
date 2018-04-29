package com.ljy.musicplayer.biomusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dominic.skuface.FaceApi;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_MODE = 0;
    private static final int ITEM_VIEW_TYPE_SONG = 1;
    private static final int ITEM_VIEW_TYPE_MINDWAVE_STATE = 2;
    private static final int ITEM_VIEW_TYPE_NORMAL = 3;
    private static final int ITEM_VIEW_TYPE_MAX = 4;

    // 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();

    // 공부모드용 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemListStudy = new ArrayList<ListViewItem>();

    public ListViewAdapter(){
    }

    @Override
    public int getItemViewType(int position) {
        return selectItemListByMode().get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getCount() {
        return selectItemListByMode().size();
    }

    @Override
    public Object getItem(int i) {
        return selectItemListByMode().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(pos);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ListViewItem listViewItem = selectItemListByMode().get(pos);

            switch (viewType) {
                case ITEM_VIEW_TYPE_MODE: {
                    ListViewItemMode item = (ListViewItemMode) listViewItem;
                    view = item.getView(inflater, parent);
                }
                break;

                case ITEM_VIEW_TYPE_MINDWAVE_STATE: {
                    ListViewItemMindwaveState item = (ListViewItemMindwaveState) listViewItem;
                    view = item.getView(inflater, parent);
                }
                break;

                case ITEM_VIEW_TYPE_NORMAL: {
                    Log.d("pwy.index",pos+"");
                    ListViewItemFaceEmotion item = (ListViewItemFaceEmotion) listViewItem;
                    view = item.getView(inflater, parent);
                }
                break;

                case ITEM_VIEW_TYPE_SONG: {
                    ListViewItemSong item = (ListViewItemSong) listViewItem;
                    view = item.getView(inflater, parent);
                }
                break;
            }
        }
        return view;
    }

    //모드별로 아이템 데이터 리스트를 변경해 줌
    private ArrayList<ListViewItem> selectItemListByMode() {
        return BioMusicPlayerApplication.getInstance().isStudyMode() ? itemListStudy : itemList;
    }

    public void addItemMode(String modeName, Boolean modeState, View.OnClickListener toggleButtonEvent) {
        ListViewItemMode item = new ListViewItemMode();

        item.setType(ITEM_VIEW_TYPE_MODE);
        item.setLayoutId(R.layout.listview_item1);
        item.setModeName(modeName);
        item.setModeState(modeState);
        item.setOnToggleButtonClick(toggleButtonEvent);

        itemList.add(item);
        itemListStudy.add(item);
    }

    public void addItemSong(Drawable musicImg, String musicName, String singerName,String filePath) {
        ListViewItemSong item = new ListViewItemSong();

        item.setType(ITEM_VIEW_TYPE_SONG);
        item.setLayoutId(R.layout.listview_item_song);
        item.setMusicImg(musicImg);
        item.setMusicName(musicName);
        item.setSingerName(singerName);
        item.setFilePath(filePath);

        item.setPosition(ListViewItemSong.songList.size());
        ListViewItemSong.songList.add(item);

        itemList.add(item);
        itemListStudy.add(item);
    }

    public void addItemMindwaveState(Mindwave mindwave) {
        ListViewItemMindwaveState item = new ListViewItemMindwaveState(mindwave);

        item.setType(ITEM_VIEW_TYPE_MINDWAVE_STATE);
        item.setLayoutId(R.layout.listview_item_mindwave_state);

        itemListStudy.add(item);
    }

    public void addItemFaceEmotion(Bitmap bitmap, FaceApi.Face face) {
        ListViewItemFaceEmotion item = new ListViewItemFaceEmotion(bitmap, face);

        item.setType(ITEM_VIEW_TYPE_NORMAL);
        item.setLayoutId(R.layout.listview_item_face_emotion);

        itemList.remove(1);
        itemList.add(1,item);
    }

    public void refresh() {
        notifyDataSetChanged();
    }

}

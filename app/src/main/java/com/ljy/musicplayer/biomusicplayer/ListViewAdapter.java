package com.ljy.musicplayer.biomusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewAdapter extends BaseAdapter {

    private BioMusicPlayerApplication app;

    private static final int ITEM_VIEW_TYPE_MODE = 0;
    private static final int ITEM_VIEW_TYPE_SONG = 1;
    private static final int ITEM_VIEW_TYPE_MINDWAVE_STATE = 2;
    private static final int ITEM_VIEW_TYPE_MAX = 3;

    // 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();

    // 공부모드용 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemListStudy = new ArrayList<ListViewItem>();

    public ListViewAdapter(BioMusicPlayerApplication app) {
        this.app = app;
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

                case ITEM_VIEW_TYPE_SONG: {
                    ListViewItemSong item = (ListViewItemSong) listViewItem;
                    view = item.getView(inflater,parent);
                }
                break;
            }
        }
        return view;
    }

    //모드별로 아이템 데이터 리스트를 변경해 줌
    private ArrayList<ListViewItem> selectItemListByMode() {
        return app.isStudyMode() ? itemListStudy : itemList;
    }

    public void addItem(String modeName, Boolean modeState, View.OnClickListener toggleButtonEvent) {
        ListViewItemMode item = new ListViewItemMode();

        item.setType(ITEM_VIEW_TYPE_MODE);
        item.setLayoutId(R.layout.listview_item1);
        item.setModeName(modeName);
        item.setModeState(modeState);
        item.setOnToggleButtonClick(toggleButtonEvent);

        itemList.add(item);
        itemListStudy.add(item);
    }

    public void addItem(int musicImg, String musicName) {
        ListViewItemSong item = new ListViewItemSong();

        item.setType(ITEM_VIEW_TYPE_SONG);
        item.setLayoutId(R.layout.listview_view2);
        item.setMusicImg(musicImg);
        item.setMusicName(musicName);

        itemList.add(item);
        itemListStudy.add(item);
    }

    public void addItem(Mindwave mindwave) {
        ListViewItemMindwaveState item = new ListViewItemMindwaveState(mindwave);

        item.setType(ITEM_VIEW_TYPE_MINDWAVE_STATE);
        item.setLayoutId(R.layout.listview_item_mindwave_state);

        itemListStudy.add(item);
    }

    public void refresh() {
        notifyDataSetChanged();
    }

}

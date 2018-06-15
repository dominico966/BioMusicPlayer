package com.ljy.musicplayer.biomusicplayer.presenter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemFaceEmotion;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemMindwaveEeg;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemMode;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemPieChart;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSongSuggest;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSuggest;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_MODE = 0;
    private static final int ITEM_VIEW_TYPE_SONG = 1;
    private static final int ITEM_VIEW_TYPE_MINDWAVE_STATE = 2;
    private static final int ITEM_VIEW_TYPE_NORMAL = 3;
    private static final int ITEM_VIEW_TYPE_MAX = 6;

    // 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();

    // 공부모드용 아이템 데이터 리스트
    private ArrayList<ListViewItem> itemListStudy = new ArrayList<ListViewItem>();

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
        return selectItemListByMode().get(i).hashCode();
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final Context context = parent.getContext();

        Log.d("pwy", "getView called " + pos);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListViewItem listViewItem = selectItemListByMode().get(pos);
        view = listViewItem.getView(inflater,parent);

        return view;
    }

    //모드별로 아이템 데이터 리스트를 변경해 줌
    private ArrayList<ListViewItem> selectItemListByMode() {
        return BioMusicPlayerApplication.getInstance().isStudyMode() ? itemListStudy : itemList;
    }

    /*
    * Tab1 ListItem Element Add Method
    * */
    public ListViewItemMode addItemMode(String modeName, Boolean modeState) {
        ListViewItemMode item = new ListViewItemMode();

        item.setLayoutId(R.layout.listview_item_mode);
        item.setModeName(modeName);
        item.setModeState(modeState);

        itemList.add(item);
        itemListStudy.add(item);

        return item;
    }

    public ListViewItemSong addItemSong() {
        ListViewItemSong item = new ListViewItemSong();

        itemList.add(item);
        itemListStudy.add(item);

        return item;
    }

    public ListViewItemFaceEmotion addItemFaceEmotion() {

        ListViewItemFaceEmotion item = new ListViewItemFaceEmotion();

        if (itemList.get(1) instanceof ListViewItemFaceEmotion) {
            itemList.add(2, item);
            itemList.remove(1);
        } else {
            itemList.add(1, item);
        }

        return item;
    }

    public ListViewItemMindwaveEeg addItemMindwaveEeg(Activity activity) {
        ListViewItemMindwaveEeg item = new ListViewItemMindwaveEeg(activity);

        itemListStudy.add(item);

        return item;
    }
    /*
     * Tab1 ListItem Element Add Method End
     * */

    /*
     * Tab2 ListItem Element Add Method
     * */
    public ListViewItemPieChart addItemPieChart() {
        ListViewItemPieChart item = new ListViewItemPieChart();

        itemList.add(item);
        itemListStudy.add(item);

        return item;
    }

    public ListViewItemSongSuggest addItemSongSuggest() {
        ListViewItemSongSuggest item = new ListViewItemSongSuggest();

        itemList.add(item);
        itemListStudy.add(item);

        return item;
    }

    public void removeSuggest() {
        while(itemList.size() != 2 && itemListStudy.size() != 2) {
            itemList.remove(2);
            itemListStudy.remove(2);
        }
    }

    public void showSuggest(ListViewItemSong.Genre genre) {
        for(ListViewItemSuggest item : ListViewItemSuggest.suggests) {
            if(genre.toString().equals(item.getGenre())) {
                itemList.add(item);
                itemListStudy.add(item);
            }
        }
        notifyDataSetChanged();
    }

    /*
     * Tab2 ListItem Element Add Method End
     * */

}

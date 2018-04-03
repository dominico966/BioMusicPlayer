package com.ljy.musicplayer.biomusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_MODE = 0;
    private static final int ITEM_VIEW_TYPE_SONG = 1;
    private static final int ITEM_VIEW_TYPE_MAX = 2;

    // 아이템 데이터 리스트
     private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    public ListViewAdapter(){

    }

    @Override
    public int getItemViewType(int position) {
        return listViewItemList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
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

            ListViewItem listViewItem = listViewItemList.get(pos);

            switch (viewType) {
                case ITEM_VIEW_TYPE_MODE:
                    view = inflater.inflate(R.layout.listview_item1, parent, false);
                    TextView modeName = view.findViewById(R.id.txtModeName);
                    ToggleButton toggleButton = view.findViewById(R.id.toggleButton);

                    modeName.setText(listViewItem.getModeName());
//                    toggleButton.setChecked(listViewItem.getModeState());

                    break;

                case ITEM_VIEW_TYPE_SONG:
                    view = inflater.inflate(R.layout.listview_view2, parent, false);
                    ImageView musicImg = view.findViewById(R.id.musicImg);
                    TextView musicName = view.findViewById(R.id.musicName);

                    musicImg.setImageResource(listViewItem.getMusicImg());
                    musicName.setText(listViewItem.getMusicName());
                    break;

            }
        }
        return view;
    }

    public void addItem(String modeName, Boolean modeState) {
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_MODE) ;
        item.setModeName(modeName);
        item.setModeState(modeState);

        listViewItemList.add(item);
    }

    public void addItem(int musicImg, String musicName) {
        ListViewItem item = new ListViewItem();

        item.setType(ITEM_VIEW_TYPE_SONG) ;
        item.setMusicImg(musicImg);
        item.setMusicName(musicName);

        listViewItemList.add(item );

    }

}

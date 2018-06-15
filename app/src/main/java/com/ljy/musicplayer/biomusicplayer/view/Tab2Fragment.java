package com.ljy.musicplayer.biomusicplayer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewAdapter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemPieChartPresenter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemSongSuggestPresenter;

public class Tab2Fragment extends Fragment {

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private ListViewItemPieChartPresenter listViewItemPieChartPresenter;
    private ListViewItemSongSuggestPresenter listViewItemSongSuggestPresenter;

    private BioMusicPlayerApplication app;

    public Tab2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2_1, container, false);

        //Application 공통변수
        app = BioMusicPlayerApplication.getInstance();

        //리스트뷰 작업
        listView = view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter();

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);

        ListViewItemPieChart listViewItemPieChart = listViewAdapter.addItemPieChart();
        ListViewItemSongSuggest listViewItemSongSuggest = listViewAdapter.addItemSongSuggest();

        //리스트뷰 Presenter
        listViewItemPieChartPresenter = new ListViewItemPieChartPresenter(listViewAdapter,listViewItemPieChart,this);
        listViewItemSongSuggestPresenter = new ListViewItemSongSuggestPresenter(listViewAdapter,listViewItemSongSuggest,this);

        return view;
    }

    @Override
    public void onResume() {
        listViewItemPieChartPresenter.setEvent();
        listViewItemSongSuggestPresenter.setEvent();
        super.onResume();
    }

}

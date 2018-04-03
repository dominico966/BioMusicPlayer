package com.ljy.musicplayer.biomusicplayer;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Tab1Fragment extends Fragment {

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        ListView listView = view.findViewById(R.id.listView);
        ListViewAdapter listViewAdapter = new ListViewAdapter();

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);

        listViewAdapter.addItem("Mode name", false);
        listViewAdapter.addItem(R.drawable.test, "test");

        return view;
    }
}

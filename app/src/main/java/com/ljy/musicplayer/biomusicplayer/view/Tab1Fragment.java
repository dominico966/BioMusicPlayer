package com.ljy.musicplayer.biomusicplayer.view;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewAdapter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemFaceEmotionPresenter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemMindwaveEegPresenter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemModePresenter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemSongPresenter;

import java.io.File;

public class Tab1Fragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private BioMusicPlayerApplication app;

    private ListViewItemModePresenter listViewItemModePresenter;
    private ListViewItemMindwaveEegPresenter listViewItemMindwaveEegPresenter;
    private ListViewItemFaceEmotionPresenter listViewItemFaceEmotionPresenter;
    private ListViewItemSongPresenter listViewItemSongPresenter;

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        //Application 공통변수
        app = BioMusicPlayerApplication.getInstance();

        //리스트뷰 작업
        listView = view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter();

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);

        ListViewItemMode listViewItemMode = listViewAdapter.addItemMode("Study Mode", app.isStudyMode());
        ListViewItemMindwaveEeg listViewItemMindwaveEeg = listViewAdapter.addItemMindwaveEeg(getActivity());

        //리스트뷰 Presenter
        listViewItemModePresenter = new ListViewItemModePresenter(listViewAdapter, listViewItemMode, this);
        listViewItemMindwaveEegPresenter = new ListViewItemMindwaveEegPresenter(listViewAdapter, listViewItemMindwaveEeg, this);

        checkPermissionReadStorage(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listViewItemModePresenter.setEvent();
        listViewItemMindwaveEegPresenter.setEvent();
    }

    @Override
    public void onStart() {
        listViewAdapter.notifyDataSetChanged();
        listView.invalidate();
        super.onStart();
    }

    public void checkPermissionReadStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            loadSongsFromMusicDir();
        }

    }

    private void loadSongsFromMusicDir() {
        if (ListViewItemSong.songList.isEmpty()) {
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File[] list = root.listFiles();
            Log.d("pwy", root.getPath());
            Log.d("pwy", list.length + "");
            app.setMusicDir(root);

            for (File file : list) {
                if (!file.getName().endsWith(".mp3")) continue;
                ListViewItemSong listViewItemSong = listViewAdapter.addItemSong();
                listViewItemSongPresenter = new ListViewItemSongPresenter(listViewAdapter, listViewItemSong, this);
                listViewItemSongPresenter.file = file;
                listViewItemSongPresenter.setEvent();
            }
        } else {
            for (ListViewItemSong item : ListViewItemSong.songList) {
                listViewAdapter.addItem(item);
            }
        }

        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_tab1_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_face_scan:
                Toast.makeText(getActivity(), "얼굴인식", Toast.LENGTH_SHORT).show();

                ListViewItemFaceEmotion listViewItemFaceEmotion = listViewAdapter.addItemFaceEmotion();
                listViewItemFaceEmotionPresenter = new ListViewItemFaceEmotionPresenter(listViewAdapter, listViewItemFaceEmotion, this);
                listViewItemFaceEmotionPresenter.setEvent();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                //permission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    loadSongsFromMusicDir();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "We Need permission Storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listViewItemFaceEmotionPresenter != null)
            listViewItemFaceEmotionPresenter.dismiss();
    }

}

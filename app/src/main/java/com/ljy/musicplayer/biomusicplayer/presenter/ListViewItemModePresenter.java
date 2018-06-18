package com.ljy.musicplayer.biomusicplayer.presenter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;
import com.ljy.musicplayer.biomusicplayer.model.MindwaveController;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemMode;

public class ListViewItemModePresenter extends ListViewItemPresenter{
    // MVP 모델
    private ListViewItemMode model;
    private Fragment view;

    // 구현에 필요한 변수
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BioMusicPlayerApplication app;
    private ProgressDialog progressDialog;
    private AppCompatActivity activity;

    public ListViewItemModePresenter(ListViewAdapter adapter, ListViewItemMode model, Fragment view) {
        super(adapter);
        this.model = model;
        this.view = view;

        this.activity =  (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();
    }

    @Override
    public void setEvent() {
        if(model.getView() == null) return;
        model.setOnToggleButtonClick(toggleButtonEvent);
    }

    private View.OnClickListener toggleButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View toggleButtonView) {
            ToggleButton toggleButton = (ToggleButton) toggleButtonView;

            if (mBluetoothAdapter == null) {
                // bluetooth error
                Log.d("pwy BluetoothAdapter", "Bluetooth not available");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    view.startActivity(intent);
                    toggleButton.setChecked(false);
                    return;
                }
            }

            //공통변수 수정
            app.setStudyMode(!app.isStudyMode());

            //mindwave 초기화
            if (progressDialog == null) progressDialog = new ProgressDialog(activity);

            MindwaveController mc = new MindwaveController(view.getContext(), progressDialog, app.getMindwave());

            if (!app.getMindwave().isMindwaveConnected() && toggleButton.isChecked()) {
                mc.execute();
            } else {
                app.getMindwave().stop();
            }

            //로깅
            Log.d("pwy " + app.getClass().getSimpleName() + ".isStudyMode()", app.isStudyMode() + "");
            Log.d("pwy Toggle Button State", toggleButton.isChecked() + "");

            //UI 업데이트
            View listItemModeView = ((ListViewItem) getListViewAdapter().getItem(0)).getView();
            TextView txtModeName = listItemModeView.findViewById(R.id.txtModeName);

            if (app.isStudyMode()) {
                activity.getSupportActionBar().setBackgroundDrawable(view.getResources().getDrawable(R.drawable.bg_gradient_purple));
                activity.findViewById(R.id.player).setBackground(view.getResources().getDrawable(R.drawable.bg_gradient_purple));
                activity.findViewById(R.id.tabs).setBackground(view.getResources().getDrawable(R.drawable.bg_gradient_purple));
                txtModeName.setTextColor(Color.WHITE);
                listItemModeView.setBackgroundColor(Color.parseColor("#404040"));
            } else {
                activity.getSupportActionBar().setBackgroundDrawable(view.getResources().getDrawable(R.drawable.bg_gradient));
                activity.findViewById(R.id.player).setBackground(view.getResources().getDrawable(R.drawable.bg_gradient));
                activity.findViewById(R.id.tabs).setBackground(view.getResources().getDrawable(R.drawable.bg_gradient));
                txtModeName.setTextColor(Color.BLACK);

                TypedValue a = new TypedValue();
                activity.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    // windowBackground is a color
                    int color = a.data;
                    listItemModeView.setBackgroundColor(color);
                } else {
                    // windowBackground is not a color, probably a drawable
                    Drawable d = activity.getResources().getDrawable(a.resourceId);
                    listItemModeView.setBackground(d);
                }
            }

            toggleButton.setChecked(app.isStudyMode());
            getListViewAdapter().notifyDataSetChanged();

        }
    };

}

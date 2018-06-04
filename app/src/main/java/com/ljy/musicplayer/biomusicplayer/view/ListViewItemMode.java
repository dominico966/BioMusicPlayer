package com.ljy.musicplayer.biomusicplayer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

/**
 * Created by wjddp on 2018-03-29.
 */

public class ListViewItemMode extends ListViewItem {

    private String modeName;
    private Boolean modeState;

    private View.OnClickListener onToggleButtonClick;

    public ListViewItemMode() {
        super();
        super.setLayoutId(R.layout.listview_item_mode);
    }

    public Boolean getModeState() {
        return modeState;
    }

    public void setModeState(Boolean modeState) {
        this.modeState = modeState;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if(view != null) return view;

        view = inflater.inflate(getLayoutId(),parent,false);
        super.setView(view);

        // 선언
        TextView modeName = view.findViewById(R.id.txtModeName);
        ToggleButton toggleButton = view.findViewById(R.id.toggleButton);

        // 설정
        toggleButton.setOnClickListener(onToggleButtonClick);
        modeName.setText(this.getModeName());

        return view;
    }

    public void setOnToggleButtonClick(View.OnClickListener onToggleButtonClick) {
        this.onToggleButtonClick = onToggleButtonClick;
    }
}

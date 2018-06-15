package com.ljy.musicplayer.biomusicplayer.view;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

import java.util.ArrayList;

public class ListViewItemSuggest extends ListViewItemSong {

    public static ArrayList<ListViewItemSuggest> suggests = new ArrayList<>();

    public ListViewItemSuggest() {
        super();
        super.setLayoutId(R.layout.listview_item_suggest);
    }

    @Override
    public View getView(final LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(getLayoutId(),parent,false);
        super.setView(view);

        //선언
        ImageView musicImg = view.findViewById(R.id.img_music_album);
        TextView musicName = view.findViewById(R.id.text_name_music);
        TextView musicSinger = view.findViewById(R.id.text_name_singer);
        TextView musicDuration = view.findViewById(R.id.text_music_duration);

        //설정
        musicImg.setImageDrawable(getMusicImg());
        musicName.setText(getMusicName());
        musicSinger.setText(getSingerName());

        long min = this.getDuration() / 1000 / 60;
        long sec = this.getDuration() / 1000 % 60;
        musicDuration.setText(String.format(inflater.getContext().getString(R.string.time_format), (int) min, (int) sec));

        musicName.setEllipsize(TextUtils.TruncateAt.END);
        musicName.setSingleLine(true);

        musicSinger.setEllipsize(TextUtils.TruncateAt.END);
        musicSinger.setSingleLine(true);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BioMusicPlayerApplication.getInstance().getServiceInterface().play(suggests.indexOf(ListViewItemSuggest.this)); // 선택한 오디오재생
                Toast.makeText(inflater.getContext(), suggests.indexOf(ListViewItemSuggest.this) + " " + getMusicName(), Toast.LENGTH_SHORT).show();
            }
        };

        view.setOnClickListener(onClickListener);

        return view;
    }

}
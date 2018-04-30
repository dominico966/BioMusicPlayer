package com.ljy.musicplayer.biomusicplayer.listview.tab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ljy.musicplayer.biomusicplayer.Mindwave;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.listview.ListViewItem;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoSignalQuality;

public class ListViewItemMindwaveState extends ListViewItem {
    Mindwave mMindwave;

    public ListViewItemMindwaveState(Mindwave mindwave) {
        this.mMindwave = mindwave;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;

        view = inflater.inflate(getLayoutId(), parent, false);
        super.setView(view);

        // 선언
        final ImageView[] imgViews = {
                view.findViewById(R.id.state_no_signal),
                view.findViewById(R.id.state_connecting1),
                view.findViewById(R.id.state_connecting2),
                view.findViewById(R.id.state_connecting3),
                view.findViewById(R.id.state_connected)
        };

        // 설정
        mMindwave.setOnSignalQualityListener(new NskAlgoSdk.OnSignalQualityListener() {
            @Override
            public void onSignalQuality(final int quality) {
                mMindwave.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(ImageView iv : imgViews) {
                            iv.setVisibility(View.INVISIBLE);
                        }

                        switch (NskAlgoSignalQuality.values()[quality]) {
                            case NSK_ALGO_SQ_NOT_DETECTED:
                                imgViews[1].setVisibility(View.VISIBLE);
                                break;
                            case NSK_ALGO_SQ_POOR:
                                imgViews[2].setVisibility(View.VISIBLE);
                                break;
                            case NSK_ALGO_SQ_MEDIUM:
                                imgViews[3].setVisibility(View.VISIBLE);
                                break;
                            case NSK_ALGO_SQ_GOOD:
                                imgViews[4].setVisibility(View.VISIBLE);
                                break;
                            default:
                                imgViews[0].setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        return view;
    }

}

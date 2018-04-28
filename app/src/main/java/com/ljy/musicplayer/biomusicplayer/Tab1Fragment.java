package com.ljy.musicplayer.biomusicplayer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.neurosky.AlgoSdk.NskAlgoType;

public class Tab1Fragment extends Fragment {

    private final int BT_REQUEST_ENABLE = 0;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private BioMusicPlayerApplication app;
    private BluetoothAdapter mBluetoothAdapter;
    private Mindwave mMindwave;

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        //Application 공통변수
        app = (BioMusicPlayerApplication) getActivity().getApplication();
        mMindwave = new Mindwave(getActivity(),
                BluetoothAdapter.getDefaultAdapter(),
                new NskAlgoType[]{
                        NskAlgoType.NSK_ALGO_TYPE_ATT,
                        NskAlgoType.NSK_ALGO_TYPE_MED,
                        NskAlgoType.NSK_ALGO_TYPE_BLINK,
                        NskAlgoType.NSK_ALGO_TYPE_BP
                }) {
            @Override
            public void onMindwaveConnected() {

            }
        };
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        app.setMindwave(mMindwave);

        listView = view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(app);

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);

        //리스트뷰 아이템 추가
        listViewAdapter.addItem("Mode name", app.isStudyMode(), listViewItemModeToggleButtonEvent);
        listViewAdapter.addItem(mMindwave);
        listViewAdapter.addItem(R.drawable.test, "test");

        return view;
    }

    // 이 밑으로는 이벤트만 선언
    private View.OnClickListener listViewItemModeToggleButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ToggleButton toggleButton = (ToggleButton) view;

            if (mBluetoothAdapter == null) {
                // bluetooth error
                Log.d("pwy BluetoothAdapter", "Bluetooth not available");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                    toggleButton.setChecked(false);
                    return;
                }
            }
            //공통변수 수정
            app.setStudyMode(!app.isStudyMode());

            //UI 수정
            toggleButton.setChecked(app.isStudyMode());

            //mindwave 초기화
            MindwaveController mc = new MindwaveController(getContext(), mMindwave);
            if (!mMindwave.isMindwaveConnected() && toggleButton.isChecked()) {
                mc.execute();
            } else {
                mMindwave.stop();
            }

            listViewAdapter.refresh();

            //로깅
            Log.d("pwy " + app.getClass().getSimpleName() + ".isStudyMode()", app.isStudyMode() + "");
            Log.d("pwy Toggle Button State", toggleButton.isChecked() + "");

        }
    };

}

package com.ljy.musicplayer.biomusicplayer;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // music setting 변수
    private final static int LOADER_ID = 0x001;

    private RecyclerView mRecyclerView;

    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private ImageButton mBtnPlayPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //사용자이름
        setTitle("홍길동");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.bg_gradient));


        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout =findViewById(R.id.tabs);


        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());


        //탭 추가
        adapter.addFragment(new Tab1Fragment(),"Tab1");
        adapter.addFragment(new Tab2Fragment(),"Tab2");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        //MiniPlayer 세팅
        mImgAlbumArt = findViewById(R.id.img_albumart);
        mTxtTitle = findViewById(R.id.txt_title);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);

        mTxtTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTxtTitle.setMarqueeRepeatLimit(-1);
        mTxtTitle.setSingleLine(true);
        mTxtTitle.setSelected(true);
        mTxtTitle.requestFocus();
        // music Player 실행
        registerBroadcast();
        updateUI();
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    /*브로드캐스트 리시버*/
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    //브로드캐스트 등록
    public void registerBroadcast() {
        Log.d("registerBroadcast", "registerBroadcast() start...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioService.BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        Log.d("registerBroadcast end", "registerBroadcast() END...");
    }

    //해제
    public void unregisterBroadcast() {
        unregisterReceiver(mBroadcastReceiver);
    }

    //UI update
    private void updateUI() {
        BioMusicPlayerApplication.getInstance().getServiceInterface().setPlayList(ListViewItemSong.songList);

        Log.d("cehck", "AudioApplication.getInstance().getServiceInterface().isPlaying() = " +
                "?????");

        if (BioMusicPlayerApplication.getInstance().getServiceInterface().isPlaying()) {
            mBtnPlayPause.setImageResource(R.drawable.pause);   //실행 중이면
        } else {
            mBtnPlayPause.setImageResource(R.drawable.play);    //중지 중이면
        }
        ListViewItemSong audioItem = BioMusicPlayerApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            mImgAlbumArt.setImageDrawable(audioItem.getMusicImg());
            mTxtTitle.setText(audioItem.getMusicName());
        } else {
            mImgAlbumArt.setImageResource(R.drawable.empty_albumart);
            mTxtTitle.setText("재생중인 음악이 없습니다.");
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    //OnClick 리스너 구현
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_miniplayer:
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                break;
            case R.id.btn_rewind:
                // 이전곡으로 이동
                BioMusicPlayerApplication.getInstance().getServiceInterface().rewind();
                break;
            case R.id.btn_play_pause:
                // 재생 또는 일시정지
                BioMusicPlayerApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_forward:
                // 다음곡으로 이동
                BioMusicPlayerApplication.getInstance().getServiceInterface().forward();
                break;
        }
    }
}
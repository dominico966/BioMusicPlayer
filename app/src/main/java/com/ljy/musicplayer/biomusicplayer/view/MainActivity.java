package com.ljy.musicplayer.biomusicplayer.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.presenter.TabAdapter;
import com.ljy.musicplayer.biomusicplayer.model.AudioService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImgAlbumArt;
    private TextView mTxtTitle;
    private TextView mTxtCurrentPlayTime;
    private TextView mTxtDuration;
    private ImageButton mBtnPlayPause;
    private SeekBar mSeekBar;

    private Handler mHandler = new Handler();
    Runnable seekbarDurationAnimationTask = new Runnable() {
        @Override
        public void run() {
            int mCurrentPosition = BioMusicPlayerApplication.getInstance().getServiceInterface().getCurrentPlayTime();
            mSeekBar.setProgress(mCurrentPosition);
            mHandler.postDelayed(this, 1000);

            int min = (mCurrentPosition / 1000) / 60;
            int sec = (mCurrentPosition / 1000) % 60;
            mTxtCurrentPlayTime.setText(String.format(getString(R.string.time_format), min, sec));
        }
    };

    // voice record 변수
    Intent intent;
    SpeechRecognizer mRecognizer;
    Boolean isVoiceExecute = false;
    private String voiceRead = "";
    private String audioEventString[] = {"다음", "이전", "정지", "재생"};
    private static final int REQUEST_PERMISSIONS = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //사용자이름
        setTitle("홍길동");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.bg_gradient));

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());

        //탭 추가
        adapter.addFragment(new Tab1Fragment(), "Tab1");
        adapter.addFragment(new Tab2Fragment(), "Tab2");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        //MiniPlayer 세팅
        mImgAlbumArt = findViewById(R.id.img_albumart);
        mTxtTitle = findViewById(R.id.txt_title);
        mTxtCurrentPlayTime = findViewById(R.id.txt_music_current_playtime);
        mTxtDuration = findViewById(R.id.txt_music_duration);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mSeekBar = findViewById(R.id.seekbar_music_duration);

        // music Player 실행
        mTxtCurrentPlayTime.setText(String.format(getString(R.string.time_format), 0, 0));
        mTxtDuration.setText(String.format(getString(R.string.time_format), 0, 0));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    BioMusicPlayerApplication.getInstance().getServiceInterface().seek(progress);
                }

                if (progress == seekBar.getMax()) {
                    seekBar.setProgress(0);
                    mTxtCurrentPlayTime.setText(String.format(getString(R.string.time_format), 0, 0));
                }
            }
        });

        registerBroadcast();
        updateUI();

        //퍼미션
        getPermissions(new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });

        // Record 장전
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(new VoiceListener());
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
            mHandler.post(seekbarDurationAnimationTask);

            mTxtTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTxtTitle.setMarqueeRepeatLimit(-1);
            mTxtTitle.setSingleLine(true);
            mTxtTitle.setSelected(true);
            mTxtTitle.requestFocus();

        } else {
            mBtnPlayPause.setImageResource(R.drawable.play);    //중지 중이면
            mHandler.removeCallbacks(seekbarDurationAnimationTask);

            mTxtTitle.setSelected(false);
        }
        ListViewItemSong audioItem = BioMusicPlayerApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            mImgAlbumArt.setImageDrawable(audioItem.getMusicImg());
            mTxtTitle.setText(audioItem.getMusicName());

            int duration = BioMusicPlayerApplication.getInstance().getServiceInterface().getDuration();
            int min = (duration / 1000) / 60;
            int sec = (duration / 1000) % 60;
            mTxtDuration.setText(String.format(getString(R.string.time_format), min, sec));
            mSeekBar.setMax(duration);
            mHandler.post(seekbarDurationAnimationTask);
        } else {
            mImgAlbumArt.setImageResource(R.drawable.empty_albumart);
            mTxtTitle.setText("재생중인 음악이 없습니다.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                //permission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 보이스 icon 클릭 시
        if (item.getItemId() == R.id.menu_voice_scan) {
            if (!isVoiceExecute) {
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                Log.d("package", getPackageName() + "");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

                isVoiceExecute = true;
                invalidateOptionsMenu();

                Toast.makeText(this, "음성인식 실행", Toast.LENGTH_SHORT).show();
                mRecognizer.startListening(intent);     // 음성 읽기 시작.
            } else {
                isVoiceExecute = false;
                invalidateOptionsMenu();
                Toast.makeText(this, "음성인식 취소", Toast.LENGTH_SHORT).show();
                mRecognizer.stopListening();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
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

    // Voice 읽고나서 어떤 이벤트 할거니
    class VoiceListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
        }

        @Override
        public void onResults(Bundle bundle) {

            Log.d("in", "result result");
            ArrayList<String> matches =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            voiceRead = matches.get(0);

            Log.d("eee", matches.size() + "");


            String getVoice = "";
            boolean isFind = false;
            int index = 0;

            // 오디오 이벤트 중 하나가 나오면
            for (int i = 0; i < matches.size(); i++) {
                for (int j = 0; j < audioEventString.length; j++) {
                    Log.d("log", " i : " + i + "j" + j);
                    if (audioEventString[j].equals(matches.get(i))) {
                        index = j;
                        isFind = true;

                        switch (j) {
                            case 0: //다음
                                BioMusicPlayerApplication.getInstance().getServiceInterface().forward();
                                Toast.makeText(MainActivity.this, "다음노래 재생", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:    //이전
                                BioMusicPlayerApplication.getInstance().getServiceInterface().rewind();
                                Toast.makeText(MainActivity.this, "이전노래 재생", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                BioMusicPlayerApplication.getInstance().getServiceInterface().togglePlay();
                                Toast.makeText(MainActivity.this, "재생/일시정지", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                BioMusicPlayerApplication.getInstance().getServiceInterface().togglePlay();
                                Toast.makeText(MainActivity.this, "재생/일시정지", Toast.LENGTH_SHORT).show();
                                break;
                            default:


                        }
                        break;
                    }
                }
                if (isFind) {
                    break;
                }
            }

            if (!isFind) {
                Toast.makeText(MainActivity.this, "해당 명령어는 유효하지 않습니다 : " + matches.get(0), Toast.LENGTH_SHORT).show();

            } else


                isVoiceExecute = false;
            invalidateOptionsMenu();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    }

    public void getPermissions(String[] permissions) {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    permissions,
                    REQUEST_PERMISSIONS);
        }
    }

}
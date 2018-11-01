package com.ljy.musicplayer.biomusicplayer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;
import com.ljy.musicplayer.biomusicplayer.model.Mindwave;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoSignalQuality;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.Arrays;

public class ListViewItemMindwaveEeg extends ListViewItem {
    private Activity activity;
    private LineChart mStatusChart;
    private boolean isGoodSignal = false;

    public ListViewItemMindwaveEeg(Activity activity) {
        super();
        super.setLayoutId(R.layout.listview_item_mindwave_eeg_chart);
        this.activity = activity;
    }

    @Override
    public View getView(final LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;
        view = inflater.inflate(getLayoutId(), parent, false);

        initChart(view);

        final Mindwave mMindwave = BioMusicPlayerApplication.getInstance().getMindwave();
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
                        for (ImageView iv : imgViews) {
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

                        switch (NskAlgoSignalQuality.values()[quality]) {
                            case NSK_ALGO_SQ_GOOD:
                                isGoodSignal = true;
                                break;
                            default:
                                isGoodSignal = false;
                        }
                    }
                });
            }
        });


        mMindwave.setOnBPAlgoIndexListener(new NskAlgoSdk.OnBPAlgoIndexListener() {
            @Override
            public void onBPAlgoIndex(final float delta, final float theta, final float alpha, final float beta, final float gamma) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("pwy.addRaw", Arrays.toString(new float[]{delta, theta, alpha, beta, gamma}));
                        addRawEntry(new float[]{delta, theta, alpha, beta, gamma});
                    }
                });

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sendEggInfoToServer(delta, theta, alpha, beta, gamma);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        final View copyView = view;
        mMindwave.setOnAttAlgoIndexListener(new NskAlgoSdk.OnAttAlgoIndexListener() {
            @Override
            public void onAttAlgoIndex(final int i) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String att = String.format("%03d", i);
                        TextView txtAtt = copyView.findViewById(R.id.status_att);
                        txtAtt.setText(att);

                        if (0 < i && i <= 30 && isGoodSignal) {
                            final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(
                                    new long[]{100, 1000}
                                    , -1
                            );
                        }
                    }
                });
            }
        });


        mMindwave.setOnEyeBlinkDetectionListener(new NskAlgoSdk.OnEyeBlinkDetectionListener() {
            Runnable eyeBlinkEvent = new Runnable() {
                @Override
                public void run() {
                    final TextView txtBlink = copyView.findViewById(R.id.status_blink);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtBlink.setBackgroundColor(Color.YELLOW);
                        }
                    });

                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtBlink.setBackgroundColor(Color.parseColor("#606060"));
                            pauseCheck();
                        }
                    });

                }

            };

            @Override
            public void onEyeBlinkDetect(int i) {
                new Thread(eyeBlinkEvent).start();
            }

            private final long FINISH_INTERVAL_TIME = 2000;
            private long backPressedTime = 0;
            private int count = 0;

            public void pauseCheck() {
                long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - backPressedTime;

                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime && count == 0) {
                    count++;
                    backPressedTime = tempTime;
                } else if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime && count == 1) {
                    count++;
                    backPressedTime = tempTime;
                } else if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime && count == 2) {
                    count = 0;
                    Toast.makeText(inflater.getContext(), "3번 깜빡임 감지\n재생/일시정지", Toast.LENGTH_SHORT).show();
                    BioMusicPlayerApplication.getInstance().getServiceInterface().togglePlay();
                } else {
                    count = 0;
                    backPressedTime = tempTime;
                }
            }
        });


        mMindwave.setOnMedAlgoIndexListener(new NskAlgoSdk.OnMedAlgoIndexListener() {
            @Override
            public void onMedAlgoIndex(final int i) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String att = String.format("%03d", i);
                        TextView txtAtt = copyView.findViewById(R.id.status_med);
                        txtAtt.setText(att);
                    }
                });
            }
        });

        return view;
    }

    private void sendEggInfoToServer(final float delta, final float theta, final float alpha, final float beta, final float gamma) throws IOException {
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        jo.addProperty("alpha",alpha);
        jo.addProperty("beta",beta);
        jo.addProperty("theta",theta);
        jo.addProperty("delta",delta);
        jo.addProperty("gamma",gamma);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        okhttp3.RequestBody body = RequestBody.create(json, gson.toJson(jo));
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://172.16.98.50:8080/BioMusicPlayer/setdata.jsp")
                .post(body)
                .build();

        okhttp3.Response response = client.newCall(request).execute();

        Log.d("Egg send to server",response.message());
    }


    private void initChart(View view) {
        mStatusChart = view.findViewById(R.id.lineChart_status);
        mStatusChart.setOnChartValueSelectedListener(null);

        mStatusChart.getDescription().setEnabled(true);

        //Touch 필요없음
        mStatusChart.setTouchEnabled(false);
        mStatusChart.setDragEnabled(true);
        mStatusChart.setScaleEnabled(true);
        mStatusChart.setDrawGridBackground(false);
        mStatusChart.setPinchZoom(false);

        // BackGround 배경색
        mStatusChart.setBackgroundColor(Color.BLACK);
        LineData data = new LineData();

        //점 색깔 dot color
        data.setValueTextColor(Color.WHITE);
        mStatusChart.setData(data);

        Legend l = mStatusChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mStatusChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        //y축 (-10 ~ 20)
        YAxis leftAxis = mStatusChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(-10f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mStatusChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // 차트 선 속성 설정
    private LineDataSet createSet(String tag, int lineColor) {
        LineDataSet set = new LineDataSet(null, tag);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(lineColor);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setDrawCircles(false);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    // 차트 엔트리 추가
    // float delta, float theta, float alpha, float beta, float gamma
    public void addRawEntry(float[] eegValue) {
        LineData data = mStatusChart.getData();

        ILineDataSet[] eeg = new ILineDataSet[eegValue.length];
        String[] name = {"Delta", "Theta", "Alpha", "Beta", "Gamma"};
        String[] color = {"#ffff99", "#ff6699", "#99ff99", "#3366ff", "#669999"};

        if (data != null) {
            for (int i = 0; i < eeg.length; i++) {
                eeg[i] = data.getDataSetByIndex(i);

                if (eeg[i] == null) {
                    eeg[i] = createSet(name[i], Color.parseColor(color[i]));
                    data.addDataSet(eeg[i]);
                }

                data.addEntry(new Entry(eeg[i].getEntryCount(), eegValue[i]), i);
            }
            data.notifyDataChanged();

            mStatusChart.notifyDataSetChanged();
            // 화면 넘어가는 x축 최솟값
            mStatusChart.setVisibleXRangeMaximum(10);
            mStatusChart.moveViewToX(data.getEntryCount());

        }
    }

}

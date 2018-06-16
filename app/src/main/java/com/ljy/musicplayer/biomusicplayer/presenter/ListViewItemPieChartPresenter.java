package com.ljy.musicplayer.biomusicplayer.presenter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.dominic.skuface.FaceApi;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemPieChart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ListViewItemPieChartPresenter extends ListViewItemPresenter implements SwipeRefreshLayout.OnRefreshListener {
    // MVP 모델
    private ListViewItemPieChart model;
    private Fragment view;

    // face api
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

    //Tab2 SwipeRefreshLayout 새로고침 이벤트
    SwipeRefreshLayout srl;

    public ListViewItemPieChartPresenter(ListViewAdapter listViewAdapter, ListViewItemPieChart model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.view = view;

        this.activity = (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();
    }

    @Override
    public void setEvent() {
        PieChart pieChart = model.getPieChart();

        /*
         * data loading codes......
         * */
        FaceApi.Face.Emotion recentEmotion = readRecentFaceEmotionObject();
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if(recentEmotion == null) {
            yValues.add(new PieEntry(0f, "기쁨"));
            yValues.add(new PieEntry(0f, "슬픔"));
            yValues.add(new PieEntry(0f, "중립"));
            yValues.add(new PieEntry(0f, "화남"));
            yValues.add(new PieEntry(0f, "놀람"));
        } else {
            yValues.add(new PieEntry((float) recentEmotion.happiness, "기쁨"));
            yValues.add(new PieEntry((float) recentEmotion.sadness, "슬픔"));
            yValues.add(new PieEntry((float) recentEmotion.neutral, "중립"));
            yValues.add(new PieEntry((float) recentEmotion.anger, "화남"));
            yValues.add(new PieEntry((float) recentEmotion.surprise, "놀람"));
        }

        /*Description description = new Description();
        description.setText("노래"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);*/

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);

        /*
         * data loading end
         * */

        this.srl = view.getView().findViewById(R.id.swipe_layout);
        srl.setOnRefreshListener(this);
    }

    public FaceApi.Face.Emotion readRecentFaceEmotionObject() {
        FaceApi.Face.Emotion recentEmotion = null;

        File dir = activity.getFilesDir();
        File[] fileList = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(".face");
            }
        });

        Arrays.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        if(fileList.length <= 0) return null;

        try {
            FileInputStream fis = new FileInputStream(fileList[fileList.length - 1]);
            ObjectInputStream ois = new ObjectInputStream(fis);
            recentEmotion = (FaceApi.Face.Emotion) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return recentEmotion;
    }

    @Override
    public void onRefresh() {
        setEvent();
        srl.setRefreshing(false);
    }
}

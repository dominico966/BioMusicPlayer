package com.ljy.musicplayer.biomusicplayer.presenter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemPieChart;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;

import java.util.ArrayList;

public class ListViewItemPieChartPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemPieChart model;
    private Fragment view;

    // face api
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

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
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(34f, "기쁨"));
        yValues.add(new PieEntry(23f, "슬픔"));
        yValues.add(new PieEntry(14f, "중립"));
        yValues.add(new PieEntry(35f, "화남"));
        yValues.add(new PieEntry(40f, "놀람"));

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
    }
}

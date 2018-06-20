package com.ljy.musicplayer.biomusicplayer.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

public class ListViewItemPieChart extends ListViewItem {
    private PieChart pieChart;

    public ListViewItemPieChart() {
        super();
        super.setLayoutId(R.layout.listview_item_piechart);
    }

    public PieChart getPieChart() {
        return pieChart;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;

        view = inflater.inflate(getLayoutId(), parent, false);
        super.setView(view);

        pieChart = view.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        legend.setFormToTextSpace(12f);

        return view;
    }
}
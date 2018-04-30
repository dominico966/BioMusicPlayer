package com.ljy.musicplayer.biomusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Tab2Fragment extends Fragment {
    PieChart pieChart;
    ListView listViewTop;
    Button btn1, btn2, btn3, btn4;
    ArrayList<ListViewItemSuggest> arr;
    private ArrayList<ListViewItemSuggest> suggests = new ArrayList<ListViewItemSuggest>();

    public Tab2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        pieChart = view.findViewById(R.id.piechart);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        listViewTop = view.findViewById(R.id.listViewTop);


        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);


        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);

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


        arr = new ArrayList<>();
        arr.add(new ListViewItemSuggest(R.drawable.a, "first", "볼빨간사춘기"));
        arr.add(new ListViewItemSuggest(R.drawable.b, "two", "볼빨간사춘기"));
        arr.add(new ListViewItemSuggest(R.drawable.c, "first", "볼빨간사춘기"));

        final SuggestAdapter adapter = new SuggestAdapter(getActivity(), R.layout.listview_item_suggest, arr);

        listViewTop.setAdapter(adapter);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arr.clear();
                arr.add(new ListViewItemSuggest(R.drawable.a, "우주를줄게", "볼빨간사춘기"));
                arr.add(new ListViewItemSuggest(R.drawable.b, "two", "레드벨벳"));
                arr.add(new ListViewItemSuggest(R.drawable.c, "first", "코시모프"));
                adapter.refresh();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arr.clear();
                arr.add(new ListViewItemSuggest(R.drawable.d, "비도오고그래서", "헤이즈"));
                arr.add(new ListViewItemSuggest(R.drawable.e, "시차", "우원재"));
                arr.add(new ListViewItemSuggest(R.drawable.f, "너의의미", "아이유"));
                adapter.refresh();
            }
        });

        setListViewHeightBasedOnChildren(listViewTop);

        return view;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int space = 100;
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+space;
        listView.setLayoutParams(params);
    }


    class SuggestAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<ListViewItemSuggest> data;
        private int layout;

        public SuggestAdapter(Context context, int layout, ArrayList<ListViewItemSuggest> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //    Context context = viewGroup.getContext();
            //    int pos = getItemViewType(i);

            if (view == null) {
                view = inflater.inflate(layout, viewGroup, false);
            }

            ListViewItemSuggest listViewItemSuggest = data.get(i);
            ImageView imageView = view.findViewById(R.id.suggest_img);
            imageView.setImageResource(listViewItemSuggest.getImage());
            TextView textView = view.findViewById(R.id.suggest_title);
            textView.setText(listViewItemSuggest.getTitle());
            TextView textView2 = view.findViewById(R.id.suggest_singer);
            textView2.setText(listViewItemSuggest.getSinger());

            return view;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

    }
}

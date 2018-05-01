package com.ljy.musicplayer.biomusicplayer.listview.tab1;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominic.skuface.FaceApi;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.listview.ListViewItem;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ListViewItemFaceEmotion extends ListViewItem {
    private Bitmap bitmap;
    private FaceApi.Face face;

    public ListViewItemFaceEmotion(Bitmap bitmap, FaceApi.Face face) {
        this.bitmap = bitmap;
        this.face = face;

        Log.d("pwy", face.getEmotion().neutral + "");
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;

        view = inflater.inflate(R.layout.listview_item_face_emotion, parent, false);
        super.setView(view);

        ImageView faceImage = view.findViewById(R.id.image_view_face);

        Log.d("pwy", face.getEmotion().neutral + "");

        Rectangle r = face.getFaceRectangle();

        Log.d("pwy.rectangle", face.getFaceRectangle().x + "");

        Bitmap cut = Bitmap.createBitmap(
                bitmap,
                r.x - 50 <= 0 ? 0 : r.x - 50,
                r.y - 50 <= 0 ? 0 : r.y - 50,
                r.x + r.width + 100 >= bitmap.getWidth() ? bitmap.getWidth() - r.x : r.width + 100,
                r.y + r.height + 100 >= bitmap.getHeight() ? bitmap.getHeight() - r.y : r.height + 100);

        //detectAndFrame 이후의 Bitmap
        faceImage.setImageBitmap(cut);

        LinearLayout linearLayout = view.findViewById(R.id.face_info);

        double max = 0;
        Map<String,Double> map = new HashMap<>();
        for (Field data : face.getEmotion().getClass().getDeclaredFields()) {
            String strName = data.getName();
            double value = 0;
            try {
                value = data.getDouble(face.getEmotion());
                max = Math.max(max, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            map.put(strName,value);
        }

        for (Map.Entry<String,Double> e: map.entrySet()) {
            View item = inflater.inflate(R.layout.listview_item_face_emotion_info, linearLayout, false);

            TextView txtViewName = item.findViewById(R.id.name);
            TextView txtViewValue = item.findViewById(R.id.value);

            txtViewName.setText(e.getKey());
            txtViewValue.setText(String.format("%.4f", e.getValue()));

            if(e.getValue() == max) {
                txtViewName.setTextColor(Color.RED);
                txtViewValue.setTextColor(Color.RED);
            }

            linearLayout.addView(item);
            linearLayout.invalidate();
            linearLayout.refreshDrawableState();

            view.invalidate();
            view.refreshDrawableState();
        }
        return view;
    }
}

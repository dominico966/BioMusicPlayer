package com.ljy.musicplayer.biomusicplayer.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominic.skuface.FaceApi;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.ListViewItem;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ListViewItemFaceEmotion extends ListViewItem {

    private Bitmap profileBitmap;
    private FaceApi.Face face;

    public Bitmap getProfileBitmap() {
        return profileBitmap;
    }

    public void setProfileBitmap(Bitmap profileBitmap) {
        this.profileBitmap = profileBitmap;
    }

    public FaceApi.Face getFace() {
        return face;
    }

    public void setFace(FaceApi.Face face) {
        this.face = face;
    }

    public ListViewItemFaceEmotion() {
        super();
        super.setLayoutId(R.layout.listview_item_face_emotion);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if (view != null) return view;

        view = inflater.inflate(getLayoutId(), parent, false);
        super.setView(view);

        ImageView faceImage = view.findViewById(R.id.image_view_face);
        LinearLayout linearLayout = view.findViewById(R.id.face_info);

        //detectAndFrame 이후의 Bitmap
        faceImage.setImageBitmap(profileBitmap);

        if (getFace() == null) return view;

        double max = 0;
        Map<String,Double> map = new HashMap<>();
        for (Field data : getFace().getEmotion().getClass().getDeclaredFields()) {
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

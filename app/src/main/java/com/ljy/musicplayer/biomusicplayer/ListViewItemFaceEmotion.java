package com.ljy.musicplayer.biomusicplayer;

import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dominic.skuface.FaceApi;

import java.lang.reflect.Field;

public class ListViewItemFaceEmotion extends ListViewItem {
    private Bitmap bitmap;
    private FaceApi.Face face;

    public ListViewItemFaceEmotion(Bitmap bitmap, FaceApi.Face face) {
        this.bitmap = bitmap;
        this.face = face;

        Log.d("pwy",face.getEmotion().neutral+"");
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = super.getView();
        if(view != null) return view;

        view = inflater.inflate(R.layout.listview_item_face_emotion, parent, false);
        super.setView(view);

        ImageView faceImage = view.findViewById(R.id.image_view_face);

        Log.d("pwy",face.getEmotion().neutral+"");

        Rectangle r = face.getFaceRectangle();

        Log.d("pwy.rectangle",face.getFaceRectangle().x+"");

        Bitmap cut = Bitmap.createBitmap(
                bitmap,
                r.x - 50 <= 0 ? 0 : r.x - 50,
                r.y - 50 <= 0 ? 0 : r.y - 50,
                r.x + r.width + 100 >= bitmap.getWidth() ? bitmap.getWidth() - r.x : r.width + 100,
                r.y + r.height + 100 >= bitmap.getHeight() ? bitmap.getHeight() - r.y : r.height + 100);

        //detectAndFrame 이후의 Bitmap
        faceImage.setImageBitmap(cut);

        LinearLayout linearLayout = view.findViewById(R.id.face_info);

        for (Field data : face.getEmotion().getClass().getDeclaredFields()) {
            View item = inflater.inflate(R.layout.listview_item_face_emotion_info, linearLayout, false);

            TextView name = item.findViewById(R.id.name);
            TextView value = item.findViewById(R.id.value);
            name.setText(data.getName());
            try {
                String str = String.format("%.4f",data.getDouble(face.getEmotion()));
                value.setText(str);

                Log.d("pwy"+data.getName(), str);
            } catch (IllegalAccessException e) {
                value.setText("N/A");
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

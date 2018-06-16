package com.ljy.musicplayer.biomusicplayer.presenter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dominic.skuface.FaceApi;
import com.dominic.skuface.FaceDetectionCamera;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.model.FaceCaptureController;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemFaceEmotion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListViewItemFaceEmotionPresenter extends ListViewItemPresenter {
    // MVP 모델
    private ListViewItemFaceEmotion model;
    private Fragment view;
    private FaceApi.Face face;

    // face api
    private final static int FACE_DETECTION = 0;
    private FaceApi faceApi;
    private FaceDetectionCamera faceDetectionCamera;

    private ProgressDialog progressDialog;
    private AppCompatActivity activity;
    private BioMusicPlayerApplication app;

    public ListViewItemFaceEmotionPresenter(ListViewAdapter listViewAdapter, ListViewItemFaceEmotion model, Fragment view) {
        super(listViewAdapter);
        this.model = model;
        this.view = view;
        this.activity = (AppCompatActivity) view.getActivity();
        this.app = (BioMusicPlayerApplication) activity.getApplication();

        if (faceApi == null || faceDetectionCamera == null) {
            faceApi = FaceApi.getInstance();
            faceDetectionCamera = new FaceDetectionCamera(activity);

            faceApi.setOnResponseListener(onResponseListener);
            faceDetectionCamera.setOnFaceDetectedListener(faceDetectedListener);
        }

        if (progressDialog == null)
            progressDialog = new ProgressDialog(activity);
    }

    @Override
    public void setEvent() {
        // 카메라 퍼미션
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, FACE_DETECTION);
        } else {

            FaceCaptureController fcc = new FaceCaptureController(activity, progressDialog, faceApi, faceDetectionCamera);
            fcc.execute();
        }
    }

    public void outputFaceEmotionObject(FaceApi.Face.Emotion userFaceEmotion) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");
        String currentTime = sdf.format(date);

        File file = new File(activity.getFilesDir(), currentTime + ".face");

        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userFaceEmotion);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    // 이 밑으로는 이벤트만 선언
    FaceApi.OnResponseListener onResponseListener = new FaceApi.OnResponseListener() {
        @Override
        public void onResponse(final Bitmap framedImage, final List<FaceApi.Face> faceList) {
            Log.d("pwy", framedImage.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (faceList.isEmpty()) return;

                    face = faceList.get(faceList.size() - 1);

                    // 비트맵 자르기
                    Rectangle r = face.getFaceRectangle();
                    Bitmap cut = Bitmap.createBitmap(
                            framedImage,
                            r.x - 50 <= 0 ? 0 : r.x - 50,
                            r.y - 50 <= 0 ? 0 : r.y - 50,
                            r.x + r.width + 100 >= framedImage.getWidth() ? framedImage.getWidth() - r.x : r.width + 100,
                            r.y + r.height + 100 >= framedImage.getHeight() ? framedImage.getHeight() - r.y : r.height + 100);

                    model.setProfileBitmap(cut);
                    model.setFace(face);
                    getListViewAdapter().notifyDataSetChanged();

                    ActionBar actionBar = activity.getSupportActionBar();
                    BitmapDrawable bd = new BitmapDrawable(activity.getResources(), BioMusicPlayerApplication.resizeBitmap(cut, 120, 120));

                    actionBar.setIcon(bd);
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayUseLogoEnabled(true);

                    outputFaceEmotionObject(model.getFace().getEmotion());
                }
            });
        }
    };

    // 카메라 초기화 및 안면인식 시 이벤트
    FaceDetectionCamera.OnFaceDetectedListener faceDetectedListener = new FaceDetectionCamera.OnFaceDetectedListener() {
        @Override
        public void onFaceDetected(Bitmap capturedFace) {

            //faceImage.setImageBitmap(capturedFace);
            int width = capturedFace.getWidth();
            int height = capturedFace.getHeight();

            Bitmap resize = BioMusicPlayerApplication.resizeBitmap(capturedFace, 600, 600 * height / width);
            faceApi.clearFaceList();
            faceApi.detectAndFrameRest(resize);
            faceDetectionCamera.stopFaceDetection();
        }
    };
}

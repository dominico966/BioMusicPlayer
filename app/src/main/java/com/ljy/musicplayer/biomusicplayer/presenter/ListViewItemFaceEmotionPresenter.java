package com.ljy.musicplayer.biomusicplayer.presenter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.SQLException;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.model.DBOpenHelper;
import com.ljy.musicplayer.biomusicplayer.model.FaceCaptureController;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemFaceEmotion;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSong;
import com.ljy.musicplayer.biomusicplayer.view.ListViewItemSuggest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DBOpenHelper mDBOpenHelper;

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

        mDBOpenHelper = new DBOpenHelper(view.getActivity());
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

    private void randomPlay(FaceApi.Face face) {
        if (face == null) return;

        FaceApi.Face.Emotion emotion = face.getEmotion();

        Map<ListViewItemSong.Genre, Double> map = new HashMap<>();
        map.put(ListViewItemSong.Genre.Happiness, emotion.happiness);
        map.put(ListViewItemSong.Genre.Surprise, emotion.surprise);
        map.put(ListViewItemSong.Genre.Sadness, emotion.sadness);
        map.put(ListViewItemSong.Genre.Anger, emotion.anger);

        ListViewItemSong.Genre genre = null;
        double max = 0;
        for (Map.Entry<ListViewItemSong.Genre, Double> e : map.entrySet()) {
            if (max < e.getValue()) {
                max = e.getValue();
                genre = e.getKey();
            }
        }

        ArrayList<ListViewItemSuggest> items = new ArrayList<>();
        for (ListViewItemSuggest item : ListViewItemSuggest.suggests) {
            if (genre.toString().equals(item.getGenre())) {
                items.add(item);
            }
        }

        int number = (int) (Math.random() * items.size());
        ListViewItemSuggest selected = items.get(number);
        BioMusicPlayerApplication.getInstance().getServiceInterface().play(ListViewItemSuggest.suggests.indexOf(selected));
    }

    public void dismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void sendEmotionInfoToServer(FaceApi.Face face) throws IOException {
        double anger = face.getEmotion().anger;
        double contempt = face.getEmotion().contempt;
        double disgust = face.getEmotion().disgust;
        double fear = face.getEmotion().fear;
        double happiness = face.getEmotion().happiness;
        double neutral = face.getEmotion().neutral;
        double sadness = face.getEmotion().sadness;
        double surprise = face.getEmotion().surprise;

        final Gson gson = new Gson();
        final JsonObject jo = new JsonObject();
        jo.addProperty("anger", anger);
        jo.addProperty("contempt", contempt);
        jo.addProperty("disgust", disgust);
        jo.addProperty("fear", fear);
        jo.addProperty("happiness", happiness);
        jo.addProperty("neutral", neutral);
        jo.addProperty("sadness", sadness);
        jo.addProperty("surprise", surprise);

        final String emotionJson = gson.toJson(jo);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        okhttp3.RequestBody body = RequestBody.create(json, emotionJson);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://172.16.98.50:8080/BioMusicPlayer/emotion.jsp")
                .post(body)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        Log.d("Emotion send to server", response.message());

    }

    private void saveEmotionInfoToDB(FaceApi.Face face) throws SQLException {
        mDBOpenHelper.open();
        mDBOpenHelper.insertEmotion(face);
        mDBOpenHelper.close();
    }

    // 이 밑으로는 이벤트만 선언
    FaceApi.OnResponseListener onResponseListener = new FaceApi.OnResponseListener() {
        @Override
        public void onResponse(final Bitmap framedImage, final List<FaceApi.Face> faceList) {
            Log.d("pwy", framedImage.toString());
            if (faceList.isEmpty()) return;

            face = faceList.get(faceList.size() - 1);

            try {
                saveEmotionInfoToDB(face);
                sendEmotionInfoToServer(face);
            } catch (IOException e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
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
                    randomPlay(face);

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

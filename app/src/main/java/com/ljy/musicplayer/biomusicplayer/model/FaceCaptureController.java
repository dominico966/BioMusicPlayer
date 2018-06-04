package com.ljy.musicplayer.biomusicplayer.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.dominic.skuface.FaceApi;
import com.dominic.skuface.FaceDetectionCamera;

public class FaceCaptureController extends AsyncTask<Void, Void, Void> {

    private Context context;
    private FaceApi faceApi;
    private FaceDetectionCamera faceDetectionCamera;

    private ProgressDialog progressDialog;

    private boolean isCaptured = false;

    private int timeout = 0;


    public FaceCaptureController(Context context, ProgressDialog progressDialog, FaceApi faceApi, FaceDetectionCamera faceDetectionCamera) {
        this.context = context;
        this.faceApi = faceApi;
        this.faceDetectionCamera = faceDetectionCamera;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Activity a = (Activity) context;
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 얼굴정보 초기화
                faceApi.clearFaceList();

                // 카메라 활성
                faceDetectionCamera.startFaceDetection();
            }
        });

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("인식 중 입니다...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (timeout > 40) {
            progressDialog.dismiss();

            AlertDialog ad = new AlertDialog.Builder(context)
                    .setMessage("얼굴을 인식 할 수 없습니다. 전면 카메라가 얼굴을 향하게 해주세요.")
                    .setCancelable(false)
                    .setPositiveButton("확인", null)
                    .create();
            ad.show();
        }
    }

}

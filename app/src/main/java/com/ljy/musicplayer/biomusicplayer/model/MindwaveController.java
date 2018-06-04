package com.ljy.musicplayer.biomusicplayer.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class MindwaveController extends AsyncTask<Void, Void, Exception> {
    private Context mContext;
    private Mindwave mMindwave;
    private ProgressDialog mProgressDialog;

    public MindwaveController(Context context, ProgressDialog progressDialog, Mindwave mindwave) {
        this.mContext = context;
        this.mMindwave = mindwave;
        this.mProgressDialog = progressDialog;
    }

    @Override
    protected Exception doInBackground(Void... voids) {

        try {
            mMindwave.start();
            mMindwave.setRecordStreamFilePath(mContext.getFilesDir().getAbsolutePath());

            // 10초 대기
            int timeout = 0;
            while (!(mMindwave.isMindwaveConnected() || timeout > 40)) {
                Thread.currentThread().sleep(250);
                timeout++;
                Log.d("test", timeout + "");
            }

            // 장치 연결 및 오류 확인
            if (!mMindwave.isMindwaveConnected()) {
                throw new IOException("mindwave not connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("장치를 연결 중 입니다...");
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        mProgressDialog.dismiss();

        // 오류처리
        if (e != null) {
            switch (e.getClass().getSimpleName()) {
                case "IOException": {
                    new AlertDialog.Builder(mContext)
                            .setTitle("장치 오류")
                            .setMessage("장치가 범위 내에 없거나 전원이 인가되지 않았습니다. 확인 후 다시 시도하세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .create()
                            .show();
                }
                break;
            }
        }
    }
}

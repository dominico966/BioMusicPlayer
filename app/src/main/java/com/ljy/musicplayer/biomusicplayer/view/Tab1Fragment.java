package com.ljy.musicplayer.biomusicplayer.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dominic.skuface.FaceApi;
import com.dominic.skuface.FaceDetectionCamera;
import com.ljy.musicplayer.biomusicplayer.BioMusicPlayerApplication;
import com.ljy.musicplayer.biomusicplayer.R;
import com.ljy.musicplayer.biomusicplayer.model.FaceCaptureController;
import com.ljy.musicplayer.biomusicplayer.model.Mindwave;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewAdapter;
import com.ljy.musicplayer.biomusicplayer.presenter.ListViewItemModePresenter;
import com.neurosky.AlgoSdk.NskAlgoType;

import java.io.File;
import java.util.List;

public class Tab1Fragment extends Fragment {

    private final static int FACE_DETECTION = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private BioMusicPlayerApplication app;
    private BluetoothAdapter mBluetoothAdapter;

    private FaceApi faceApi;
    private FaceDetectionCamera faceDetectionCamera;

    private Mindwave mMindwave;

    private ProgressDialog progressDialog;

    private ListViewItemModePresenter listViewItemModePresenter;

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        //Application 공통변수
        app = BioMusicPlayerApplication.getInstance();

        initMindwave();
        checkPermissionReadStorage(getActivity());

        //리스트뷰 작업
        listView = view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter();

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(listViewAdapter);

        ListViewItemMode listViewItemMode = listViewAdapter.addItemMode("Study Mode", app.isStudyMode());
        ListViewItemMindwaveEeg listViewItemMindwaveEeg = listViewAdapter.addItemMindwaveEeg(getActivity());

        //리스트뷰 Presenter
        listViewItemModePresenter = new ListViewItemModePresenter(listViewAdapter,listViewItemMode,this);

        return view;
    }

    private void initMindwave() {
        mMindwave = new Mindwave(getActivity(),
                BluetoothAdapter.getDefaultAdapter(),
                new NskAlgoType[]{
                        NskAlgoType.NSK_ALGO_TYPE_ATT,
                        NskAlgoType.NSK_ALGO_TYPE_MED,
                        NskAlgoType.NSK_ALGO_TYPE_BLINK,
                        NskAlgoType.NSK_ALGO_TYPE_BP
                }) {
            @Override
            public void onMindwaveConnected() {

            }
        };

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        app.setMindwave(mMindwave);
    }

    public void checkPermissionReadStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            loadSongsFromMusicDir();
        }
    }

    private void loadSongsFromMusicDir() {

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File[] list = root.listFiles();
        Log.d("pwy", root.getPath());
        Log.d("pwy", list.length + "");

        app.setMusicDir(root);

        Drawable defaultImage = getResources().getDrawable(R.drawable.empty_albumart);
        Bitmap drawableToBitmap = ((BitmapDrawable) defaultImage).getBitmap();
        BitmapDrawable resize = new BitmapDrawable(getResources(), resizeBitmap(drawableToBitmap, 128, 128 * drawableToBitmap.getHeight() / drawableToBitmap.getWidth()));

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        for (File f : list) {
            BitmapDrawable cover = resize;
            mmr.setDataSource(f.getPath());

            // 이미지
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap resizeBitmap = resizeBitmap(bitmap, 128, 128);
                cover = new BitmapDrawable(getResources(), resizeBitmap);
                bitmap.recycle();
            }

            // 문자열 데이터
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);

            if (title == null) title = f.getName().split("mp3")[0];
            if (artist == null) artist = "";

            listViewAdapter.addItemSong(cover, title, artist, Long.parseLong(duration), f.getPath());
            listViewAdapter.notifyDataSetChanged();
        }
    }

    public static Bitmap resizeBitmap(Bitmap src, float newWidth, float newHeight) {
        Matrix matrix = new Matrix();
        int width = src.getWidth();
        int height = src.getHeight();

        float scaledWidth = newWidth / width;
        float scaledHeight = newHeight / height;
        matrix.postScale(scaledWidth, scaledHeight);

        return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_tab1_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_face_scan:
                Toast.makeText(getActivity(), "얼굴인식", Toast.LENGTH_SHORT).show();

                // 카메라 퍼미션
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, FACE_DETECTION);
                } else {
                    if (faceApi == null)
                        faceApi = FaceApi.getInstance();

                    if (faceDetectionCamera == null)
                        faceDetectionCamera = new FaceDetectionCamera(getActivity());

                    if (progressDialog == null)
                        progressDialog = new ProgressDialog(getActivity());

                    faceApi.setOnResponseListener(onResponseListener);
                    faceDetectionCamera.setOnFaceDetectedListener(faceDetectedListener);

                    FaceCaptureController fcc = new FaceCaptureController(getActivity(), progressDialog, faceApi, faceDetectionCamera);
                    fcc.execute();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                //permission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    loadSongsFromMusicDir();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "We Need permission Storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.dismiss();
    }

    // 이 밑으로는 이벤트만 선언
    FaceApi.OnResponseListener onResponseListener = new FaceApi.OnResponseListener() {
        @Override
        public void onResponse(final Bitmap framedImage, final List<FaceApi.Face> faceList) {
            Log.d("pwy", framedImage.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (faceList.isEmpty()) return;

                    // 비트맵 자르기
                    Rectangle r = faceList.get(faceList.size()-1).getFaceRectangle();
                    Bitmap cut = Bitmap.createBitmap(
                            framedImage,
                            r.x - 50 <= 0 ? 0 : r.x - 50,
                            r.y - 50 <= 0 ? 0 : r.y - 50,
                            r.x + r.width + 100 >= framedImage.getWidth() ? framedImage.getWidth() - r.x : r.width + 100,
                            r.y + r.height + 100 >= framedImage.getHeight() ? framedImage.getHeight() - r.y : r.height + 100);

                    listViewAdapter.addItemFaceEmotion(cut, faceList.get(faceList.size() - 1));
                    listViewAdapter.notifyDataSetChanged();

                    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    BitmapDrawable bd = new BitmapDrawable(getResources(),resizeBitmap(cut,120,120));

                    actionBar.setIcon(bd);
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayUseLogoEnabled(true);
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

            Bitmap resize = resizeBitmap(capturedFace, 600, 600 * height / width);
            faceApi.clearFaceList();
            faceApi.detectAndFrameRest(resize);
            faceDetectionCamera.stopFaceDetection();
        }
    };

}

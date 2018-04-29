package com.ljy.musicplayer.biomusicplayer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dominic.skuface.FaceApi;
import com.dominic.skuface.FaceDetectionCamera;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.neurosky.AlgoSdk.NskAlgoType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Tab1Fragment extends Fragment {

    private final static int FACE_DETECTION = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    private BioMusicPlayerApplication app;
    private BluetoothAdapter mBluetoothAdapter;
    private Mindwave mMindwave;
    private FaceApi faceApi;
    private FaceDetectionCamera faceDetectionCamera;

    private ProgressDialog progressDialog;

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        //Application 공통변수
        app = BioMusicPlayerApplication.getInstance();
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

        listView = view.findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter();

        //리스트뷰 Adapter
        listView.setAdapter(listViewAdapter);

        listViewAdapter.addItemMode("Study Mode", app.isStudyMode(), listViewItemModeToggleButtonEvent);
        listViewAdapter.addItemMindwaveState(mMindwave);

        checkPermissionReadStorage(getActivity());

        return view;
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
        BitmapDrawable resize = new BitmapDrawable(getResources(), resizeBitmap(drawableToBitmap, 256, 256 * drawableToBitmap.getHeight() / drawableToBitmap.getWidth()));

        try {
            for (File f : list) {
                Mp3File mp3File = new Mp3File(f.getAbsolutePath());

                String fileName = f.getName().split(".mp3")[0];
                String songName = null;
                String singerName = null;
                String filePath = f.getAbsolutePath();

                if (mp3File.hasId3v1Tag()) {
                    ID3v1 tag = mp3File.getId3v1Tag();

                    if (tag.getTitle() != null) {
                        songName = new String(tag.getTitle().getBytes("ISO-8859-1"), "EUC-KR");
                    } else {
                        songName = fileName;
                    }

                    if (tag.getArtist() != null) {
                        singerName = new String(tag.getArtist().getBytes("ISO-8859-1"), "EUC-KR");
                    } else {
                        singerName = fileName;
                    }

                    listViewAdapter.addItemSong(resize, songName, singerName, filePath);
                    Log.d("pwy title", tag.getTitle());

                } else if (mp3File.hasId3v2Tag()) {
                    ID3v2 tag = mp3File.getId3v2Tag();
                    byte[] imageData = tag.getAlbumImage();

                    if (tag.getTitle() != null) {
                        songName = new String(tag.getTitle().getBytes("ISO-8859-1"), "EUC-KR");
                    } else {
                        songName = fileName;
                    }

                    if (tag.getArtist() != null) {
                        singerName = new String(tag.getArtist().getBytes("ISO-8859-1"), "EUC-KR");
                    } else {
                        singerName = fileName;
                    }

                    if (imageData != null) {
                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length - 1);
                        Bitmap resizeBitmap = resizeBitmap(imageBitmap, 256, 256 * drawableToBitmap.getHeight() / drawableToBitmap.getWidth());
                        BitmapDrawable bd = new BitmapDrawable(getResources(), resizeBitmap);
                        listViewAdapter.addItemSong(bd, songName, singerName, filePath);

                        imageBitmap.recycle();
                    } else {
                        listViewAdapter.addItemSong(resize, songName, singerName, filePath);
                    }

                    Log.d("pwy title", tag.getTitle() + "");
                }
            }

        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }

        listViewAdapter.refresh();
    }

    // 이 밑으로는 이벤트만 선언
    private View.OnClickListener listViewItemModeToggleButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ToggleButton toggleButton = (ToggleButton) view;

            if (mBluetoothAdapter == null) {
                // bluetooth error
                Log.d("pwy BluetoothAdapter", "Bluetooth not available");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                    toggleButton.setChecked(false);
                    return;
                }
            }
            //공통변수 수정
            app.setStudyMode(!app.isStudyMode());

            //UI 수정
            toggleButton.setChecked(app.isStudyMode());

            //mindwave 초기화
            if (progressDialog == null) progressDialog = new ProgressDialog(getActivity());
            MindwaveController mc = new MindwaveController(getContext(), progressDialog, mMindwave);
            if (!mMindwave.isMindwaveConnected() && toggleButton.isChecked()) {
                mc.execute();
            } else {
                mMindwave.stop();
            }

            listViewAdapter.refresh();
            listView.invalidate();

            //로깅
            Log.d("pwy " + app.getClass().getSimpleName() + ".isStudyMode()", app.isStudyMode() + "");
            Log.d("pwy Toggle Button State", toggleButton.isChecked() + "");

        }
    };

    // 이벤트
    FaceApi.OnResponseListener onResponseListener = new FaceApi.OnResponseListener() {
        @Override
        public void onResponse(final Bitmap framedImage, final List<FaceApi.Face> faceList) {
            Log.d("pwy", "OnResponseListener start");
            Log.d("pwy",framedImage.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (faceList.isEmpty()) return;

                    Log.d("pwy",faceList.get(faceList.size()-1).getEmotion().neutral+"");

                    listViewAdapter.addItemFaceEmotion(framedImage, faceList.get(faceList.size()-1));
                    listViewAdapter.refresh();
                    Log.d("pwy", "OnResponseListener end");
                }
            });
        }
    };

    // 카메라 초기화 및 안면인식 시 이벤트
    FaceDetectionCamera.OnFaceDetectedListener faceDetectedListener = new FaceDetectionCamera.OnFaceDetectedListener() {
        @Override
        public void onFaceDetected(Bitmap capturedFace) {
            Log.d("pwy", "OnFaceDetectedListener start");

            //faceImage.setImageBitmap(capturedFace);
            int width = capturedFace.getWidth();
            int height = capturedFace.getHeight();

            Bitmap resize = resizeBitmap(capturedFace, 600, 600 * height / width);
            faceApi.clearFaceList();
            faceApi.detectAndFrameRest(resize);
            faceDetectionCamera.stopFaceDetection();
            Log.d("pwy", "OnFaceDetectedListener end");
        }
    };

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
        inflater.inflate(R.menu.menu, menu);
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
}

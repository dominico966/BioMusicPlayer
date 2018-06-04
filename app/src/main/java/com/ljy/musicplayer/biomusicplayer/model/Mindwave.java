package com.ljy.musicplayer.biomusicplayer.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoType;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.EEGPower;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public abstract class Mindwave extends NskAlgoSdk {

    private boolean isMindwaveConnected = false;

    private TgStreamReader tgStreamReader;
    private short raw_data[] = {0};
    private int raw_data_index = 0;

    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private NskAlgoType[] mDataTypes;

    private Mindwave.OnEEGPowerReadListener mOnEEGPowerReadListener;

    public Mindwave(Activity activity, BluetoothAdapter bluetoothAdapter, NskAlgoType[] dataTypes) {
        super();
        mActivity = activity;
        mBluetoothAdapter = bluetoothAdapter;
        raw_data = new short[512];
        mDataTypes = dataTypes;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public abstract void onMindwaveConnected();


    // algoType 추가 메소드
    private void setAlgoType(NskAlgoType[] args) {
        int algoTypes = 0;
        for (NskAlgoType v : args) {
            algoTypes += v.value;
        }

        int ret = this.NskAlgoInit(algoTypes, mActivity.getFilesDir().getAbsolutePath());
    }

    //TgHandler
    private TgStreamHandler callback = new TgStreamHandler() {
        @Override
        public void onStatesChanged(int connectionStates) {
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    break;

                case ConnectionStates.STATE_CONNECTED:
                    onMindwaveConnected();
                    isMindwaveConnected = true;
                    tgStreamReader.setGetDataTimeOutTime(10);
                    tgStreamReader.start();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Connected!!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    setAlgoType(mDataTypes);
                    Mindwave.this.NskAlgoStart(false);
                    break;

                case ConnectionStates.STATE_WORKING:
                    tgStreamReader.startRecordRawData();
                    break;

                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    tgStreamReader.stop();
                    tgStreamReader.close();

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Time Out!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    tgStreamReader.stopRecordRawData();
                    isMindwaveConnected = false;
                    break;

                case ConnectionStates.STATE_STOPPED:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Stopped", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case ConnectionStates.STATE_DISCONNECTED:
                    isMindwaveConnected = false;
                    break;

                case ConnectionStates.STATE_ERROR:
                    isMindwaveConnected = false;
                    break;

                case ConnectionStates.STATE_FAILED:
                    isMindwaveConnected = false;
                    break;
            }
        }

        @Override
        public void onRecordFail(int flag) {
        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            switch (datatype) {
                case MindDataType.CODE_ATTENTION:
                    short attValue[] = {(short) data};
                    Mindwave.this.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ATT.value, attValue, 1);
                    break;
                case MindDataType.CODE_MEDITATION:
                    short medValue[] = {(short) data};
                    Mindwave.this.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_MED.value, medValue, 1);
                    break;
                case MindDataType.CODE_POOR_SIGNAL:
                    short pqValue[] = {(short) data};
                    Mindwave.this.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_PQ.value, pqValue, 1);
                    break;
                case MindDataType.CODE_RAW:
                    raw_data[raw_data_index++] = (short) data;
                    if (raw_data_index == 512) {
                        Mindwave.this.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                        raw_data_index = 0;
                    }
                    break;
                case MindDataType.CODE_EEGPOWER:
                    EEGPower eeg = (EEGPower) obj;
                    if (eeg.isValidate() && mOnEEGPowerReadListener != null) {
                        mOnEEGPowerReadListener.onEEGPowerRead(eeg);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public interface OnEEGPowerReadListener {
        void onEEGPowerRead(EEGPower eegPower);
    }

    public void setOnEEGPowerReadListener(OnEEGPowerReadListener e) {
        mOnEEGPowerReadListener = e;
    }

    public boolean isMindwaveConnected() {
        return isMindwaveConnected;
    }

    public void setRecordStreamFilePath(String path) {
        tgStreamReader.setRecordStreamFilePath(path);
    }

    public void start() {
        BluetoothDevice Mindwave = null;
        for (BluetoothDevice bd : mBluetoothAdapter.getBondedDevices()) {
            if (bd.getName().contains("Mind")) {
                Mindwave = bd;
                break;
            }
        }
        tgStreamReader = new TgStreamReader(Mindwave, callback);

        BluetoothSocket bs = null;

        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
            tgStreamReader.stop();
            tgStreamReader.close();
        }
        tgStreamReader.startLog();

        tgStreamReader.connect();
    }

    public void stop() {
        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
            tgStreamReader.stop();
            tgStreamReader.close();
        }
        this.NskAlgoStop();
    }

    @Override
    public void setOnAttAlgoIndexListener(OnAttAlgoIndexListener listener) {
        super.setOnAttAlgoIndexListener(listener);
    }

    @Override
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        super.setOnStateChangeListener(listener);
    }

    @Override
    public void setOnSignalQualityListener(OnSignalQualityListener listener) {
        super.setOnSignalQualityListener(listener);
    }

    static public abstract class RawDataReader {

        TgStreamReader tgStreamReader;
        ArrayList<Integer> buffer = new ArrayList<>();

        boolean readComplete = false;

        public RawDataReader(String path) throws FileNotFoundException {
            this(new File(path));
        }

        public RawDataReader(File file) throws FileNotFoundException {
            this(new FileInputStream(file));
        }

        public RawDataReader(InputStream inputStream) {
            tgStreamReader = new TgStreamReader(inputStream, callback);
            tgStreamReader.connect();
        }

        private TgStreamHandler callback = new TgStreamHandler() {
            @Override
            public void onStatesChanged(int connectionStates) {
                // TODO Auto-generated method stub
                switch (connectionStates) {
                    case ConnectionStates.STATE_CONNECTING:
                        break;
                    case ConnectionStates.STATE_CONNECTED:
                        tgStreamReader.setReadFileBlockSize(16);
                        tgStreamReader.setReadFileDelay(0);
                        break;
                    case ConnectionStates.STATE_COMPLETE:
                        tgStreamReader.stop();
                        tgStreamReader.close();

                        Log.d(TAG, "complete");
                        readComplete = true;
                        onReadComplete();
                        break;
                    case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    case ConnectionStates.STATE_STOPPED:
                    case ConnectionStates.STATE_DISCONNECTED:
                    case ConnectionStates.STATE_ERROR:
                    case ConnectionStates.STATE_FAILED:
                        Log.d(TAG, "fail");
                        break;
                }
            }

            @Override
            public void onRecordFail(int flag) {
            }

            @Override
            public void onChecksumFail(byte[] payload, int length, int checksum) {
            }

            @Override
            public void onDataReceived(int datatype, int data, Object obj) {
                Message msg = LinkDetectedHandler.obtainMessage();
                msg.what = datatype;
                msg.arg1 = data;
                msg.obj = obj;
                LinkDetectedHandler.sendMessage(msg);
            }
        };

        Handler LinkDetectedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MindDataType.CODE_ATTENTION:
                        buffer.add(msg.arg1);
                        break;
                    case MindDataType.CODE_MEDITATION:
                        break;
                    case MindDataType.CODE_POOR_SIGNAL:
                        break;
                    case MindDataType.CODE_RAW:
                        break;
                    default:
                        break;
                }

                super.handleMessage(msg);
            }
        };

        public ArrayList<Integer> getAttDataArray() {
            if (readComplete) return buffer;
            else return null;
        }

        public abstract void onReadComplete();

        public void read() {
            tgStreamReader.start();
            Log.d(TAG, "Reading...");
        }
    }

}

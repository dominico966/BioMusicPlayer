package com.ljy.musicplayer.biomusicplayer.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.dominic.skuface.FaceApi;

public class DBOpenHelper {

    private static final String DATABASE_NAME = "emotion.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    public void insertEmotion(FaceApi.Face face) {
        ContentValues row = new ContentValues();
        FaceApi.Face.Emotion e = face.getEmotion();
        row.put("anger", e.anger);
        row.put("contempt", e.contempt);
        row.put("disgust", e.disgust);
        row.put("fear", e.fear);
        row.put("happiness", e.happiness);
        row.put("neutral", e.neutral);
        row.put("sadness", e.sadness);
        row.put("surprise", e.surprise);

        mDB.insert("emotion", null, row);
    }

    public FaceApi.Face.Emotion selectEmotionAverage() {
        Cursor c = mDB.rawQuery("select avg(anger),avg(contempt),avg(disgust),avg(fear),avg(happiness),avg(neutral),avg(sadness),avg(surprise) from emotion", null);
        if(c.getCount() <= 0) return null;

        c.moveToNext();
        float anger = c.getFloat(0);
        float contempt = c.getFloat(1);
        float disgust = c.getFloat(2);
        float fear = c.getFloat(3);
        float happiness = c.getFloat(4);
        float neutral = c.getFloat(5);
        float sadness = c.getFloat(6);
        float surprise = c.getFloat(7);
        c.close();

        return new FaceApi.Face.Emotion(anger, contempt, disgust, fear, happiness, neutral, sadness, surprise);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    public DBOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DBOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDB.close();
    }

    private static class DataBases {
        public static final class CreateDB implements BaseColumns {
            public static final String ANGER = "anger";
            public static final String CONTEMPT = "contempt";
            public static final String DISGUST = "disgust";
            public static final String FEAR = "fear";
            public static final String HAPPINESS = "happiness";
            public static final String NEUTRAL = "neutral";
            public static final String SADNESS = "sadness";
            public static final String SURPRISE = "surprise";
            public static final String _TABLENAME = "emotion";
            public static final String _CREATE = "CREATE TABLE IF NOT EXISTS " + _TABLENAME + " ( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`anger` REAL NOT NULL, " +
                    "`contempt` REAL NOT NULL, " +
                    "`disgust` REAL NOT NULL, " +
                    "`fear` REAL NOT NULL, " +
                    "`happiness` REAL NOT NULL, " +
                    "`neutral` REAL NOT NULL, " +
                    "`sadness` REAL NOT NULL, " +
                    "`surprise` REAL NOT NULL )";
        }

    }

}

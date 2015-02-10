package com.example.testapp.clipboardtranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    static final int VERSION = 1;
    static final String FILENAME = "dic";

    static final String TABLE_NAME = "dic";
    static final String COL_ID = "_id";
    static final String COL_TYPE = "t";
    static final String COL_WORD = "word";
    static final String COL_DESC = "desc";
    static final String ALL_COLS[] = {
            COL_ID,
            COL_TYPE,
            COL_WORD,
            COL_DESC,
    };
    public static class Result {
        public int type;
        public String word;
        public String desc;
        Result(Cursor c) {
            type = c.getInt(1);
            word = c.getString(2);
            desc = c.getString(3);
        }
    }

    public static final int TYPE_HAND = 1;
    public static final int TYPE_GENE95 = 2;

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TYPE + " INTEER, " +
            COL_WORD + " TEXT, " +
            COL_DESC + " TEXT)";

    SQLiteDatabase db;

    public DB(Context context) {
        super(context, FILENAME, null, VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public int remove(int type) {
        return db.delete(TABLE_NAME, COL_TYPE + "=?", new String[] { "" + type });
    }

    public long insert(int type, String word, String desc) {
        ContentValues val = new ContentValues();
        val.put(COL_TYPE, type);
        val.put(COL_WORD, word);
        val.put(COL_DESC, desc);
        return db.insert(TABLE_NAME, null, val);
    }

    public int count(int type) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COL_TYPE + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{"" + type});
        try {
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            c.close();
        }
    }

    public List<Result> find(String word, int limit) {
        Cursor c = db.query(TABLE_NAME, ALL_COLS, COL_WORD + " like ?", new String[] { word + '%' }, null, null, COL_WORD + " COLLATE NOCASE");
        List<Result> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(new Result(c));
            if (list.size() >= limit) break;
        }
        c.close();
        return list;
    }
}

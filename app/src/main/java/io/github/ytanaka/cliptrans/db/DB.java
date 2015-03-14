package io.github.ytanaka.cliptrans.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    static final String TAG = DB.class.getSimpleName();

    static final int VERSION = 3;
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
        public String type;
        public String word;
        public String desc;
        Result(Cursor c) {
            type = c.getString(1);
            word = c.getString(2);
            desc = c.getString(3);
        }
    }

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TYPE + " TEXT, " +
            COL_WORD + " TEXT, " +
            COL_DESC + " TEXT)";

    static final String INDEX_NAME = "idx_dic";
    static final String CREATE_INDEX = "CREATE INDEX " + INDEX_NAME + " ON " + TABLE_NAME + "(" + COL_WORD + " COLLATE NOCASE)";

    SQLiteDatabase db;
    SQLiteStatement insertStatement;

    public DB(Context context) {
        super(context, FILENAME, null, VERSION);
        Log.d(TAG, "constructor");
        db = getWritableDatabase();
        insertStatement = db.compileStatement(
                "insert into " + TABLE_NAME + "(" +
                COL_TYPE + "," +
                COL_WORD + "," +
                COL_DESC + ") values (?,?,?)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade(" + oldVersion + ", " + newVersion + ")");
        for (int i = oldVersion; i < newVersion; i++) {
            if (i == 2) {
                Log.d(TAG, "onUpgrade: " + i);
                db.execSQL(CREATE_INDEX);
            } else {
                db.execSQL("DROP INDEX IF EXISTS " + INDEX_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public int remove(String type) {
        return db.delete(TABLE_NAME, COL_TYPE + "=?", new String[] { type });
    }

    /*
     * SQLiteDatabase.insert() から SQLiteStatement.executeInsert() への高速化の効果
     * ZenFone5 23秒 => 18秒
     * Nexis9 8秒 => 7秒
     */
    public long insert(String type, String word, String desc) {
        insertStatement.clearBindings();
        insertStatement.bindAllArgsAsStrings(new String[] { type, word, desc });
        return insertStatement.executeInsert();
    }

    public int count(String type) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COL_TYPE + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ type });
        //noinspection TryFinallyCanBeTryWithResources
        try {
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            c.close();
        }
    }

    public List<Result> find(String word, int limit) {
        Cursor c = db.query(TABLE_NAME, ALL_COLS, COL_WORD + " COLLATE NOCASE LIKE ?", new String[] { word + '%' }, null, null, COL_WORD + " COLLATE NOCASE," + COL_ID);
        List<Result> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(new Result(c));
            if (list.size() >= limit) break;
        }
        c.close();
        return list;
    }

    public Result find1(String word) {
        Cursor c = db.query(TABLE_NAME, ALL_COLS, COL_WORD + " COLLATE NOCASE = ?", new String[] { word }, null, null, null);
        Result ret = null;
        if (c.moveToNext()) ret = new Result(c);
        c.close();
        return ret;
    }
}

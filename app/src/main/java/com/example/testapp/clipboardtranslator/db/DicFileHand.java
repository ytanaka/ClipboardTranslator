package com.example.testapp.clipboardtranslator.db;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.testapp.clipboardtranslator.MyApplication;
import com.example.testapp.clipboardtranslator.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DicFileHand {
    private static final String TAG = DicFileHand.class.getSimpleName();

    private static final String ZIP_ENTRY_NAME = "ejdic-hand-txt/ejdic-hand-utf8.txt";

    public static int extractAndInsertToDb(Context context, Uri uri, Util.Notifier notifier) {
        File file = new File(context.getCacheDir(), "dic.zip");
        try {
            Util.copyContentResolverToFile(context, uri, file);
            ZipFile zip = new ZipFile(file);
            ZipEntry ze = zip.getEntry(ZIP_ENTRY_NAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(zip.getInputStream(ze)));
            return insertToDb(context, in, notifier);
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (!file.delete()) {
                Log.w(TAG, "delete file error: " + file);
            }
        }
        return 0;
    }

    private static int insertToDb(Context context, BufferedReader in, Util.Notifier notifier) throws IOException {
        DB db = MyApplication.instance(context).getDb();
        db.getDb().beginTransaction();
        int count = 0;
        try {
            while (true) {
                String line = in.readLine();
                if (line == null) break;
                String[] spl = line.split("\t", 2);
                if (spl.length != 2) continue;
                db.insert(DB.TYPE_HAND, spl[0], formatDesc(spl[1]));
                count++;
                if (count % 100 == 0) notifier.notify("" + count);
            }
            db.getDb().setTransactionSuccessful();
        } finally {
            db.getDb().endTransaction();
        }
        return count;
    }

    private static String formatDesc(String s) {
        return s.trim().replaceAll(" +/ +", "\n");
    }
}

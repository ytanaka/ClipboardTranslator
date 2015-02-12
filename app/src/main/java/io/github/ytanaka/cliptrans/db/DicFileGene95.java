package io.github.ytanaka.cliptrans.db;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import io.github.ytanaka.cliptrans.MyApplication;
import io.github.ytanaka.cliptrans.util.Util;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class DicFileGene95 {
    private static final String TAG = DicFileGene95.class.getSimpleName();

    private static final String TAR_ENTRY_NAME = "gene.txt";

    public static int extractAndInsertToDb(Context context, Uri uri, Util.Notifier notifier) {
        try {
            GZIPInputStream gis = new GZIPInputStream(context.getContentResolver().openInputStream(uri));
            TarInputStream tis = new TarInputStream(new BufferedInputStream(gis));
            while (true) {
                TarEntry e = tis.getNextEntry();
                if (e == null) break;
                if (e.getName().equals(TAR_ENTRY_NAME)) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(tis, "MS932"));
                    return insertToDb(context, in, notifier);
                }
            }
            return 1;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return 0;
    }

    private static int insertToDb(Context context, BufferedReader in, Util.Notifier notifier) throws IOException {
        DB db = MyApplication.instance(context).getDb();
        db.getDb().beginTransaction();
        int count = 0;
        try {
            String word = null;
            while (true) {
                if (count % 200 == 0) notifier.notify("" + (count / 2));
                String line = in.readLine();
                count++;
                if (count <= 2) continue;
                if (line == null) break;
                if (word == null) {
                    word = line;
                    continue;
                }
                db.insert(DB.TYPE_GENE95, word, formatDesc(line));
                word = null;
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

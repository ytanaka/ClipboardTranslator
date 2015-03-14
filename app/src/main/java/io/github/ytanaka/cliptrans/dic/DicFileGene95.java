package io.github.ytanaka.cliptrans.dic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.util.Util;

public class DicFileGene95 implements Dic {
    private static final String TAG = DicFileGene95.class.getSimpleName();

    static Drawable iconBackground;
    static {
        float[] outer = { 100,100,100,100,100,100,100,100,};
        RoundRectShape s = new RoundRectShape(outer, null, null);
        ShapeDrawable d = new ShapeDrawable(s);
        d.getPaint().setColor(0xffffc400);
        iconBackground = d;
    }

    @Override
    public String getId() {
        return TAG;
    }

    @Override
    public Info getInfo() {
        Info i = new Info();
        i.downloadUrl = "http://www.namazu.org/~tsuchiya/sdic/data/gene.html";
        i.downloadClickTarget = "gene95.tar.gz (tar+gzip圧縮形式)";
        i.downloadFilename = "gene95.tar.???";
        i.downloadFiletype = "application/*";
        i.description = "GENE95 dictionary";
        i.iconText = "GENE95";
        i.iconBackground = iconBackground;

        if (TextUtils.equals(Locale.JAPAN.getCountry(), Locale.getDefault().getCountry())) {
            i.description = "GENE95 辞書";
        }
        return i;
    }

    @Override
    public int extractAndInsertToDb(Context context, Uri uri, DB db, Util.Notifier progressNotifier) {
        try {
            GZIPInputStream gis = new GZIPInputStream(context.getContentResolver().openInputStream(uri));
            TarInputStream tis = new TarInputStream(new BufferedInputStream(gis));
            while (true) {
                TarEntry e = tis.getNextEntry();
                if (e == null) break;
                if (e.getName().equals(TAR_ENTRY_NAME)) {
                    BufferedReader fin = new BufferedReader(new InputStreamReader(tis, "MS932"));
                    return insertToDb(db, fin, progressNotifier);
                }
            }
            return 1;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return 0;
    }

    private static final String TAR_ENTRY_NAME = "gene.txt";

    private static int insertToDb(DB db, BufferedReader in, Util.Notifier notifier) throws IOException {
        Log.d(TAG, "insertToDb(): start");
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
                db.insert(TAG, word, formatDesc(line));
                word = null;
            }
            db.getDb().setTransactionSuccessful();
        } finally {
            db.getDb().endTransaction();
        }
        Log.d(TAG, "insertToDb(): finish " + count);
        return count;
    }

    private static String formatDesc(String s) {
        return s.trim().replaceAll(" +/ +", "\n");
    }
}

package io.github.ytanaka.cliptrans.dic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.util.Util;

public class DicFileHand implements Dic {
    private static final String TAG = DicFileHand.class.getSimpleName();

    static Drawable iconBackground;
    static {
        float[] outer = { 100,100,100,100,100,100,100,100,};
        RoundRectShape s = new RoundRectShape(outer, null, null);
        ShapeDrawable d = new ShapeDrawable(s);
        d.getPaint().setColor(0xff12ff00);
        iconBackground = d;
    }

    @Override
    public String getId() {
        return TAG;
    }

    @Override
    public Info getInfo() {
        Info i = new Info();
        i.downloadUrl = "http://kujirahand.com/web-tools/EJDictFreeDL.php";
        i.downloadClickTarget = "辞書データ(テキスト形式)";
        i.downloadFilename = "ejdic-hand-txt.zip";
        i.downloadFiletype = "application/zip";
        i.description = "English-Japanese dictionary in the public domain (ejdic-hand)";
        i.iconText = "ejdic-hand";
        i.iconBackground = iconBackground;

        if (TextUtils.equals(Locale.JAPAN.getCountry(), Locale.getDefault().getCountry())) {
            i.description = "パブリックドメインの英和辞書データ (ejdic-hand)";
        }
        return i;
    }

    private static final String ZIP_ENTRY_NAME = "ejdic-hand-txt/ejdic-hand-utf8.txt";

    @Override
    public int extractAndInsertToDb(Context context, Uri uri, DB db, Util.Notifier progressNotifier) {
        File file = new File(context.getCacheDir(), "dic.zip");
        try {
            Util.copyContentResolverToFile(context, uri, file);
            ZipFile zip = new ZipFile(file);
            ZipEntry ze = zip.getEntry(ZIP_ENTRY_NAME);
            BufferedReader fin = new BufferedReader(new InputStreamReader(zip.getInputStream(ze)));
            return insertToDb(db, fin, progressNotifier);
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (!file.delete()) {
                Log.w(TAG, "delete file error: " + file);
            }
        }
        return 0;
    }

    private int insertToDb(DB db, BufferedReader in, Util.Notifier notifier) throws IOException {
        Log.d(TAG, "insertToDb(): start");
        db.getDb().beginTransaction();
        int count = 0;
        try {
            while (true) {
                String line = in.readLine();
                if (line == null) break;
                String[] spl = line.split("\t", 2);
                if (spl.length != 2) continue;
                db.insert(getId(), spl[0], formatDesc(spl[1]));
                count++;
                if (count % 100 == 0) notifier.notify("" + count);
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

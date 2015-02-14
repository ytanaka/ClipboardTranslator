package io.github.ytanaka.cliptrans.dic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.util.Util;

public interface Dic {
    public Dic[] LIST = {
            new DicFileGene95(),
            new DicFileHand(),
    };

    /** get unique ID */
    public String getId();

    /** get dictinary info */
    public Info getInfo();

    /**
     * Insert data to DB
     * @param uri Downloaded data
     * @param db SQLite DB
     * @param progressNotifier Notification to user
     * @return fail: 0, success: > 0
     */
    public int extractAndInsertToDb(Context context, Uri uri, DB db, Util.Notifier progressNotifier);

    public class Info {
        public String description;
        public String iconText;
        public Drawable iconBackground;
        public String downloadUrl;
        public String downloadClickTarget;
        public String downloadFilename;
        public String downloadFiletype;
    }
}

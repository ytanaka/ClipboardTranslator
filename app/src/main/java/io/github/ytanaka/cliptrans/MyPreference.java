package io.github.ytanaka.cliptrans;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private static final String FILENAME = MyPreference.class.getCanonicalName();

    private final SharedPreferences pref;

    public MyPreference(Context context) {
        pref = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    private static final String KEY_IS_DISPLAY_DIC_BOTTOM = "isDisplayDicBottom";
    public boolean isDisplayDicBottom() {
        return pref.getBoolean(KEY_IS_DISPLAY_DIC_BOTTOM, true);
    }
    public void setDisplayDicBottom(boolean b) {
        pref.edit().putBoolean(KEY_IS_DISPLAY_DIC_BOTTOM, b).apply();
    }

    private static final String KEY_IS_SEARCH_FUZZY = "isSearchFuzzy";
    public boolean isSearchFuzzy() {
        return pref.getBoolean(KEY_IS_SEARCH_FUZZY, true);
    }
    public void setSearchFuzzy(boolean b) {
        pref.edit().putBoolean(KEY_IS_SEARCH_FUZZY, b).apply();
    }

    private static final String KEY_IS_DISPLAY_THUMBNAIL = "isDisplayThumbnail";
    public boolean isDisplayThumbnail() {
        return pref.getBoolean(KEY_IS_DISPLAY_THUMBNAIL, false);
    }
    public void setDisplayThumbnail(boolean b) {
        pref.edit().putBoolean(KEY_IS_DISPLAY_THUMBNAIL, b).apply();
    }
}

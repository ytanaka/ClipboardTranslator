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

    private static final String KEY_FILTER_WORD_NIHONGO = "isFilterWordNihongo";
    public boolean isFilterWordNihongo() {
        return pref.getBoolean(KEY_FILTER_WORD_NIHONGO, true);
    }
    public void setFilterWordNihongo(boolean b) {
        pref.edit().putBoolean(KEY_FILTER_WORD_NIHONGO, b).apply();
    }

    private static final String KEY_FILTER_WORD_NUM_SP = "isFilterWordNumSp";
    public boolean isFilterWordNumSp() {
        return pref.getBoolean(KEY_FILTER_WORD_NUM_SP, true);
    }
    public void setFilterWordNumSp(boolean b) {
        pref.edit().putBoolean(KEY_FILTER_WORD_NUM_SP, b).apply();
    }

    private static final String KEY_USE_GOOGLE_TRANSLATE = "isUseGoogleTranslate";
    public boolean isUseGoogleTranslate() {
        return pref.getBoolean(KEY_USE_GOOGLE_TRANSLATE, true);
    }
    public void setUseGoogleTranslate(boolean b) {
        pref.edit().putBoolean(KEY_USE_GOOGLE_TRANSLATE, b).apply();
    }

    private static final String KEY_FILTER_WORD_ALPHA_LOW2UP = "isFilterWordAlphaLow2up";
    public boolean isFilterWordAlphaLow2up() {
        return pref.getBoolean(KEY_FILTER_WORD_ALPHA_LOW2UP, true);
    }
    public void setFilterWordAlphaLow2up(boolean b) {
        pref.edit().putBoolean(KEY_FILTER_WORD_ALPHA_LOW2UP, b).apply();
    }


}

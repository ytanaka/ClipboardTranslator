package com.example.testapp.clipboardtranslator;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private static final String FILENAME = MyPreference.class.getCanonicalName();
    private static final String KEY_IS_DISPLAY_DIC_BOTTOM = "isDisplayDicBottom";

    private final SharedPreferences pref;

    public MyPreference(Context context) {
        pref = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public boolean isDisplayDicBottom() {
        return pref.getBoolean(KEY_IS_DISPLAY_DIC_BOTTOM, true);
    }
    public void setDisplayDicBottom(boolean b) {
        pref.edit().putBoolean(KEY_IS_DISPLAY_DIC_BOTTOM, b).apply();
    }
}

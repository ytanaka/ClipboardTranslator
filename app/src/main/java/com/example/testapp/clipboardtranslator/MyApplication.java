package com.example.testapp.clipboardtranslator;

import android.app.Application;
import android.content.Context;

import com.example.testapp.clipboardtranslator.db.DB;
import com.example.testapp.clipboardtranslator.service.ClipboardListenerService;

public class MyApplication extends Application {
    private DB db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DB(this);
        ClipboardListenerService.start(this);
    }

    public DB getDb() {
        return db;
    }
    public static MyApplication instance(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    public MyPreference getPref() {
        return new MyPreference(this);
    }
}

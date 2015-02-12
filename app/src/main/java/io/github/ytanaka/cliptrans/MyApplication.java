package io.github.ytanaka.cliptrans;

import android.app.Application;
import android.content.Context;

import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.service.ClipboardListenerService;

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

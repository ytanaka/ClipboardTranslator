package com.example.testapp.clipboardtranslator.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.testapp.clipboardtranslator.activity.DicActivity;
import com.example.testapp.clipboardtranslator.activity.MainActivity;
import com.example.testapp.clipboardtranslator.MyApplication;
import com.example.testapp.clipboardtranslator.R;

public class ClipboardListenerService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = ClipboardListenerService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ClipboardListenerService.class);
        intent.setAction("START");
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ClipboardListenerService.class);
        intent.setAction("STOP");
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        getClipboardManager().addPrimaryClipChangedListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);

        if (intent != null && intent.getAction().equals("STOP")) {
            stopSelf();
        } else {
            showNotification();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        getClipboardManager().removePrimaryClipChangedListener(this);
        stopForeground(true);
    }

    long lastCall = 0;

    /**
     * ClipboardManager からのコールバック
     */
    @Override
    public void onPrimaryClipChanged() {
        String s = getClipboardText();
        if (TextUtils.isEmpty(s)) return;

        long now = System.currentTimeMillis();
        if (lastCall + 1000 > now) return;
        lastCall = now;

        if (MyApplication.instance(this).getDb().find(s, 1).size() > 0) {
            startDic(s);
        } else {
            startTranslator(this, s);
        }
    }

    private void startDic(String s) {
        DicActivity.startActivity(this, s);
    }

    private ClipboardManager getClipboardManager() {
        return (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    private String getClipboardText() {
        ClipData clip = getClipboardManager().getPrimaryClip();
        for (int i = 0; i < clip.getItemCount(); i++) {
            CharSequence cs = clip.getItemAt(i).getText();
            if (TextUtils.isEmpty(cs)) continue;
            String s = cs.toString().trim();
            if (s.length() == 0) continue;
            return s;
        }
        return null;
    }

    public static void startTranslator(Context context, String s) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PackageManager pm = context.getPackageManager();
        for (ResolveInfo res: pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)) {
            if (!TextUtils.equals(res.activityInfo.applicationInfo.packageName, MainActivity.PACKAGE_NAME_GOOGLE_TRANSLATE)) continue;
            intent.setComponent(new ComponentName(res.activityInfo.applicationInfo.packageName, res.activityInfo.name));
            Log.v(TAG, "start google translate activity");
            context.startActivity(intent);
            return;
        }
        Toast.makeText(context, "Google翻訳が見つかりませんでした", Toast.LENGTH_LONG).show();
    }

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(0)
                .setLargeIcon(largeIcon)
                .setContentTitle("TITLE")
                .setContentText("TEXT")
                .setOngoing(true)
                .setContentIntent(pIntent)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT < 16) {
            startForeground(1, builder.getNotification());
        } else {
            startForeground(1, builder.build());
        }
    }
}

package com.example.testapp.clipboardtranslator.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.testapp.clipboardtranslator.R;
import com.example.testapp.clipboardtranslator.service.ClipboardListenerService;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PACKAGE_NAME_GOOGLE_TRANSLATE = "com.google.android.apps.translate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        PackageInfo pkg = null;
        try {
            pkg = getPackageManager().getPackageInfo(PACKAGE_NAME_GOOGLE_TRANSLATE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            /* */
        }
        ViewGroup googlePlay = (ViewGroup) findViewById(R.id.layout_google_play);
        googlePlay.setVisibility(pkg != null ? View.GONE : View.VISIBLE);
    }

    public void onButtonClicked_start(View v) {
        ClipboardListenerService.start(this);
    }

    public void onButtonClicked_stop(View v) {
        ClipboardListenerService.stop(this);
    }

    public void onButtonClicked_googlePlay(View v) {
        Uri uri = Uri.parse("market://details?id=" + PACKAGE_NAME_GOOGLE_TRANSLATE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onButtonClicked_setting(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void onButtonClicked_about(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}

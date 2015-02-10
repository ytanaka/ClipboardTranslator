package com.example.testapp.clipboardtranslator.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.testapp.clipboardtranslator.MyApplication;
import com.example.testapp.clipboardtranslator.R;
import com.example.testapp.clipboardtranslator.db.DB;
import com.example.testapp.clipboardtranslator.service.ClipboardListenerService;

public class DicActivity extends Activity {
    private static final String TAG = DicActivity.class.getSimpleName();
    private static String sWord = null;

    public static void startActivity(Context context, String word) {
        Intent intent = new Intent(context, DicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sWord = word;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic);

        // タスクのヒストリーから画面が復活しないようにする
        if (sWord == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        final String word = sWord;
        sWord = null;
        boolean isBottom = MyApplication.instance(this).getPref().isDisplayDicBottom();

        View layoutTop = findViewById(R.id.layout_blank_top);
        View layoutBottom = findViewById(R.id.layout_blank_bottom);
        layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layoutTop.setVisibility(isBottom ? View.VISIBLE : View.GONE);
        layoutBottom.setVisibility(!isBottom ? View.VISIBLE : View.GONE);

        TextView tvTitle = (TextView) findViewById(R.id.textView_title);
        tvTitle.setText(word);
        findViewById(R.id.button_translate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardListenerService.startTranslator(DicActivity.this, word);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (DB.Result i: MyApplication.instance(this).getDb().find(word, 200)) {
            sb.append("<b>").append(i.word).append("</b><br>");
            for (String s: i.desc.split("\n")) {
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(s).append("<br>");
            }
        }
        TextView tv = (TextView) findViewById(R.id.textView_dic);
        tv.setText(Html.fromHtml(sb.toString()));
        tv.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

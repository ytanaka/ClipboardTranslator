package com.example.testapp.clipboardtranslator.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testapp.clipboardtranslator.MyApplication;
import com.example.testapp.clipboardtranslator.R;
import com.example.testapp.clipboardtranslator.db.DB;
import com.example.testapp.clipboardtranslator.service.ClipboardListenerService;

import java.util.ArrayList;
import java.util.List;

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

        List<Item> items = new ArrayList<>();
        for (DB.Result i: MyApplication.instance(this).getDb().find(word, 200)) {
            items.add(new Item(i.word + (i.type == DB.TYPE_HAND ? " (H)" : " (G)"), null));
            for (String s: i.desc.split("\n")) {
                items.add(new Item(null, s));
            }
        }
        MyAdapter adapter = new MyAdapter(this, items);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    static class Item {
        String title;
        String content;
        Item(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
    class MyAdapter extends ArrayAdapter<Item> {
        public MyAdapter(Context context, List<Item> items) {
            super(context, R.layout.list_item_dic, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_dic, null);
            }
            Tag tag = (Tag) convertView.getTag();
            if (tag == null) {
                tag = new Tag();
                tag.tvTitle = (TextView) convertView.findViewById(R.id.textView_title);
                tag.tvContent = (TextView) convertView.findViewById(R.id.textView_content);
            }
            Item i = getItem(position);
            tag.tvTitle.setVisibility(i.title != null ? View.VISIBLE : View.GONE);
            tag.tvContent.setVisibility(i.content != null ? View.VISIBLE : View.GONE);
            tag.tvTitle.setText(i.title);
            tag.tvContent.setText(i.content);
            return convertView;
        }

        class Tag {
            TextView tvTitle;
            TextView tvContent;
        }
    }
}

package com.example.testapp.clipboardtranslator.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
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
        layoutTop.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 closeActivity();
                 return true;
             }
         });
        layoutBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeActivity();
                return false;
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
                closeActivity();
            }
        });

        List<Item> items = new ArrayList<>();
        for (DB.Result i: MyApplication.instance(this).getDb().find(word, 200)) {
            items.add(new Item(i.word, i.type, null));
            for (String s: i.desc.split("\n")) {
                items.add(new Item(null, -1, s));
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
        closeActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(0, 0);
    }

    static class Item {
        String title;
        int type;
        String content;
        Item(String title, int type, String content) {
            this.title = title;
            this.type = type;
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
                tag.layoutTitle = convertView.findViewById(R.id.layout_title);
                tag.tvTitle = (TextView) convertView.findViewById(R.id.textView_title);
                tag.tvType = (TextView) convertView.findViewById(R.id.textView_dic_type);
                tag.tvContent = (TextView) convertView.findViewById(R.id.textView_content);
            }
            Item i = getItem(position);
            tag.layoutTitle.setVisibility(i.title != null ? View.VISIBLE : View.GONE);
            tag.tvContent.setVisibility(i.content != null ? View.VISIBLE : View.GONE);
            tag.tvTitle.setText(i.title);
            tag.tvType.setText(getDicTypeString(i.type));
            tag.tvType.setBackgroundResource(getDicTypeBg(i.type));
            tag.tvContent.setText(i.content);
            return convertView;
        }
        private String getDicTypeString(int type) {
            switch (type) {
                case DB.TYPE_HAND: return getResources().getString(R.string.dic_type_hand);
                case DB.TYPE_GENE95: return getResources().getString(R.string.dic_type_gene95);
                default: return "???";
            }
        }
        private int getDicTypeBg(int type) {
            switch (type) {
                case DB.TYPE_HAND: return R.drawable.dic_icon_hand;
                case DB.TYPE_GENE95: return R.drawable.dic_icon_gene95;
                default: return 0;
            }
        }

        class Tag {
            View layoutTitle;
            TextView tvTitle;
            TextView tvType;
            TextView tvContent;
        }
    }
}

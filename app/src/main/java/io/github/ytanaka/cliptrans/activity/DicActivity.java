package io.github.ytanaka.cliptrans.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.ytanaka.cliptrans.Logic;
import io.github.ytanaka.cliptrans.MyApplication;
import io.github.ytanaka.cliptrans.MyPreference;
import io.github.ytanaka.cliptrans.R;
import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.dic.Dic;
import io.github.ytanaka.cliptrans.dic.FuzzyWordEnglish;
import io.github.ytanaka.cliptrans.service.ClipboardListenerService;
import io.github.ytanaka.cliptrans.util.Util;

public class DicActivity extends Activity {
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
        MyPreference pref = MyApplication.instance(this).getPref();
        if (!pref.isDisplayThumbnail()) getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_dic);

        // タスクのヒストリーから画面が復活しないようにする
        if (sWord == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        final String word = sWord;
        sWord = null;

        // 余白設定
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
        layoutTop.setVisibility(pref.isDisplayDicBottom() ? View.VISIBLE : View.GONE);
        layoutBottom.setVisibility(!pref.isDisplayDicBottom() ? View.VISIBLE : View.GONE);

        // 訳表示
        DB db = MyApplication.instance(this).getDb();
        String word2 = null;
        List<DB.Result> resultList = db.find(word, 200);
        if (resultList.size() == 0) {
            word2 = Logic.fuzzyMatchInDic(this, word);
            if (word2 != null) resultList = db.find(word2, 200);
        }
        List<Item> items = new ArrayList<>();
        for (DB.Result i: resultList) {
            items.add(new Item(i.type, i.word, i.desc));
        }
        MyAdapter adapter = new MyAdapter(this, items);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);

        // タイトル部
        TextView tvTitle = (TextView) findViewById(R.id.textView_title);
        tvTitle.setText(word2 == null ? word : word + " (" + word2 + ")");
        findViewById(R.id.button_translate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardListenerService.startTranslator(DicActivity.this, word);
                closeActivity();
            }
        });
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
        String type;
        String title;
        String content;
        Item(String type, String title, String content) {
            this.type = type;
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
                convertView = getLayoutInflater().inflate(R.layout.list_item_dic, parent, false);
            }
            Tag tag = (Tag) convertView.getTag();
            if (tag == null) {
                tag = new Tag();
                tag.tvTitle = (TextView) convertView.findViewById(R.id.textView_title);
                tag.tvType = (TextView) convertView.findViewById(R.id.textView_dic_type);
                tag.tvContent = (TextView) convertView.findViewById(R.id.textView_content);
            }
            Item i = getItem(position);
            Dic.Info dic = getDic(i.type).getInfo();
            tag.tvTitle.setText(i.title);
            tag.tvType.setText(dic.iconText);
            Util.setBackground(tag.tvType, dic.iconBackground);
            tag.tvContent.setText(i.content);
            return convertView;
        }

        class Tag {
            TextView tvTitle;
            TextView tvType;
            TextView tvContent;
        }
    }

    private Dic getDic(String type) {
        for (Dic dic : Dic.LIST) {
            if (TextUtils.equals(dic.getId(), type)) return dic;
        }
        throw new RuntimeException(type);
    }
}

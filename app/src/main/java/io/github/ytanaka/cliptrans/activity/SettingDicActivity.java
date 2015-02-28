package io.github.ytanaka.cliptrans.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.ytanaka.cliptrans.MyApplication;
import io.github.ytanaka.cliptrans.R;
import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.dic.Dic;
import io.github.ytanaka.cliptrans.util.ProgressAsyncTask;
import io.github.ytanaka.cliptrans.util.Util;

public class SettingDicActivity extends Activity {
    public static final String TAG = SettingDicActivity.class.getSimpleName();

    private DB mDb;
    private List<View> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_dic);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        mDb = MyApplication.instance(this).getDb();

        // 辞書取り込み
        LinearLayout dicList = (LinearLayout) findViewById(R.id.layout_dic_list);
        for (int i = 0; i < Dic.LIST.length; i++) {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            View v = createListItem(dicList, i);
            dicList.addView(v, p);
            list.add(v);
        }
        refresh();
    }

    private View createListItem(LinearLayout parent, final int i) {
        final Dic dic = Dic.LIST[i];
        final Dic.Info info = dic.getInfo();

        View dicView = getLayoutInflater().inflate(R.layout.list_item_setting, parent, false);
        Holder holder = new Holder();
        dicView.setTag(holder);

        TextView tvDescription = (TextView) dicView.findViewById(R.id.textView_description);
        TextView tvIcon = (TextView) dicView.findViewById(R.id.textView_icon);
        holder.tvWordCount = (TextView) dicView.findViewById(R.id.textView_word_count);
        holder.btDownload = (Button) dicView.findViewById(R.id.button_download);
        holder.btImport = (Button) dicView.findViewById(R.id.button_import);
        holder.btDelete = (Button) dicView.findViewById(R.id.button_delete);

        tvDescription.setText(info.description);
        tvIcon.setText(info.iconText);
        Util.setBackground(tvIcon, info.iconBackground);
        holder.btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Util.showOKCancelMsgBox(SettingDicActivity.this, getString(R.string.explanation_to_user_how_to_download, info.downloadClickTarget), new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });
        holder.btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(info.downloadFiletype);
                if (getPackageManager().resolveActivity(intent, 0) == null) {
                    Util.showOKCancelMsgBox(SettingDicActivity.this, getString(R.string.confirm_search_file_browser_in_google_play), new Runnable() {
                        @Override
                        public void run() {
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q=filer"));
                            startActivity(goToMarket);
                        }
                    });
                    return;
                }
                Util.showOKCancelMsgBox(SettingDicActivity.this, getString(R.string.explanation_to_user_how_to_import, info.downloadFilename), new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(intent, 100 + i);
                    }
                });
            }
        });
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showOKCancelMsgBox(SettingDicActivity.this, getString(R.string.confirm_delete_dictionary_data), new Runnable() {
                    @Override
                    public void run() {
                        mDb.remove(dic.getId());
                        refresh();
                    }
                });
            }
        });
        return dicView;
    }

    private void refresh() {
        for (int i = 0; i < Dic.LIST.length; i++) {
            Holder holder = (Holder) list.get(i).getTag();
            int wordCount = mDb.count(Dic.LIST[i].getId());
            holder.tvWordCount.setText(getString(R.string.__words_imported, wordCount));
            holder.btDownload.setVisibility(wordCount == 0 ? View.VISIBLE : View.GONE);
            holder.btImport.setVisibility(wordCount == 0 ? View.VISIBLE : View.GONE);
            holder.btDelete.setVisibility(wordCount > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private static class Holder {
        TextView tvWordCount;
        Button btDownload;
        Button btImport;
        Button btDelete;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.v(TAG, "onActivityResult(" + data + ")");
        if (resultCode != RESULT_OK) return;
        for (int i = 0; i < Dic.LIST.length; i++) {
            if (requestCode != 100 + i) continue;
            final Dic dic = Dic.LIST[i];
            new ProgressAsyncTask(SettingDicActivity.this, getString(R.string.importing_dictionary)) {
                int count = 0;
                @Override
                protected void run() {
                    count = dic.extractAndInsertToDb(SettingDicActivity.this, data.getData(), mDb, notifier);
                }
                @Override
                protected void finished() {
                    if (count == 0) {
                        Toast.makeText(SettingDicActivity.this, getString(R.string.failed_to_import_dictionary), Toast.LENGTH_LONG).show();
                    } else {
                        Util.showMsgBox(SettingDicActivity.this, getString(R.string.notify_finished_import_dictionary));
                    }
                    refresh();
                }
            };
        }
    }
}

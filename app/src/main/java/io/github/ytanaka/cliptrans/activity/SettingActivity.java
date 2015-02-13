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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.github.ytanaka.cliptrans.MyApplication;
import io.github.ytanaka.cliptrans.MyPreference;
import io.github.ytanaka.cliptrans.R;
import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.dic.DicFileGene95;
import io.github.ytanaka.cliptrans.dic.DicFileHand;
import io.github.ytanaka.cliptrans.util.ProgressAsyncTask;
import io.github.ytanaka.cliptrans.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {
    public static final String TAG = SettingActivity.class.getSimpleName();

    private DB mDb;
    private MyPreference mPref;
    private List<DicItem> dicItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        mDb = MyApplication.instance(this).getDb();
        mPref = MyApplication.instance(this).getPref();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_display);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mPref.setDisplayDicBottom(checkedId == R.id.radioButton_display_bottom);
            }
        });
        radioGroup.check(mPref.isDisplayDicBottom() ? R.id.radioButton_display_bottom : R.id.radioButton_display_top);

        dicItems.add(new DicItem(DB.TYPE_HAND, 101,
                R.id.button_download1, R.id.button_extract1, R.id.button_delete1, R.id.textView_dic_count1,
                "http://kujirahand.com/web-tools/EJDictFreeDL.php", "辞書データ(テキスト形式)",
                "ejdic-hand-txt.zip", "application/zip"));
        dicItems.add(new DicItem(DB.TYPE_GENE95, 102,
                R.id.button_download2, R.id.button_extract2, R.id.button_delete2, R.id.textView_dic_count2,
                "http://www.namazu.org/~tsuchiya/sdic/data/gene.html", "gene95.tar.gz (tar+gzip圧縮形式)",
                "gene95.tar.???", "application/*"));
        refresh();
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

    class DicItem {
        int fileGetRequestCode;
        int dicType;
        Button buttonDownload;
        Button buttonExtract;
        Button buttonDelete;
        TextView textViewWordCount;
        String url;
        String clickTargetText;
        String filename;
        String fileType;

        public DicItem(int dicType, int fileGetRequestCode, int buttonDownload, int buttonExtract, int buttonDelete, int textViewWordCount, String url, String clickTargetText, String filename, String fileType) {
            this.dicType = dicType;
            this.fileGetRequestCode = fileGetRequestCode;
            this.buttonDownload = (Button) findViewById(buttonDownload);
            this.buttonExtract  = (Button) findViewById(buttonExtract);
            this.buttonDelete   = (Button) findViewById(buttonDelete);
            this.textViewWordCount = (TextView) findViewById(textViewWordCount);
            this.url = url;
            this.clickTargetText = clickTargetText;
            this.filename = filename;
            this.fileType = fileType;
            setListener();
        }
        private void setListener() {
            buttonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Util.showOKCancelMsgBox(SettingActivity.this, getString(R.string.explanation_to_user_how_to_download, clickTargetText), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    });
                }
            });
            buttonExtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(fileType);
                    if (getPackageManager().resolveActivity(intent, 0) == null) {
                        Util.showOKCancelMsgBox(SettingActivity.this, getString(R.string.confirm_search_file_browser_in_google_play), new Runnable() {
                            @Override
                            public void run() {
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q=filer"));
                                startActivity(goToMarket);
                            }
                        });
                        return;
                    }
                    Util.showOKCancelMsgBox(SettingActivity.this, getString(R.string.explanation_to_user_how_to_import, filename), new Runnable() {
                        @Override
                        public void run() {
                            startActivityForResult(intent, fileGetRequestCode);
                        }
                    });
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showOKCancelMsgBox(SettingActivity.this, getString(R.string.confirm_delete_dictionary_data), new Runnable() {
                        @Override
                        public void run() {
                            mDb.remove(dicType);
                            refresh();
                        }
                    });
                }
            });
        }
        // ボタンなどの画面部品を更新
        public void refreshDisplay() {
            int wordCount = mDb.count(dicType);
            buttonDownload.setVisibility(wordCount == 0 ? View.VISIBLE : View.GONE);
            buttonExtract.setVisibility(wordCount == 0 ? View.VISIBLE : View.GONE);
            buttonDelete.setVisibility(wordCount > 0 ? View.VISIBLE : View.GONE);
            textViewWordCount.setText(getString(R.string.__words_imported, wordCount));
        }
        // ファイル選択が成功したら、ファイルをインポートする
        public void onActivityResult(int requestCode, int resultCode, final Intent data) {
            if (requestCode != fileGetRequestCode) return;
            if (resultCode != RESULT_OK) return;
            new ProgressAsyncTask(SettingActivity.this, getString(R.string.importing_dictionary)) {
                int count = 0;
                @Override
                protected void run() {
                    if (dicType == DB.TYPE_HAND) {
                        count = DicFileHand.extractAndInsertToDb(SettingActivity.this, data.getData(), notifier);
                    } else if (dicType == DB.TYPE_GENE95) {
                        count = DicFileGene95.extractAndInsertToDb(SettingActivity.this, data.getData(), notifier);
                    }
                }
                @Override
                protected void finished() {
                    if (count == 0) {
                        Toast.makeText(SettingActivity.this, getString(R.string.failed_to_import_dictionary), Toast.LENGTH_LONG).show();
                    } else {
                        Util.showMsgBox(SettingActivity.this, getString(R.string.notify_finished_import_dictionary));
                    }
                    refresh();
                }
            };
        }

    }

    private void refresh() {
        for (DicItem di : dicItems) di.refreshDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.v(TAG, "onActivityResult(" + data + ")");
        for (DicItem di : dicItems) di.onActivityResult(requestCode, resultCode, data);
    }
}

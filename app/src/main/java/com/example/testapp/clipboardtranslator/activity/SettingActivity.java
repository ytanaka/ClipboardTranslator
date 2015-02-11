package com.example.testapp.clipboardtranslator.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.clipboardtranslator.MyApplication;
import com.example.testapp.clipboardtranslator.MyPreference;
import com.example.testapp.clipboardtranslator.R;
import com.example.testapp.clipboardtranslator.db.DB;
import com.example.testapp.clipboardtranslator.db.DicFileGene95;
import com.example.testapp.clipboardtranslator.db.DicFileHand;
import com.example.testapp.clipboardtranslator.util.ProgressAsyncTask;
import com.example.testapp.clipboardtranslator.util.Util;

public class SettingActivity extends Activity {
    public static final String TAG = SettingActivity.class.getSimpleName();

    private DB mDb;
    private MyPreference mPref;

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

    private void refresh() {
        int wordCount1 = mDb.count(DB.TYPE_HAND);
        int wordCount2 = mDb.count(DB.TYPE_GENE95);
        findViewById(R.id.button_download1).setVisibility(wordCount1 == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_extract1).setVisibility(wordCount1 == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_delete1).setVisibility(wordCount1 > 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_download2).setVisibility(wordCount2 == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_extract2).setVisibility(wordCount2 == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_delete2).setVisibility(wordCount2 > 0 ? View.VISIBLE : View.GONE);
        TextView tvCount1 = (TextView) findViewById(R.id.textView_dic_count1);
        TextView tvCount2 = (TextView) findViewById(R.id.textView_dic_count2);
        tvCount1.setText(getString(R.string.__words_imported, wordCount1));
        tvCount2.setText(getString(R.string.__words_imported, wordCount2));
    }

    public void onButtonClicked_download1(View v) {
        String url = "http://kujirahand.com/web-tools/EJDictFreeDL.php";
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Util.showMsg(this, getString(R.string.explanation_to_user_how_to_download, "辞書データ(テキスト形式)"), new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        });
    }

    public void onButtonClicked_extract1(View v) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        if (getPackageManager().resolveActivity(intent, 0) == null) {
            Util.showMsg(this, getString(R.string.confirm_search_file_browser_in_google_play), new Runnable() {
                @Override
                public void run() {
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q=filer"));
                    startActivity(goToMarket);
                }
            });
            return;
        }

        Util.showMsg(this, getString(R.string.explanation_to_user_how_to_import, "ejdic-hand-txt.zip"), new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, 101);
            }
        });
    }

    public void onButtonClicked_delete1(View v) {
        Util.showMsg(this, getString(R.string.confirm_delete_dictionary_data), new Runnable() {
            @Override
            public void run() {
                mDb.remove(DB.TYPE_HAND);
                refresh();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.v(TAG, "" + data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            new ProgressAsyncTask(this, getString(R.string.importing_dictionary)) {
                int count = 0;
                @Override
                protected void run() {
                    count = DicFileHand.extractAndInsertToDb(SettingActivity.this, data.getData(), notifier);
                }
                @Override
                protected void finished() {
                    if (count == 0) Toast.makeText(SettingActivity.this, getString(R.string.failed_to_import_dictionary), Toast.LENGTH_LONG).show();
                    refresh();
                }
            };
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            new ProgressAsyncTask(this, "辞書データ登録中") {
                int count = 0;
                @Override
                protected void run() {
                    count = DicFileGene95.extractAndInsertToDb(SettingActivity.this, data.getData(), notifier);
                }
                @Override
                protected void finished() {
                    if (count == 0) Toast.makeText(SettingActivity.this, "辞書登録失敗", Toast.LENGTH_LONG).show();
                    refresh();
                }
            };
        }
    }

    public void onButtonClicked_download2(View v) {
        String url = "http://www.namazu.org/~tsuchiya/sdic/data/gene.html";
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Util.showMsg(this, "これからブラウザを開きます。\n開いたブラウザ画面で、「gene95.tar.gz (tar+gzip圧縮形式)」をタップしてファイルをダウンロードしてください。\nダウンロードが終わったら、この画面に戻ってください。\n※）パソコンでダウンロードしたファイルをAndroidに転送してもかまいません。", new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        });
    }

    public void onButtonClicked_extract2(View v) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        if (getPackageManager().resolveActivity(intent, 0) == null) {
            Util.showMsg(this, "ファイル選択アプリが見つかりませんでした。Google Play で探しますか？", new Runnable() {
                @Override
                public void run() {
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q=filer"));
                    startActivity(goToMarket);
                }
            });
            return;
        }

        Util.showMsg(this, "これからファイル選択画面を開きます。\nAndroidのダウンロードディレクトリにある gene95.tar.gz を選択してください。\n※）端末内部のファイルを選択できない場合は、GooglePlayで \"ファイラー\"で検索してアプリをインストールしてください。", new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, 102);
            }
        });
    }

    public void onButtonClicked_delete2(View v) {
        Util.showMsg(this, "アプリに取り込んだ辞書ファイルを削除します。よろしいですか？", new Runnable() {
            @Override
            public void run() {
                mDb.remove(DB.TYPE_GENE95);
                refresh();
            }
        });
    }
}

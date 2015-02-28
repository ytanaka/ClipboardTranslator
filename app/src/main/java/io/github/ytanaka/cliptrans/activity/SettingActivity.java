package io.github.ytanaka.cliptrans.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import io.github.ytanaka.cliptrans.MyApplication;
import io.github.ytanaka.cliptrans.MyPreference;
import io.github.ytanaka.cliptrans.R;

public class SettingActivity extends Activity {
    private MyPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        mPref = MyApplication.instance(this).getPref();

        // 表示位置
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_display);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mPref.setDisplayDicBottom(checkedId == R.id.radioButton_display_bottom);
            }
        });
        radioGroup.check(mPref.isDisplayDicBottom() ? R.id.radioButton_display_bottom : R.id.radioButton_display_top);

        // あいまい検索
        final CheckBox checkBoxFuzzySearch = (CheckBox) findViewById(R.id.checkBox_search_fuzzy);
        checkBoxFuzzySearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setSearchFuzzy(checkBoxFuzzySearch.isChecked());
            }
        });
        checkBoxFuzzySearch.setChecked(mPref.isSearchFuzzy());

        // サムネイル表示
        final CheckBox checkBoxShowThumbnail = (CheckBox) findViewById(R.id.checkBox_display_thumbnail);
        checkBoxShowThumbnail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setDisplayThumbnail(checkBoxShowThumbnail.isChecked());
            }
        });
        checkBoxShowThumbnail.setChecked(mPref.isDisplayThumbnail());

        // フィルタリング
        final CheckBox checkBobFilterNihongo = (CheckBox) findViewById(R.id.checkBox_filter_nihongo);
        checkBobFilterNihongo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setFilterWordNihongo(checkBobFilterNihongo.isChecked());
            }
        });
        checkBobFilterNihongo.setChecked(mPref.isFilterWordNihongo());
        final CheckBox checkBobFilterNumSp = (CheckBox) findViewById(R.id.checkBox_filter_num_sp);
        checkBobFilterNumSp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setFilterWordNumSp(checkBobFilterNumSp.isChecked());
            }
        });
        checkBobFilterNumSp.setChecked(mPref.isFilterWordNumSp());
        final CheckBox checkBobFilterAlphaLow2Up = (CheckBox) findViewById(R.id.checkBox_filter_alpha_low2up);
        checkBobFilterAlphaLow2Up.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setFilterWordAlphaLow2up(checkBobFilterAlphaLow2Up.isChecked());
            }
        });
        checkBobFilterAlphaLow2Up.setChecked(mPref.isFilterWordAlphaLow2up());
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
}

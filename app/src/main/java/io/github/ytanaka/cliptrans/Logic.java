package io.github.ytanaka.cliptrans;

import android.content.Context;

import java.util.regex.Pattern;

import io.github.ytanaka.cliptrans.db.DB;
import io.github.ytanaka.cliptrans.dic.FuzzyWordEnglish;

public class Logic {
    /**
     * あいまい検索で辞書に単語が存在するかどうか検索する
     * @param word 検索対象
     * @return 辞書の中で見つかった単語 (見つからなかったら null)
     */
    public static String fuzzyMatchInDic(Context context, String word) {
        DB db = MyApplication.instance(context).getDb();
        DB.Result r = db.find1(word);
        if (r != null) return word;
        if (!MyApplication.instance(context).getPref().isSearchFuzzy()) return null;
        for (String s : new FuzzyWordEnglish().normalize(word)) {
            r = db.find1(s);
            if (r != null) return s;
        }
        return null;
    }

    /**
     * フィルタリングして無視するかどうかを判定する
     */
    public static boolean isFilteredWord(Context context, String s) {
        MyPreference pref = MyApplication.instance(context).getPref();

        if (pref.isFilterWordNihongo()) {
            if (regexNihongo.matcher(s).find()) return true;
        }
        if (pref.isFilterWordNumSp()) {
            if (!regexSp.matcher(s).find() && regexNumSp.matcher(s).find()) return true;
        }
        if (pref.isFilterWordAlphaLow2up()) {
            if (!regexSp.matcher(s).find() && regexNumAlphaLow2Up.matcher(s).find()) return true;
        }
        return false;
    }
    private static Pattern regexSp = Pattern.compile("\\s");
    private static Pattern regexNihongo = Pattern.compile("\\p{InHiragana}|\\p{InKatakana}|\\p{InCjkUnifiedIdeographs}");
    private static Pattern regexNumSp = Pattern.compile("\\p{Digit}|[!\"#$%&()*+,/:;<=>?@\\[\\\\\\]^_`{|}~']"); // .-' を除く
    private static Pattern regexNumAlphaLow2Up = Pattern.compile("[a-z][A-Z]");
}

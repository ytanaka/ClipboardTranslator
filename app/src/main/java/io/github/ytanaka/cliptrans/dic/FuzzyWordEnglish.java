package io.github.ytanaka.cliptrans.dic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FuzzyWordEnglish {
    static class Normalize implements Comparable<Normalize> {
        final String regex;
        final String str;
        Normalize(String regex, String str) {
            this.regex = regex + "$";
            this.str = str;
        }
        public String normalize(String s) {
            return Pattern.compile(regex).matcher(s).replaceFirst(str);
        }

        @Override
        public int compareTo(@NonNull Normalize another) {
            return this.regex.length() - another.regex.length();
        }
    }

    static private final List<Normalize> list = new ArrayList<>();
    static private void n(String s1, String s2) {
        list.add(new Normalize(s1, s2));
    }
    static {
        // http://eigogakusyu-web.com/grammar/050/
        // 複数形
        n("s", "");                // book => books, cap => caps, chief => chiefs, hat => hats, month => months
        n("es", "");               // brush => brushes, bus => buses, church => churches, lens => lenses
        n("([^aeiou])ies", "$1y"); // baby => babies, city => cities, country => countries, enemy => enemies, lady => ladies
        n("ves", "fe");            // half => halves, knife => knives, leaf => leaves, life => lives, thief => thieves
        n("ves", "f");

        // http://eigogakusyu-web.com/grammar/009/
        // 過去形
        n("ed", "");                           // walk => walked, finish => finished, limit => limited, offer => offered
        n("ed", "e");                          // agree => agreed, love => loved
        n("([^aeiou])ied", "$1y");             // cry => cried, dry => dried
        n("([aeiou])yed", "$1y");              // enjoy => enjoyed, stay => stayed
        n("([aeiou])([^aeiou])\\2ed", "$1$2"); // stop => stopped, beg => begged
        n("([aeiou])red", "$1r");              // occur => occurred
        n("cked", "c");                        // picnic => picnicked, mimic => mimicked

        // 三単現
        n("([aeiou])os", "$1o");   // woo => woos
        n("([aeiou])ys", "$1y");   // play => plays
        n("ses", "s");             // miss => misses
        n("ches", "ch");           // teach => teaches
        n("shes", "sh");           // push => pushes
        n("xes", "x");             // fix => fixes
        n("zes", "z");             // buzz => buzzes
        n("([^aeiou])oes", "$1o"); // go => goes, do => does
        n("([^aeiou])ies", "$1y"); // carry => carries, study => studies
        n("s", "");                // think => thinks, bend => bends, choose => chooses

        // ing
        n("ing", "e");                          // come => coming, take => taking
        n("ing", "");                           // agree => agreeing, be => being, see => seeing
        n("ying", "ie");                        // die => dying, lie => lying
        n("([aeiou])([^aeiou])\\2ing", "$1$2"); // sit => sitting, begin => beginning, occur => occurring, visit => visiting, offer => offering
        n("cking", "c");                        // picnic => picnicking, mimic => mimicking

        // 長いものから先にチェックする
        Collections.sort(list);
        Collections.reverse(list);
    }

    public List<String> normalize(String word) {
        List<String> ret = new ArrayList<>();
        if (word.indexOf(' ') >= 0) return ret;
        for (Normalize n : list) {
            String s = n.normalize(word);
            if (s.length() >= 2 && !s.equals(word)) ret.add(s);
        }
        return ret;
    }
}

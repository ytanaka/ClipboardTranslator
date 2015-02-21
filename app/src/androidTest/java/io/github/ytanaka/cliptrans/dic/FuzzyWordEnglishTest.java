package io.github.ytanaka.cliptrans.dic;

import junit.framework.TestCase;

import java.util.List;

public class FuzzyWordEnglishTest extends TestCase {
    FuzzyWordEnglish f = new FuzzyWordEnglish();

    private void t(String n, String x) {
        List<String> list = f.normalize(x);
        assertTrue(x + " => " + list, list.contains(n));
    }

    public void testNormalizeAll() throws Exception {
        // 複数形
        t("book", "books");
        t("cap", "caps");
        t("chief", "chiefs");
        t("hat", "hats");
        t("month", "months");
        t("brush", "brushes");
        t("bus", "buses");
        t("church", "churches");
        t("lens", "lenses");
        t("baby", "babies");
        t("city", "cities");
        t("country", "countries");
        t("enemy", "enemies");
        t("lady", "ladies");
        t("half", "halves");
        t("knife", "knives");
        t("leaf", "leaves");
        t("life", "lives");
        t("thief", "thieves");

        // 過去形"
        t("walk", "walked");
        t("finish", "finished");
        t("limit", "limited");
        t("offer", "offered");
        t("agree", "agreed");
        t("love", "loved");
        t("cry", "cried");
        t("dry", "dried");
        t("enjoy", "enjoyed");
        t("stay", "stayed");
        t("stop", "stopped");
        t("beg", "begged");
        t("occur", "occurred");
        t("picnic", "picnicked");
        t("mimic", "mimicked");

        // 三単現
        t("woo", "woos");
        t("play", "plays");
        t("miss", "misses");
        t("teach", "teaches");
        t("push", "pushes");
        t("fix", "fixes");
        t("buzz", "buzzes");
        t("go", "goes");
        t("do", "does");
        t("carry", "carries");
        t("study", "studies");
        t("think", "thinks");
        t("bend", "bends");
        t("choose", "chooses");

        // ing
        t("come", "coming");
        t("take", "taking");
        t("agree", "agreeing");
        t("be", "being");
        t("see", "seeing");
        t("die", "dying");
        t("lie", "lying");
        t("sit", "sitting");
        t("begin", "beginning");
        t("occur", "occurring");
        t("visit", "visiting");
        t("offer", "offering");
        t("picnic", "picnicking");
        t("mimic", "mimicking");
    }

    public void testNormalize() throws Exception {
        assertEquals(f.normalize("a a").size(), 0);
        assertEquals(f.normalize("aaingaa").size(), 0);
        assertEquals(f.normalize("aaedaa").size(), 0);
        assertEquals(f.normalize("aaesaa").size(), 0);
        assertEquals(f.normalize("aasaa").size(), 0);
    }
}

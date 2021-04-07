package com.yuukaze.i18next.util;

import com.intellij.openapi.util.Pair;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationKeyRemovalTest implements Predicate<Pair<String, String>> {
    private final String searchQuery;
    private final boolean exact;
    private final Pattern enPattern = Pattern.compile("^en:(.*?)$");
    protected final boolean reverse;

    public TranslationKeyRemovalTest(String searchQuery) {
        this(searchQuery, false);
    }

    public TranslationKeyRemovalTest(String searchQuery, boolean exact) {
        this(searchQuery, exact, false);
    }

    public TranslationKeyRemovalTest(String searchQuery, boolean exact, boolean reverse) {
        this.searchQuery = searchQuery;
        this.exact = exact;
        this.reverse = reverse;
    }

    @Override
    public boolean test(Pair<String, String> s) {
        String key = s.first,
                en = s.second;
        Matcher m = enPattern.matcher(searchQuery);
        if (!m.find()) return this.reverse ^ this.falsyCompare(key, searchQuery);
        return en != null && this.reverse ^ this.falsyCompare(en, m.group(1));
    }

    private boolean falsyCompare(String str, String pat) {
        if (exact) return !str.equals(pat);
        return !str.startsWith(pat);
    }
}

package com.yuukaze.i18next.util;

public class TranslationKeyTest extends TranslationKeyRemovalTest {
    public TranslationKeyTest(String searchQuery) {
        super(searchQuery, false, true);
    }

    public TranslationKeyTest(String searchQuery, boolean exact) {
        super(searchQuery, exact, true);
    }
}

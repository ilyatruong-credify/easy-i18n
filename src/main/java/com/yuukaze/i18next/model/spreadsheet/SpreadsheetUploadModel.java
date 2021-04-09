package com.yuukaze.i18next.model.spreadsheet;

import com.intellij.util.containers.ImmutableList;
import com.yuukaze.i18next.model.LocalizedNode;
import com.yuukaze.i18next.model.Translations;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;

public class SpreadsheetUploadModel extends AbstractList<List<Object>> {
    private final Translations translations;
    private final List<String> locales;

    public SpreadsheetUploadModel(Translations translations) {
        this.translations = translations;
        locales = translations.getLocales();
    }

    @Override
    public List<Object> get(int index) {
        if (index == 0) {
            List<Object> keys = new ArrayList<>();
            keys.add("Key");
            keys.addAll(locales);
            return keys;
        }
        LocalizedNode node = (LocalizedNode) translations.getNodes().getChildren().toArray()[index - 1];
        return new ImmutableList<Object>() {
            @Override
            public int size() {
                return locales.size() + 1;
            }

            @Override
            public Object get(int index) {
                if (index == 0) return node.getKey();
                return firstNonNull(node.getValue().get(locales.get(index - 1)), "");
            }
        };
    }

    @Override
    public int size() {
        return translations.getNodes().getChildren().size();
    }
}

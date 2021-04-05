package com.yuukaze.i18next.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents update request to create a new translation.
 * @author marhali
 */
public class TranslationCreate extends TranslationUpdate {
    public TranslationCreate(@NotNull KeyedTranslation translation) {
        super(null, translation);
    }
}
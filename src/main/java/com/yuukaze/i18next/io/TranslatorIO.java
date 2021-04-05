package com.yuukaze.i18next.io;

import com.yuukaze.i18next.model.Translations;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Interface to retrieve and save localized messages.
 * Can be implemented by various standards. Such as JSON, Properties-Bundle and so on.
 * @author marhali
 */
public interface TranslatorIO {

    /**
     * Reads localized messages from the persistence layer.
     * @param directoryPath The full path for the directory which holds all locale files
     * @param callback Contains loaded translations. Will be called after io operation. Content might be null on failure.
     */
    void read(@NotNull String directoryPath, @NotNull Consumer<Translations> callback);

    /**
     * Writes the provided messages (translations) to the persistence layer.
     * @param translations Translations instance to save
     * @param directoryPath The full path for the directory which holds all locale files
     * @param callback Will be called after io operation. Can be used to determine if action was successful(true) or not
     */
    void save(@NotNull Translations translations, @NotNull String directoryPath, @NotNull Consumer<Boolean> callback);
}
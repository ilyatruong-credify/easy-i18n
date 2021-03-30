package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the persistent settings which can be configured.
 *
 * @author marhali
 */
public class SettingsState {

    public static final String DEFAULT_PREVIEW_LOCALE = "en";

    private String localesPath;
    private String previewLocale;
    private String keySeparator;

    public SettingsState() {
    }

    public @Nullable String getLocalesPath() {
        return localesPath;
    }

    public void setLocalesPath(String localesPath) {
        this.localesPath = localesPath;
    }

    public @NotNull String getPreviewLocale() {
        return previewLocale != null ? previewLocale : DEFAULT_PREVIEW_LOCALE;
    }

    public void setPreviewLocale(String previewLocale) {
        this.previewLocale = previewLocale;
    }

    public String getKeySeparator() {
        return keySeparator;
    }

    public void setKeySeparator(String keySeparator) {
        this.keySeparator = keySeparator;
    }

    public boolean hasSeparator() {
        return keySeparator != null && keySeparator.length() > 0;
    }
}
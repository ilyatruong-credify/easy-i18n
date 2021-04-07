package com.yuukaze.i18next.model;

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
    private boolean hasSeparator;
    private String spreadSheetId;
    private String spreadSheetTab;

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

    public boolean isHasSeparator() {
        return hasSeparator;
    }

    public void setHasSeparator(boolean hasSeparator) {
        this.hasSeparator = hasSeparator;
    }

    public @Nullable String getSpreadSheetId() {
        return spreadSheetId;
    }

    public void setSpreadSheetId(String spreadSheetId) {
        this.spreadSheetId = spreadSheetId;
    }

    public String getSpreadSheetTab() {
        return spreadSheetTab;
    }

    public void setSpreadSheetTab(String spreadSheetTab) {
        this.spreadSheetTab = spreadSheetTab;
    }
}
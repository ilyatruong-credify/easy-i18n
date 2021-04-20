package com.yuukaze.i18next.model

/**
 * Represents the persistent settings which can be configured.
 *
 * @author marhali
 */
class SettingsState {
  var localesPath: String = ""
  var previewLocale: String = ""
    get() = field.ifEmpty { DEFAULT_PREVIEW_LOCALE }
  var keySeparator: String = ""
  var isHasSeparator = false
  var spreadSheetId: String = ""
  var spreadSheetTab: String = ""

  companion object {
    const val DEFAULT_PREVIEW_LOCALE = "en"
  }
}
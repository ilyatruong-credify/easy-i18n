package com.yuukaze.i18next.model

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.yuukaze.i18next.service.getEasyI18nService

/**
 * Translated messages for a dedicated key.
 */
class KeyedTranslation(
  var key: String,
  var translations: Map<String, String>?
) {
  override fun toString(): String {
    return "KeyedTranslation{" +
        "key='" + key + '\'' +
        ", translations=" + translations +
        '}'
  }
}

fun Project.getKeyedFromPair(pair: Pair<String, String?>) = KeyedTranslation(
  pair.first,
  mapOf(this.getEasyI18nService().state.previewLocale to (pair.second ?: ""))
)
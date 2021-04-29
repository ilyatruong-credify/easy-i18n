package com.yuukaze.i18next.data

import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.model.Translations

data class SearchAction(val text: String)

data class ReloadTranslations(val translations: Translations)

open class UpdateTranslation
  (
  val origin: KeyedTranslation?,
  val change: KeyedTranslation?
) {
  val isCreation: Boolean
    get() = origin == null
  val isDeletion: Boolean
    get() = change == null
  val isKeyChange: Boolean
    get() = origin != null && change != null && origin.key != change.key

  override fun toString(): String {
    return "UpdateTranslation{" +
        "origin=" + origin +
        ", change=" + change +
        '}'
  }
}

class CreateTranslation(translation: KeyedTranslation) :
  UpdateTranslation(null, translation)

class DeleteTranslation(translation: KeyedTranslation) :
  UpdateTranslation(translation, null)

enum class TableFilterMode {
  ALL, SHOW_MISSING, SHOW_UNUSED
}

data class TableFilterAction(val mode: TableFilterMode)
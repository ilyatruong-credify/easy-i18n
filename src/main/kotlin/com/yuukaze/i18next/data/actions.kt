package com.yuukaze.i18next.data

import com.intellij.openapi.project.Project
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.model.Translations
import com.yuukaze.i18next.service.EasyI18nSettingsService
import com.yuukaze.i18next.service.PsiElementSet
import com.yuukaze.i18next.service.getEasyI18nReferenceService
import com.yuukaze.i18next.util.IOUtil
import com.yuukaze.reduxkotlin.thunk.Thunk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

data class InitProjectAction(val project: Project)

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

data class ReloadPsi(val map: Map<String, PsiElementSet>) {
  override fun toString(): String = "ReloadPsi(map.size=${map.size})"
}

fun reloadI18nData(): Thunk<AppState> = { dispatch, getState, extraArg ->
  runBlocking {
    val project = getState().project!!
    listOf(async {
      lateinit var translations: Translations
      val localesPath = project.getService(
        EasyI18nSettingsService::class.java
      ).state.localesPath
      if (localesPath.isEmpty()) {
        translations = Translations()
      } else {
        val io = IOUtil.determineFormat(localesPath)
        io.read(localesPath) {
          translations = it ?: Translations()
        }
      }
      dispatch(ReloadTranslations(translations = translations))
    }, async {
      project.getEasyI18nReferenceService().processAll()
    }).awaitAll()
  }
}
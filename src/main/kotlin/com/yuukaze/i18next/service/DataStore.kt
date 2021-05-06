package com.yuukaze.i18next.service

import com.intellij.openapi.project.Project
import com.yuukaze.i18next.data.ReloadTranslations
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.io.TranslatorIO
import com.yuukaze.i18next.model.Translations
import com.yuukaze.i18next.util.IOUtil
import java.util.function.Consumer

/**
 * Singleton service to manage localized messages.
 */
class DataStore(private val project: Project) {
  /**
   * @return Current translation state
   */
  val translations: Translations
    get() = i18nStore.getState().translations!!

  /**
   * Loads all translations from disk and overrides current [.translations] state.
   */
  fun reloadFromDisk() {
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
    i18nStore.dispatch(ReloadTranslations(translations = translations))
  }

  /**
   * Saves the current translation state to disk. See [TranslatorIO.save]
   *
   * @param callback Complete callback. Indicates if operation was successful(true) or not
   */
  private fun saveToDisk(callback: Consumer<Boolean>) {
    val localesPath = project.getEasyI18nService().state.localesPath
    // Cannot save without valid path
    if (localesPath.isEmpty()) {
      return
    }
    val io = IOUtil.determineFormat(localesPath)
    io.save(translations, localesPath, callback)
  }

  fun doWriteToDisk() {
    saveToDisk { success: Boolean ->
      if (success) {
        i18nStore.dispatch(ReloadTranslations(translations = translations))
      }
    }
  }
}
package com.yuukaze.i18next.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.yuukaze.i18next.data.CreateTranslation
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.components.LocaleDialogBase
import java.util.*

class AddDialog(project: Project, override val preKey: String?) :
  LocaleDialogBase(
    project,
    ResourceBundle.getBundle("messages").getString("action.add")
  ) {
  var extractedText: String? = null
  private val previewLocale: String =
    project.getEasyI18nService().state.previewLocale

  override fun getTranslation(locale: String?): String? {
    return if (locale == previewLocale) extractedText else null
  }

  fun showAndHandle() {
    val code = prepare().show()
    if (code == DialogWrapper.OK_EXIT_CODE) {
      val result = saveTranslation()
      if (callback != null) {
        callback!!.invoke(result)
      }
    }
  }

  private fun saveTranslation(): KeyedTranslation {
    val keyed = KeyedTranslation(keyTextField!!.text, entry)
    i18nStore.dispatch(CreateTranslation(keyed))
    return keyed
  }

}
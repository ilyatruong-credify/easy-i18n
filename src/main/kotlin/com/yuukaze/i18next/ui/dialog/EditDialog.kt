package com.yuukaze.i18next.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.model.TranslationDelete
import com.yuukaze.i18next.model.TranslationUpdate
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.components.LocaleDialogBase
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor
import java.util.*

class EditDialog(project: Project?, private val origin: KeyedTranslation) :
  LocaleDialogBase(
    project!!, ResourceBundle.getBundle("messages").getString("action.edit")
  ) {
  fun showAndHandle() {
    val code = prepare().show()
    if (code == DialogWrapper.OK_EXIT_CODE) { // Edit
      DataStore.getInstance(project).processUpdate(
        TranslationUpdate(
          origin, changes
        )
      )
    } else if (code == DeleteActionDescriptor.EXIT_CODE) { // Delete
      DataStore.getInstance(project).processUpdate(
        TranslationDelete(
          origin
        )
      )
    }
  }

  private val changes: KeyedTranslation
    get() {
      return KeyedTranslation(keyTextField!!.text, entry)
    }
  override val preKey: String?
    get() = origin.key

  override fun getTranslation(locale: String?): String? {
    return origin.translations[locale]
  }
}
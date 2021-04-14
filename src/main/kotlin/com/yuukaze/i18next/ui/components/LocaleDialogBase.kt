package com.yuukaze.i18next.ui.components

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.dialog.LocaleRecord
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor
import javax.swing.JPanel

abstract class LocaleDialogBase protected constructor(
  @JvmField val project: Project,
  private val title: String
) {
  @JvmField
  var callback: ((KeyedTranslation) -> Unit)? = null

  @JvmField
  protected var keyTextField: I18nAutoCompleteTextField? = null

  @JvmField
  protected var valueTextFields: Map<String, JBTextField>? = null

  protected abstract val preKey: String?

  protected abstract fun getTranslation(locale: String?): String?

  protected fun prepare(): DialogBuilder {
    keyTextField = I18nAutoCompleteTextField.create(project);
    keyTextField!!.text = preKey
    val valuePanel: JPanel = VerticalPanel()
    valueTextFields = HashMap()
    DataStore.getInstance(
      project
    ).translations.locales.forEach { locale ->
      LocaleRecord(locale, project, valueTextFields).let {
        it.localeText.text = getTranslation(locale)
        it.insertTo(valuePanel)
      }
    }

    return DialogBuilder().let {
      it.setTitle(title)
      it.removeAllActions()
      it.addCancelAction()
      it.addActionDescriptor(DeleteActionDescriptor())
      it.addOkAction()
      it.setCenterPanel(panel {
        row() {
          cell(isVerticalFlow = true, isFullWidth = true) {
            label("i18n Key")
            keyTextField!!(grow).focused()
          }
        }
        row() {
          valuePanel(grow)
        }
      })
      it
    }
  }
}

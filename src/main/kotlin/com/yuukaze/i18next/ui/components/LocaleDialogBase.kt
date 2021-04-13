package com.yuukaze.i18next.ui.components

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.dialog.LocaleRecord
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor
import com.yuukaze.i18next.ui.renderer.I18nKeyComponentSuggestionClient
import com.yuukaze.i18next.ui.renderer.SuggestionDropDownDecorator
import java.util.*
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.EtchedBorder

abstract class LocaleDialogBase protected constructor(
  @JvmField val project: Project,
  private val title: String
) {
  @JvmField
  var callback: ((KeyedTranslation) -> Unit)? = null

  @JvmField
  protected var keyTextField: JBTextField? = null

  @JvmField
  protected var valueTextFields: Map<String, JBTextField>? = null

  protected abstract val preKey: String?

  protected abstract fun getTranslation(locale: String?): String?

  private fun getSuggestion(input: String): List<String>? {
    if (input.isEmpty()) return null;
    return listOf("foo", "bar", "baz").filter { it.startsWith(input) }
  }

  private fun setupKeyAutoSuggestion(textField: JBTextField) {
    SuggestionDropDownDecorator.decorate(
      textField,
      I18nKeyComponentSuggestionClient(this::getSuggestion)
    )
  }

  protected fun prepare(): DialogBuilder {
    val rootPanel: JPanel = VerticalPanel()
    val keyPanel: JPanel = VerticalPanel()
    val keyLabel =
      JBLabel(ResourceBundle.getBundle("messages").getString("translation.key"))
    keyLabel.horizontalAlignment = JBLabel.LEFT
    val preKey = preKey
    keyTextField = JBTextField(preKey)
    this.setupKeyAutoSuggestion(keyTextField!!);
    keyLabel.labelFor = keyTextField
    keyPanel.add(keyLabel)
    keyPanel.border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
    keyPanel.add(keyTextField)
    rootPanel.add(keyPanel)
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
    val valuePane = JBScrollPane(valuePanel)
    valuePane.border = BorderFactory.createTitledBorder(
      EtchedBorder(),
      ResourceBundle.getBundle("messages").getString("translation.locales")
    )
    rootPanel.add(valuePane)
    val builder = DialogBuilder()
    builder.setTitle(title)
    builder.removeAllActions()
    builder.addCancelAction()
    builder.addActionDescriptor(DeleteActionDescriptor())
    builder.addOkAction()
    builder.setCenterPanel(rootPanel)
    return builder
  }
}
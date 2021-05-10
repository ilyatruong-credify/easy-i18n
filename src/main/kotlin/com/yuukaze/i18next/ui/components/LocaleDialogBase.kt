package com.yuukaze.i18next.ui.components

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

abstract class LocaleDialogBase protected constructor(
    val project: Project,
    private val title: String
) {
    var callback: ((KeyedTranslation) -> Unit)? = null

    private val locales = project.getEasyI18nDataStore().translations.locales

    protected var keyTextField: I18nAutoCompleteTextField? = null

    private val entryFields by lazy {
        locales.associateWith {
            createLocaleTextField(
                it
            )
        }
    }

    protected val entry: Map<String, String>
        get() = entryFields.mapValues { (_, field) -> field.text }

    protected abstract val preKey: String?

    protected abstract fun getTranslation(locale: String?): String?

    protected fun prepare(): DialogBuilder {
        keyTextField = I18nAutoCompleteTextField.create(project)
        keyTextField!!.text = preKey

        return DialogBuilder().let {
            it.setTitle(title)
            it.removeAllActions()
            it.addCancelAction()
            it.addActionDescriptor(DeleteActionDescriptor())
            it.addOkAction()
            it.setCenterPanel(panel {
                row {
                    cell(isVerticalFlow = true, isFullWidth = true) {
                        label("i18n Key")
                        keyTextField!!(grow).focused()
                    }
                }
                titledRow("Locales") {
                    locales.map { locale ->
                        row(locale) {
                            component(entryFields[locale]!!)
                        }
                    }
                }
            })
            it
        }
    }

    private fun createLocaleTextField(locale: String): JBTextField =
        JBTextField().let {
            it.text = getTranslation(locale)
            it.addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    val defaultLocale = project.getEasyI18nService().state.previewLocale
                    if (defaultLocale != locale && it.text.isEmpty()) {
                        it.text = entry[defaultLocale]!!
                        it.selectAll()
                    }
                }

                override fun focusLost(e: FocusEvent?) {
                }

            })
            it
        }
}
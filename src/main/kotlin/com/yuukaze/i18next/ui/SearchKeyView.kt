package com.yuukaze.i18next.ui

import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI
import com.intellij.ui.SearchTextField
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.data.SearchAction
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.utils.JComponentWrapper
import com.yuukaze.reduxkotlin.reselect.reselect
import com.yuukaze.uiUtils.addDebouncedDocumentListener
import javax.swing.JComponent


class SearchKeyView :
    JComponentWrapper<JComponent> {
    private val textField = SearchTextField(false)

    init {
        textField.isOpaque = false
        textField.textEditor!!.apply {
            border = JBUI.Borders.empty(3, 5)
            setUI(DarculaTextFieldUI())
        }
        i18nStore.reselect({ it.searchText }) {
//      if (!textField.textEditor.isFocusOwner) textField.text = it
            println(textField.textEditor.isFocusOwner)
        }
        textField.addDebouncedDocumentListener {
            i18nStore.dispatch(
                SearchAction(
                    text = document.getText(
                        0,
                        document.length
                    )
                )
            )
        }
    }

    override val component: JComponent
        get() = textField
}
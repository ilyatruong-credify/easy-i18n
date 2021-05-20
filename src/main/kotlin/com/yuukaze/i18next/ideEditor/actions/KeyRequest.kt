package com.yuukaze.i18next.ideEditor.actions

//import com.yuukaze.i18next.ui.renderer.I18nDetectRegexes
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.Consumer
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.ui.dialog.AddDialog
import com.yuukaze.i18next.utils.KeyMatcherBuilder

object KeyRequest {
    var postProcess: ((List<Any>) -> Unit)? = null
    fun executeAdd(
        project: Project,
        text: String,
        callback: Consumer<Any>
    ) {
        val add = AddDialog(project, null)
        add.extractedText = text
        add.callback = {
            run {
                callback.consume(it)
            }
        }
        add.showAndHandle()
    }

    fun manipulateTranslationKey(
        project: Project,
        text: String,
        editor: Editor,
        callback: Consumer<Any>
    ) {
        val translations = project.getEasyI18nDataStore().translations
        val fullKeys = translations.fullKeys.mapNotNull {
            KeyMatcherBuilder.run(text, it)
        }
        postProcess?.let { it(fullKeys) }
        if (fullKeys.isEmpty()) {
            executeAdd(project, text, callback)
        } else {
            val popup =
                JBPopupFactory.getInstance().createPopupChooserBuilder(fullKeys + "or add new translation...")
                    .setTitle("Choose existing translation(s) below")
                    .setMovable(false)
                    .setResizable(false)
                    .setRequestFocus(true)
                    .setCancelOnWindowDeactivation(false)
                    .setItemChosenCallback {
                        run {
                            if (it !is String)
                                callback.consume(it)
                            else executeAdd(project, text, callback)
                        }
                    }
                    .createPopup()
            popup.showInBestPositionFor(editor)
        }
    }
}
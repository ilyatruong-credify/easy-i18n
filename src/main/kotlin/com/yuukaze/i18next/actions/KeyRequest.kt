package com.yuukaze.i18next.actions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.dialog.AddDialog
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object KeyRequest {
    fun key(project: Project, text: String, editor: Editor) {
        val translations = DataStore.getInstance(project).translations;
        val fullKeys = translations.fullKeys.filter { it.second.equals(text) }

        if (fullKeys.isEmpty()) {
            val add = AddDialog(project, null)
            add.extractedText = text;
            add.setCallback { keyed ->
                run {
                    addToClipboard(keyed.key)
                }
            }
            add.showAndHandle();
        } else {
            val popup = JBPopupFactory.getInstance().createPopupChooserBuilder(fullKeys)
                .setTitle("Choose existing translation(s) below")
                .setMovable(false)
                .setResizable(false)
                .setRequestFocus(true)
                .setCancelOnWindowDeactivation(false)
                .setItemChosenCallback { keyed ->
                    run {
                        addToClipboard(keyed.first)
                    }
                }
                .createPopup();
            popup.showInBestPositionFor(editor);
        }
    }

    private fun addToClipboard(s: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard;
        val selection = StringSelection("t(\"$s\")");
        clipboard.setContents(selection, selection)
    }
}
package com.yuukaze.i18next.actions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.Consumer
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.dialog.AddDialog
import com.yuukaze.i18next.ui.renderer.DetectRegexes


object KeyRequest {
  fun manipulateTranslationKey(
    project: Project,
    text: String,
    editor: Editor,
    callback: Consumer<String>
  ) {
    val translations = DataStore.getInstance(project).translations
    val fullKeys = translations.fullKeys.filter {
      when {
        DetectRegexes.variable.containsMatchIn(text) -> true
        else -> it.second.equals(text)
      }
    }

    if (fullKeys.isEmpty()) {
      val add = AddDialog(project, null)
      add.extractedText = text
      add.setCallback { keyed ->
        run {
          callback.consume(keyed.key)
        }
      }
      add.showAndHandle()
    } else {
      val popup =
        JBPopupFactory.getInstance().createPopupChooserBuilder(fullKeys)
          .setTitle("Choose existing translation(s) below")
          .setMovable(false)
          .setResizable(false)
          .setRequestFocus(true)
          .setCancelOnWindowDeactivation(false)
          .setItemChosenCallback { keyed ->
            run {
              callback.consume(keyed.first)
            }
          }
          .createPopup()
      popup.showInBestPositionFor(editor)
    }
  }
}
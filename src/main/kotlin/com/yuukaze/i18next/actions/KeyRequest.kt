package com.yuukaze.i18next.actions

//import com.yuukaze.i18next.ui.renderer.I18nDetectRegexes
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.Consumer
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.ui.dialog.AddDialog
import com.yuukaze.i18next.utils.KeyMatcherBuilder


object KeyRequest {
  fun manipulateTranslationKey(
    project: Project,
    text: String,
    editor: Editor,
    callback: Consumer<KeyedTranslation>
  ) {
    val translations = project.getEasyI18nDataStore().translations
    val fullKeys = translations.fullKeys.mapNotNull {
      KeyMatcherBuilder.run(text, it)
    }

    if (fullKeys.isEmpty()) {
      val add = AddDialog(project, null)
      add.extractedText = text
      add.callback = {
        run {
          callback.consume(it)
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
          .setItemChosenCallback {
            run {
//              callback.consume(project.getKeyedFromPair(it))
            }
          }
          .createPopup()
      popup.showInBestPositionFor(editor)
    }
  }
}
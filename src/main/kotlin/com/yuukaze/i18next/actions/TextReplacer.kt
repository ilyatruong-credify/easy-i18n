package com.yuukaze.i18next.actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.yuukaze.i18next.actions.KeyRequest.key
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.service.SettingsService
import com.yuukaze.i18next.utils.whenMatches
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

internal class DefaultExtractor : TranslationExtractor {
  override fun canExtract(element: PsiElement): Boolean = false
  override fun text(element: PsiElement): String = ""
  override fun isExtracted(element: PsiElement): Boolean = false
}

class TextReplacer : PsiElementBaseIntentionAction(), IntentionAction {
  override fun getText(): @IntentionName String {
    return "I18n-ize..."
  }

  override fun invoke(project: Project, editor: Editor, element: PsiElement) {
    ApplicationManager.getApplication()
      .invokeLater { doInvoke(editor, project, element) }
  }

  private fun getExtractor(e: PsiElement): TranslationExtractor =
    SettingsService.getInstance(e.project)
      .mainFactory()
      .translationExtractors()
      .filter { it.canExtract(e) }
      .whenMatches { extractors -> !extractors.any { it.isExtracted(e) } }
      ?.firstOrNull()
      ?: DefaultExtractor()


  override fun isAvailable(
    project: Project,
    editor: Editor,
    element: PsiElement
  ): Boolean = getExtractor(element).canExtract(element)

  override fun getFamilyName(): @IntentionFamilyName String {
    return "EasyI18n"
  }

  private fun doInvoke(editor: Editor, project: Project, element: PsiElement) {
    if (editor.selectionModel.hasSelection())
      Objects.requireNonNull(editor.selectionModel.selectedText)?.let {
        key(
          project,
          it,
          editor,
          this::addToClipboard
        )
      }
    else {
      val extractor = getExtractor(element)
      val text = extractor.text(element).trim()
      println(text)
    }
  }

  private fun addToClipboard(s: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection("t(\"$s\")")
    clipboard.setContents(selection, selection)
  }
}
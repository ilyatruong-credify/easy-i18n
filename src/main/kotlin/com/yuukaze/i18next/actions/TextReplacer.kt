package com.yuukaze.i18next.actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.yuukaze.i18next.actions.KeyRequest.manipulateTranslationKey
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.service.SettingsService
import com.yuukaze.i18next.utils.memoize
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

  private fun _getExtractor(e: PsiElement): TranslationExtractor =
    SettingsService.getInstance(e.project)
      .mainFactory()
      .translationExtractors()
      .filter { it.canExtract(e) }
      .whenMatches { extractors -> !extractors.any { it.isExtracted(e) } }
      ?.firstOrNull()
      ?: DefaultExtractor()

  private val getExtractor = ::_getExtractor.memoize(1024)


  override fun isAvailable(
    project: Project,
    editor: Editor,
    element: PsiElement
  ): Boolean =
    editor.selectionModel.hasSelection() || getExtractor(element).canExtract(
      element
    )

  override fun getFamilyName(): @IntentionFamilyName String {
    return "EasyI18n"
  }

  private fun doInvoke(editor: Editor, project: Project, element: PsiElement) {
    if (editor.selectionModel.hasSelection())
      Objects.requireNonNull(editor.selectionModel.selectedText)?.let {
        manipulateTranslationKey(
          project,
          it,
          editor,
          this::addToClipboard
        )
      }
    else {
      val extractor = getExtractor(element)
      val text = extractor.text(element).trim()
      manipulateTranslationKey(
        project,
        text,
        editor
      ) { key -> replaceFromPsi(key, editor, element, extractor) }
    }
  }

  private fun addToClipboard(s: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection("t(\"$s\")")
    clipboard.setContents(selection, selection)
  }

  private fun replaceFromPsi(
    key: String,
    editor: Editor,
    element: PsiElement,
    extractor: TranslationExtractor
  ) {
    val document = editor.document
    val template = extractor.template(element)
    val range = extractor.textRange(element)

    CommandProcessor.getInstance().executeCommand(
      element.project,
      {
        ApplicationManager.getApplication().runWriteAction {
          document.replaceString(
            range.startOffset,
            range.endOffset,
            template("'${key}'")
          )
        }
      },
      "EasyI18n",
      UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
    )
    extractor.postProcess(editor, range.startOffset)
    editor.caretModel.primaryCaret.removeSelection()
  }
}
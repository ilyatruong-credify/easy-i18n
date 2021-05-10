package com.yuukaze.i18next.actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.yuukaze.i18next.data.selectI18nKey
import com.yuukaze.i18next.openToolWindow
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.Icons
import com.yuukaze.i18next.utils.memoize
import com.yuukaze.i18next.utils.whenMatches
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.Icon

/**
 * TODO
 * show in JSX text (not attribute)
 */

@Suppress("IntentionDescriptionNotFoundInspection")
class GoToI18nTable : PsiElementBaseIntentionAction(),
  IntentionAction, Iconable {

  private val getExtractor = { e: PsiElement ->
    e.project.getEasyI18nService()
      .mainFactory()
      .translationExtractors()
      .filter { it.canExtract(e) }
      .whenMatches { extractors -> extractors.any { it.isExtracted(e) } }
      ?.firstOrNull()
  }.memoize(1024)

  override fun getFamilyName(): String = "EasyI18n"
  override fun getText(): String = "Open i18n Table"

  override fun isAvailable(
    project: Project,
    editor: Editor,
    element: PsiElement
  ): Boolean =
    !editor.selectionModel.hasSelection() && getExtractor(element) != null

  @ExperimentalCoroutinesApi
  override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    project.openToolWindow()
    selectI18nKey(element.text!!.removeSurrounding("\""))
  }

  override fun getIcon(flags: Int): Icon = Icons.ToolWindowIcon
}
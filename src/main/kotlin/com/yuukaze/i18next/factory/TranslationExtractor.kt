package com.yuukaze.i18next.factory

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

interface TranslationExtractor {

  /**
   * Checks if it is possible to extract translation from given element
   */
  fun canExtract(element: PsiElement): Boolean

  /**
   * Checks if translation already extracted
   */
  fun isExtracted(element: PsiElement): Boolean

  /**
   * Get text of translation
   */
  fun text(element: PsiElement): String

  /**
   * Get translation textRange
   */
  fun textRange(element: PsiElement): TextRange = element.parent.textRange

  /**
   * Get template to substitute translation with
   */
  fun template(element: PsiElement): (argument: String) -> String = {"i18n.t($it)"}

  fun postProcess(editor: Editor, offset: Int) {}
}
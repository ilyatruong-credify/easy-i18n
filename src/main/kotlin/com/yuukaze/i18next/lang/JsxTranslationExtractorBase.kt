package com.yuukaze.i18next.lang

import com.intellij.lang.ecmascript6.JSXHarmonyFileType
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.utils.toBoolean

abstract class JsxTranslationExtractorBase : TranslationExtractor {
  override fun canExtract(element: PsiElement): Boolean = listOf(
    JSXHarmonyFileType.INSTANCE,
    TypeScriptJSXFileType.INSTANCE
  ).any { it == element.containingFile.fileType } &&
      element.getParentJsxTag().toBoolean()

  protected fun PsiElement.getParentJsxTag(): XmlTag? =
    PsiTreeUtil.getParentOfType(this, XmlTag::class.java)

  protected fun PsiElement.isJs(): Boolean =
    this.language == JavascriptLanguage.INSTANCE
}
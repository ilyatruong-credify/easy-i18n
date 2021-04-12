package com.yuukaze.i18next.lang

import com.intellij.lang.javascript.patterns.JSPatterns
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.yuukaze.i18next.factory.LanguageFactory
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.utils.toBoolean

class JsxAttributeFactory : LanguageFactory {
  override fun translationExtractor(): TranslationExtractor =
    JsxAttributeExtractor()
}

open class JsxAttributeExtractor : JsxTranslationExtractorBase() {
  override fun canExtract(element: PsiElement): Boolean =
    super.canExtract(element) && element.getAttribute().toBoolean()

  override fun isExtracted(element: PsiElement): Boolean =
    element.isJs() && JSPatterns.jsArgument("t", 0).accepts(element.parent)

  override fun text(element: PsiElement): String =
    element.getAttribute()!!
      .value!!

  override fun textRange(element: PsiElement): TextRange =
    element.getAttribute()!!.valueElement!!.textRange

  override fun template(element: PsiElement): (argument: String) -> String =
    { "{t($it)}" }

  fun PsiElement.getAttribute(): XmlAttribute? = PsiTreeUtil.getParentOfType(
    this,
    XmlAttribute::class.java
  )
}
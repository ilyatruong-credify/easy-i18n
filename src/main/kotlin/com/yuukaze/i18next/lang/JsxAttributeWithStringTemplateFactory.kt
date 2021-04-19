package com.yuukaze.i18next.lang

import com.intellij.lang.javascript.patterns.JSPatterns
import com.intellij.lang.javascript.psi.ecma6.JSStringTemplateExpression
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.yuukaze.i18next.factory.LanguageFactory
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.utils.toBoolean

class JsxAttributeWithStringTemplateFactory : LanguageFactory {
  override fun translationExtractor(): TranslationExtractor =
    JsxAttributeExtractor()
}

internal class JsxAttributeWithStringTemplateExtractor :
  JsxAttributeExtractor() {
  override fun canExtract(element: PsiElement): Boolean =
    super.canExtract(element) && (element.getAttribute()!!
      .getInnerStringTemplate()).toBoolean()

  override fun isExtracted(element: PsiElement): Boolean =
    element.isJs() && JSPatterns.jsArgument("t", 0).accepts(element.parent)

  override fun text(element: PsiElement): String =
    PsiTreeUtil.getParentOfType(element, XmlAttribute::class.java)!!
      .value!!

  override fun textRange(element: PsiElement): TextRange =
    PsiTreeUtil.getParentOfType(
      element,
      XmlAttribute::class.java
    )!!.valueElement!!.textRange

  override fun template(element: PsiElement): (argument: KeyedTranslation) -> String =
    { "{t($it)}" }

  fun PsiElement.getInnerStringTemplate(): JSStringTemplateExpression? =
    PsiTreeUtil.findChildOfType(this, JSStringTemplateExpression::class.java)
}
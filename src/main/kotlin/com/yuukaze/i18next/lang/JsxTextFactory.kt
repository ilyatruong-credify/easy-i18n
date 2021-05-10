package com.yuukaze.i18next.lang

import com.intellij.lang.javascript.patterns.JSPatterns
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTokenType
import com.yuukaze.i18next.factory.LanguageFactory
import com.yuukaze.i18next.factory.TranslationExtractor
import com.yuukaze.i18next.model.KeyedTranslation

class JsxTextFactory : LanguageFactory {
    override fun translationExtractor(): TranslationExtractor =
        JsxTextExtractor()
}

internal class JsxTextExtractor : JsxTranslationExtractorBase() {
    override fun canExtract(element: PsiElement): Boolean =
        super.canExtract(element) && element.elementType == XmlTokenType.XML_DATA_CHARACTERS

    override fun isExtracted(element: PsiElement): Boolean =
        element.isJs() && JSPatterns.jsArgument("t", 0).accepts(element.parent)

    override fun text(element: PsiElement): String =
        PsiTreeUtil.getParentOfType(element, XmlTag::class.java)!!
            .value
            .textElements.joinToString(" ") { it.text }

    override fun textRange(element: PsiElement): TextRange =
        PsiTreeUtil.getParentOfType(element, XmlTag::class.java)!!
            .value
            .textElements
            .let {
                TextRange(
                    it.first().textRange.startOffset,
                    it.last().textRange.endOffset
                )
            }

    override fun template(element: PsiElement): (argument: KeyedTranslation) -> String =
        { "{t($it)}" }
}
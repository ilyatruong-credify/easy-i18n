package com.yuukaze.i18next.actions

import com.intellij.psi.PsiElement
import com.yuukaze.i18next.factory.TranslationExtractor

internal class DefaultExtractor : TranslationExtractor {
    override fun canExtract(element: PsiElement): Boolean = false
    override fun text(element: PsiElement): String = ""
    override fun isExtracted(element: PsiElement): Boolean = false
}
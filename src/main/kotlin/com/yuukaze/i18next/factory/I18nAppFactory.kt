package com.yuukaze.i18next.factory

interface LanguageFactory {
    /**
     * Get translation extractor object
     */
    fun translationExtractor(): TranslationExtractor
}

class I18nAppFactory(private val languageFactories: List<LanguageFactory>) {
    fun translationExtractors(): List<TranslationExtractor> =
        languageFactories.map { it.translationExtractor() }
}
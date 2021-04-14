package com.yuukaze.i18next.ui.components

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.ui.renderer.I18nKeyComponentSuggestionClient
import com.yuukaze.i18next.ui.renderer.SuggestionDropDownDecorator

class I18nAutoCompleteTextField private constructor() : JBTextField() {
  private lateinit var project: Project

  init {
    SuggestionDropDownDecorator.decorate(
      this,
      I18nKeyComponentSuggestionClient(this::getSuggestion)
    )
  }

  private fun getSuggestion(input: String): List<String>? {
    val translations = project.getEasyI18nDataStore().translations
    if (input.isEmpty()) return null;
    return translations.treeKeys.children.map { it.value }.filter {
      it.startsWith(
        input
      )
    }
  }

  companion object {
    fun create(project: Project): I18nAutoCompleteTextField =
      I18nAutoCompleteTextField().let {
        it.project = project;
        it
      }
  }
}
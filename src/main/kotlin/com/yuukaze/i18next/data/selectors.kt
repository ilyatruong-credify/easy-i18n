package com.yuukaze.i18next.data

import com.yuukaze.reduxkotlin.reselect.reselect

object I18nReduxSelectors {
  val filteredTranslations = i18nStore.reselect { state ->
    val searchText = state.searchText
    state.translations.clone(state.translations.nodes.children.filter { node ->
      node.key.startsWith(searchText)
    })
  }
}
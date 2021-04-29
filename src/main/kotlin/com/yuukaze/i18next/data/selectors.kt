package com.yuukaze.i18next.data

import com.yuukaze.i18next.model.LocalizedNode
import com.yuukaze.i18next.model.Translations
import com.yuukaze.reduxkotlin.reselect.reselect

typealias TFilter = (LocalizedNode) -> Boolean

private val primitiveFilterFn: TFilter = { node -> true }
fun foldAnd(fns: List<TFilter>): TFilter =
  fns.reduce { f1, f2 -> { node: LocalizedNode -> f1(node) and f2(node) } }

object I18nReduxSelectors {
  val filteredTranslations = i18nStore.reselect { state ->
    val searchText = state.searchText
    val filterFn = foldAnd(listOf(
      { node -> node.key.startsWith(searchText) },
      when (state.filter) {
        TableFilterMode.SHOW_UNUSED -> { it: LocalizedNode -> true }
        else -> primitiveFilterFn
      }
    ))
    state.translations?.let { it ->
      it.clone(it.nodes.children.filter(filterFn))
    } ?: Translations()
  }
}
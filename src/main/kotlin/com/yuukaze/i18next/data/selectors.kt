package com.yuukaze.i18next.data

import com.yuukaze.i18next.model.LocalizedNode
import com.yuukaze.i18next.service.PsiElementSet
import com.yuukaze.i18next.utils.memoize
import com.yuukaze.reduxkotlin.reselect.reselect

typealias TFilter = (LocalizedNode) -> Boolean

private fun primitiveFilterFn(node: LocalizedNode) = true
fun foldAnd(fns: List<TFilter>): TFilter =
  fns.reduce { f1, f2 -> { node: LocalizedNode -> f1(node) and f2(node) } }

private val unusedFilter = { psiMap: Map<String, PsiElementSet> ->
  { node: LocalizedNode ->
    (psiMap[node.key]?.size ?: 0) == 0
  }
}.memoize(10240)

object I18nReduxSelectors {
  val filteredTranslations = i18nStore.reselect { state ->
    val searchText = state.searchText
    val filterFn = foldAnd(
      listOf(
        { node -> node.key.startsWith(searchText) },
        when (state.filter) {
          TableFilterMode.SHOW_UNUSED -> unusedFilter(state.psiMap!!)
          else -> ::primitiveFilterFn
        }
      )
    )
    state.translations?.let { it ->
      it.clone(it.nodes.children.filter(filterFn))
    }
  }

//  val filter = i18nStore.reselectors(
//    { it.searchText },
//    { it.filter },
//    { it.psiMap }
//  ) { searchText, filter, psiMap ->
//    foldAnd(
//      listOf(
//        { node -> node.key.startsWith(searchText) },
//        when (filter) {
//          TableFilterMode.SHOW_UNUSED -> unusedFilter(psiMap!!)
//          else -> ::primitiveFilterFn
//        }
//      )
//    )
//  }
}
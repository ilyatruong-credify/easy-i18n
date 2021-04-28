package com.yuukaze.i18next.data

import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.model.LocalizedNode
import com.yuukaze.i18next.model.Translations
import com.yuukaze.i18next.util.TranslationsUtil
import org.reduxkotlin.Reducer
import org.reduxkotlin.createThreadSafeStore

data class AppState(
  val searchText: String = "",
  val translations: Translations = Translations()
)

fun processUpdate(
  translations: Translations,
  update: UpdateTranslation?
): Translations {
  if (update != null) {
    if (update.isDeletion || update.isKeyChange) { // Delete origin i18n key
      val originKey = update.origin!!.key
      val sections = TranslationsUtil.getSections(originKey)
      val nodeKey =
        sections.removeAt(sections.size - 1) // Remove last node, which needs to be removed by parent
      var node: LocalizedNode? = translations.nodes
      for (section in sections) {
        if (node == null) { // Might be possible on multi-delete
          break
        }
        node = node.getChildren(section!!)
      }
      if (node != null) { // Only remove if parent exists. Might be already deleted on multi-delete
        node.removeChildren(nodeKey)

        // Parent is empty now, we need to remove it as well (except root)
        if (node.children.isEmpty() && node.key != LocalizedNode.ROOT_KEY) {
          processUpdate(
            translations, DeleteTranslation(
              KeyedTranslation(
                TranslationsUtil.sectionsToFullPath(sections), null
              )
            )
          )
        }
      }
    }
    if (!update.isDeletion) { // Recreate with changed val / create
      val node = translations.getOrCreateNode(update.change!!.key)
      node.value = update.change.translations as MutableMap<String, String>
    }
  }
  return translations
}

val reducer: Reducer<AppState> = { state, action ->
  when (action) {
    is SearchAction -> state.copy(searchText = action.text)
    is ReloadTranslations -> state.copy(translations = action.translations)
    is UpdateTranslation -> state.copy(
      translations = processUpdate(
        state.translations,
        action
      )
    )
    else -> state
  }
}

val i18nStore = createThreadSafeStore(reducer, AppState())
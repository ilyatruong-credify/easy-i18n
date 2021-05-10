package com.yuukaze.i18next.model.spreadsheet

import com.intellij.util.containers.ImmutableList
import com.yuukaze.i18next.model.LocalizedNode
import com.yuukaze.i18next.model.Translations
import java.util.*

class SpreadsheetUploadModel(private val translations: Translations) :
    AbstractList<List<Any>>() {
    private val locales: List<String> = translations.locales
    override fun get(index: Int): List<Any> {
        if (index == 0) {
            val keys: MutableList<Any> = ArrayList()
            keys.add("Key")
            keys.addAll(locales)
            return keys
        }
        val node =
            translations.nodes.children.toTypedArray()[index - 1] as LocalizedNode
        return object : ImmutableList<Any>() {
            override val size: Int = locales.size + 1

            override fun get(index: Int) =
                if (index == 0) node.key else node.value[locales[index - 1]] ?: ""
        }
    }

    override val size: Int = translations.nodes.children.size

}
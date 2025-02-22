package com.yuukaze.i18next.utils

import com.intellij.openapi.util.Pair
import com.yuukaze.i18next.model.I18nKeyed

object KeyMatcherBuilder : KeyMatcherRunner<Any> {
    private val runners = listOf<(String, Pair<String, String?>) -> Any?>(
        VariableKeyMatcher::run,
        SingleKeyMatcher::run
    )

    override fun run(text: String, rawKey: Pair<String, String?>): Any? {
        for (runner in runners) {
            val matcher = runner(text, rawKey)
            if (matcher != null) return matcher;
        }
        return null;
    }
}

interface KeyMatcherRunner<M> {
    fun run(text: String, rawKey: Pair<String, String?>): M?
}

class VariableKeyMatcher private constructor(
    key: String,
    val params: Map<String, String>
) : I18nKeyed(key) {
    companion object : KeyMatcherRunner<VariableKeyMatcher> {
        private val regex = Regex("\\{\\{([^}]+)}}")

        override fun run(
            text: String,
            rawKey: Pair<String, String?>
        ): VariableKeyMatcher? {
            val compareText = rawKey.second ?: return null

            // must return null if keyText not formed variable-translation
            if (!regex.containsMatchIn(text)) return null

            //transform search text into regex
            val regexFromText = Regex(text.replace(regex, "\\\\{\\\\{([^}]+)}}"))
            val match = regexFromText.matchEntire(compareText)
            if (match != null) {
                val textMatch = regexFromText.matchEntire(text)!!.groupValues
                var index = 0
                return VariableKeyMatcher(
                    key = rawKey.first,
                    params = match.groupValues.drop(1)
                        .associateWith { textMatch[++index] })
            }
            return null;
        }
    }
}

class SingleKeyMatcher private constructor(key: String) : I18nKeyed(key) {
    companion object : KeyMatcherRunner<SingleKeyMatcher> {
        override fun run(
            text: String,
            rawKey: Pair<String, String?>
        ): SingleKeyMatcher? =
            if (rawKey.second == text) SingleKeyMatcher(rawKey.first) else null
    }
}

fun Map<String, String>.toI18nParamsObject(): String =
    this.map { "${it.key}:${it.value}" }.reduce { acc, v ->
        "$acc,$v"
    }.let { return "{$it}" }

fun main() {
    val searchText = "Hello, Phong"
    val key = Pair("key.name", "Hello, Phong")
    KeyMatcherBuilder.run(searchText, key)
}
package com.yuukaze.i18next.utils

import com.intellij.openapi.util.Pair

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
  val key: String,
  val params: Map<String, String>
) {
  companion object : KeyMatcherRunner<VariableKeyMatcher> {
    val regex = Regex("\\{\\{([^}]+)}}")

    override fun run(
      text: String,
      rawKey: Pair<String, String?>
    ): VariableKeyMatcher? {
      val compareText = rawKey.second ?: return null
      //transform search text into regex
      val regexFromText = Regex(text.replace(regex, "\\\\{\\\\{([^}]+)}}"))
      val match = regexFromText.matchEntire(compareText)
      if (match != null) {
        val textMatch = regexFromText.matchEntire(text)!!.groupValues
        var index = 0
        return VariableKeyMatcher(
          key = rawKey.first,
          params = match.groupValues.drop(1).associateWith { textMatch[++index] })
      }
      return null;
    }
  }
}

class SingleKeyMatcher private constructor(val key: String) {
  companion object : KeyMatcherRunner<SingleKeyMatcher> {
    override fun run(
      text: String,
      rawKey: Pair<String, String?>
    ): SingleKeyMatcher? =
      if (rawKey.second == text) SingleKeyMatcher(rawKey.first) else null
  }
}
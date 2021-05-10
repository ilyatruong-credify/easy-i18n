package com.yuukaze.i18next.utils

/**
 * Chain-style predicate matcher
 */
inline fun <C> C.whenMatches(predicate: (arg: C) -> Boolean): C? {
    return if (predicate(this)) this else null
}

/**
 * Converts nullable to Boolean
 */
fun <C> C?.toBoolean(): Boolean {
    return this != null
}
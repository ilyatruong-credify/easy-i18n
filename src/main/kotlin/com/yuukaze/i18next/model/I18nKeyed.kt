package com.yuukaze.i18next.model

abstract class I18nKeyed(val key: String) {
    override fun toString(): String = key
}
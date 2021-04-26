package com.yuukaze.i18next.lang.psi

import com.intellij.lang.javascript.psi.JSElement
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey

class I18nKeyIndex : StringStubIndexExtension<JSElement>() {
  companion object {
    val KEY: StubIndexKey<String, JSElement> =
      StubIndexKey.createIndexKey("i18n.key.index")
  }

  override fun getKey(): StubIndexKey<String, JSElement> = KEY
}
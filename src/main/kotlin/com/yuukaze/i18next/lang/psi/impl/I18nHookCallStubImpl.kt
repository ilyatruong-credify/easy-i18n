package com.yuukaze.i18next.lang.psi.impl

import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.yuukaze.i18next.lang.parsing.I18nEntryElementTypes
import com.yuukaze.i18next.lang.psi.I18nHookCallStub

class I18nHookCallStubImpl(parent: StubElement<*>, key: String) :
  StubBase<JSCallExpression>(parent, I18nEntryElementTypes.HOOK_CALL),
  I18nHookCallStub {

}
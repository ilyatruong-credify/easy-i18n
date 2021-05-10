package com.yuukaze.i18next.lang.parsing

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.psi.stubs.*
import com.yuukaze.i18next.lang.psi.I18nHookCallStub

class I18nHookCallStubElementType() :
    ILightStubElementType<I18nHookCallStub, JSCallExpression>(
        "I18N_HOOK_CALL",
        JavascriptLanguage.INSTANCE
    ) {
    override fun getExternalId() = "i18n.keys"

    override fun serialize(stub: I18nHookCallStub, dataStream: StubOutputStream) {
        TODO("Not yet implemented")
    }

    override fun deserialize(
        dataStream: StubInputStream,
        parentStub: StubElement<*>?
    ): I18nHookCallStub {
        TODO("Not yet implemented")
    }

    override fun indexStub(stub: I18nHookCallStub, sink: IndexSink) {
        println("OK")
    }

    override fun createPsi(stub: I18nHookCallStub): JSCallExpression {
        TODO("Not yet implemented")
    }

    override fun createStub(
        tree: LighterAST,
        node: LighterASTNode,
        parentStub: StubElement<*>
    ): I18nHookCallStub {
        TODO("Not yet implemented")
    }

    override fun createStub(
        psi: JSCallExpression,
        parentStub: StubElement<*>?
    ): I18nHookCallStub {
        TODO("Not yet implemented")
    }
}
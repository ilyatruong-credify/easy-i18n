package com.yuukaze.i18next.ideEditor.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yuukaze.i18next.data.deleteI18nTranslation
import com.yuukaze.i18next.data.i18nStore
import org.junit.Test

internal class DataActionTest : BasePlatformTestCase() {

    @Test
    fun `test delete i18n translation`() {
        initProject()
        i18nStore.deleteI18nTranslation("foo.key1")
        val translations = i18nStore.state.translations!!
        assertNull(translations.getNode("foo.key1"))
    }

    override fun getTestDataPath(): String = "src/test/testData/textReplacer"

    private fun initProject() = myFixture.initProject()
}
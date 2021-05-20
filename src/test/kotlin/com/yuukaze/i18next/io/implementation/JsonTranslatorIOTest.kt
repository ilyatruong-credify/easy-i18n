package com.yuukaze.i18next.io.implementation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yuukaze.i18next.data.CreateTranslation
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.ideEditor.actions.initProject
import com.yuukaze.i18next.model.KeyedTranslation
import com.yuukaze.i18next.service.getEasyI18nDataStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JsonTranslatorIOTest : BasePlatformTestCase() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
        myFixture.initProject()
    }

    override fun getTestDataPath(): String = "src/test/testData/newKey"

    @Test
    fun `test case1 add new key with only english translation`() {
//        myFixture.configureByFile("../_common/locales/vi/translation.json")
        val keyed = KeyedTranslation("foo.key3", mapOf("en" to "Foo Key3"))
        i18nStore.dispatch(CreateTranslation(keyed))
        myFixture.project.getEasyI18nDataStore().doWriteToDisk()
        myFixture.checkResultByFile("locales/vi/translation.json", "vi/translation.json", true)
        myFixture.checkResultByFile("locales/en/translation.json", "en/translation.json", true)
    }
}
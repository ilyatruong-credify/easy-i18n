package com.yuukaze.i18next.actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.yuukaze.i18next.data.InitProjectAction
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.data.reloadI18nData
import com.yuukaze.i18next.model.I18nKeyed
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.utils.IOUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class I18nTextReplacerTest : BasePlatformTestCase() {
    private val hint = "I18n-ize..."

    @Test
    fun testReplaceSimpleXmlText() {
        myFixture.configureByFile("simple.tsx")
        initProject()
        val action: IntentionAction = myFixture.findSingleIntention(hint)
        assertNotNull(action)
        var predicates: List<Any>? = null
        KeyRequest.postProcess = {
            predicates = it
        }
        myFixture.launchAction(action)
        assertEquals(predicates?.size ?: 0, 1)
        assertEquals((predicates!![0] as I18nKeyed).key, "foo.key1")
    }

    @Test
    fun testReplaceSimpleXmlText_WithNameVariable() {
        myFixture.configureByFile("simpleWithNameVariable.tsx")
        initProject()
        val action: IntentionAction = myFixture.findSingleIntention(hint)
        assertNotNull(action)
        var predicates: List<Any>? = null
        KeyRequest.postProcess = {
            predicates = it
        }
        myFixture.launchAction(action)
        assertEquals(predicates?.size ?: 0, 1)
        assertEquals((predicates!![0] as I18nKeyed).key, "foo.keyNamed1")
    }

    @Test
    fun testReplaceSimpleXmlAttr() {
        myFixture.configureByFile("simpleAttr.tsx")
        initProject()
        val action: IntentionAction = myFixture.findSingleIntention(hint)
        assertNotNull(action)
        var predicates: List<Any>? = null
        KeyRequest.postProcess = {
            predicates = it
        }
        myFixture.launchAction(action)
        assertEquals(predicates?.size ?: 0, 1)
        assertEquals((predicates!![0] as I18nKeyed).key, "foo.key1")
    }

    override fun getTestDataPath(): String = "src/test/testData/textReplacer"

    private fun initProject() {
        IOUtil.getFile = myFixture.tempDirFixture::getFile
        i18nStore.dispatch(InitProjectAction(project = myFixture.project))
        myFixture.copyDirectoryToProject("../_common/locales", "locales")
        project.getEasyI18nService().state.localesPath = "locales"
        i18nStore.dispatch(reloadI18nData())
    }
}
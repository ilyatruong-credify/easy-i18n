package com.yuukaze.i18next.ideEditor.actions

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.yuukaze.i18next.data.InitProjectAction
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.data.reloadI18nData
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.utils.IOUtil

fun CodeInsightTestFixture.initProject() {
    project.apply {
        IOUtil.getFile = tempDirFixture::getFile
        i18nStore.dispatch(InitProjectAction(project = this))
        copyDirectoryToProject("../_common/locales", "locales")
        getEasyI18nService().state.localesPath = "locales"
        i18nStore.dispatch(reloadI18nData())
    }
}
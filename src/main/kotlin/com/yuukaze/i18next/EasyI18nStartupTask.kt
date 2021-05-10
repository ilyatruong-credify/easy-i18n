package com.yuukaze.i18next

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.yuukaze.i18next.service.getEasyI18nReferenceService

class EasyI18nStartupTask : StartupActivity.Background {
    override fun runActivity(project: Project) {
        project.getEasyI18nReferenceService().processAll()
    }
}
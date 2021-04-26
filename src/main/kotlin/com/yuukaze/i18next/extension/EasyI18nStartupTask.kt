package com.yuukaze.i18next.extension

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.yuukaze.i18next.service.getEasyI18nReferenceService
import com.yuukaze.i18next.service.getEasyI18nService

class EasyI18nStartupTask : StartupActivity.Background {
  override fun runActivity(project: Project) {
    project.getEasyI18nService().dataStore.reloadFromDisk()
    project.getEasyI18nReferenceService().processAll()
  }
}
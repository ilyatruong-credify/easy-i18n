package com.yuukaze.i18next.ui.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.data.reloadI18nData
import com.yuukaze.i18next.service.Notifier
import java.util.*

/**
 * Reload translations action.
 */
class ReloadAction : AnAction(
  ResourceBundle.getBundle("messages").getString("action.reload"),
  null, AllIcons.Actions.Refresh
) {
  override fun actionPerformed(e: AnActionEvent) {
    i18nStore.dispatch(reloadI18nData())
    Notifier.notifySuccess(e.project, "Reload from disk success")
  }
}
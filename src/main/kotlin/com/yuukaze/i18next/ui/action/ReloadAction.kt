package com.yuukaze.i18next.ui.action

import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.service.Notifier
import java.util.*

/**
 * Reload translations action.
 *
 * @author marhali
 */
class ReloadAction : AnAction(
  ResourceBundle.getBundle("messages").getString("action.reload"),
  null, AllIcons.Actions.Refresh
) {
  override fun actionPerformed(e: AnActionEvent) {
    //TODO
    val files = FileTypeIndex.getFiles(
      TypeScriptJSXFileType.INSTANCE,
      GlobalSearchScope.projectScope(e.project!!)
    )
    DataStore.getInstance(e.project!!).reloadFromDisk()
    Notifier.notifySuccess(e.project, "Reload from disk success")
  }
}
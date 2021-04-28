package com.yuukaze.i18next

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.yuukaze.i18next.service.WindowManager
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.I18nToolWindow
import com.yuukaze.i18next.ui.Icons
import com.yuukaze.i18next.ui.action.*
import java.util.*

/**
 * Tool window factory which will represent the entire ui for this plugin.
 */
class TranslatorToolWindowFactory : ToolWindowFactory {
  override fun init(toolWindow: ToolWindow) {
    toolWindow.setIcon(Icons.ToolWindowIcon)
  }

  override fun createToolWindowContent(
    project: Project,
    toolWindow: ToolWindow
  ) {
    val contentFactory = ContentFactory.SERVICE.getInstance()

    // Translations table view
    val toolWindowComponent = I18nToolWindow(project)
    val tableContent = contentFactory.createContent(
      toolWindowComponent.rootPanel,
      ResourceBundle.getBundle("messages").getString("view.table.title"), false
    )
    toolWindow.contentManager.addContent(tableContent)
    val settings = project.getEasyI18nService().state
    val spreadsheetId = settings.spreadSheetId
    val hasSpreadsheetId = spreadsheetId.isNotEmpty()

    // ToolWindow Actions (Can be used for every view)
    val actions: MutableList<AnAction> = ArrayList()
    if (hasSpreadsheetId) {
      actions.add(SpreadsheetUploadAction())
      actions.add(SpreadsheetUpdateAction())
    }
    actions.add(AddAction())
    actions.add(ReloadAction())
    actions.add(SettingsAction())
    toolWindow.setTitleActions(actions)

    // Initialize Window Manager
    WindowManager.getInstance()
      .initialize(toolWindow, toolWindowComponent.tableView)

    // Initialize data store and load from disk
    val store = project.getEasyI18nService().dataStore
    store.addSynchronizer(toolWindowComponent.tableView)
    store.addSynchronizer(toolWindowComponent.rootKeyTreeView)
    store.reloadFromDisk()
  }
}
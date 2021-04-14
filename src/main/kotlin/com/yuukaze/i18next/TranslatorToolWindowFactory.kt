package com.yuukaze.i18next

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.service.WindowManager
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.Icons
import com.yuukaze.i18next.ui.action.*
import com.yuukaze.i18next.ui.tabs.TableView
import java.util.*

/**
 * Tool window factory which will represent the entire ui for this plugin.
 *
 * @author marhali
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
    val tableView = TableView(project)
    val tableContent = contentFactory.createContent(
      tableView.rootPanel,
      ResourceBundle.getBundle("messages").getString("view.table.title"), false
    )
    toolWindow.contentManager.addContent(tableContent)
    val settings = project.getEasyI18nService().state
    val spreadsheetId = settings.spreadSheetId
    val hasSpreadsheetId = spreadsheetId != null && spreadsheetId.isNotEmpty()

    // ToolWindow Actions (Can be used for every view)
    val actions: MutableList<AnAction> = ArrayList()
    if (hasSpreadsheetId) {
      actions.add(SpreadsheetUploadAction())
      actions.add(SpreadsheetUpdateAction())
    }
    actions.add(AddAction())
    actions.add(ReloadAction())
    actions.add(SettingsAction())
    actions.add(SearchAction { searchString: String? ->
      DataStore.getInstance(
        project
      ).searchByKey(searchString)
    })
    toolWindow.setTitleActions(actions)

    // Initialize Window Manager
    WindowManager.getInstance().initialize(toolWindow, tableView)

    // Initialize data store and load from disk
    val store = DataStore.getInstance(project)
    store.addSynchronizer(tableView)
    store.addSynchronizer(tableView.rootKeyTree)
    store.reloadFromDisk()
  }
}
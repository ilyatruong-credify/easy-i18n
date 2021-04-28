package com.yuukaze.i18next

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.service.WindowManager
import com.yuukaze.i18next.service.getEasyI18nService
import com.yuukaze.i18next.ui.Icons
import com.yuukaze.i18next.ui.RootKeyTreeView
import com.yuukaze.i18next.ui.SearchKeyView
import com.yuukaze.i18next.ui.TableView
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
    val tableView = TableView(project)
    val searchKeyView = SearchKeyView { searchString: String? ->
      DataStore.getInstance(
        project
      ).searchByKey(searchString)
    }
    val rootKeyTreeView = RootKeyTreeView(project)
    val tableContent = contentFactory.createContent(
      OnePixelSplitter(false, 0.3f).apply {
        firstComponent = BorderLayoutPanel().apply {
          border = JBUI.Borders.empty()
          addToTop(BorderLayoutPanel().apply {
            border = IdeBorderFactory.createBorder(SideBorder.BOTTOM)
            addToCenter(NonOpaquePanel(searchKeyView.component))
          })
          addToCenter(rootKeyTreeView.component)
        }
        secondComponent = tableView.component
      },
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
    WindowManager.getInstance().initialize(toolWindow, tableView)

    // Initialize data store and load from disk
    val store = project.getEasyI18nService().dataStore
    store.addSynchronizer(tableView)
    store.addSynchronizer(rootKeyTreeView)
    store.reloadFromDisk()
  }
}
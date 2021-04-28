package com.yuukaze.i18next.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import com.yuukaze.i18next.service.DataStore

class I18nToolWindow(project: Project) {
  val tableView = TableView(project)
  val searchKeyView = SearchKeyView { searchString: String? ->
    DataStore.getInstance(
      project
    ).searchByKey(searchString)
  }
  val rootKeyTreeView = RootKeyTreeView(project)

  val rootPanel
    get() = OnePixelSplitter(false, 0.3f).apply {
      firstComponent = BorderLayoutPanel().apply {
        border = JBUI.Borders.empty()
        addToTop(BorderLayoutPanel().apply {
          border = IdeBorderFactory.createBorder(SideBorder.BOTTOM)
          addToCenter(NonOpaquePanel(searchKeyView.component))
        })
        addToCenter(rootKeyTreeView.component)
      }
      secondComponent = tableView.component
    }
}
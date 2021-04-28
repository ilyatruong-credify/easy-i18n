package com.yuukaze.i18next.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.project.Project
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.ui.action.ReloadAction
import com.yuukaze.i18next.utils.border

class I18nToolWindow(project: Project) {
  val tableView = TableView(project)
  val searchKeyView = SearchKeyView()
  val rootKeyTreeView = RootKeyTreeView(project)

  val toolbar = ActionManager.getInstance()
    .createActionToolbar(
      ActionPlaces.UNKNOWN, DefaultActionGroup(
        ReloadAction(),
        Separator.getInstance()
      ), false
    )

  val rootPanel
    get() = border {
      addToLeft(border {
        border = IdeBorderFactory.createBorder(SideBorder.RIGHT)
        addToCenter(toolbar.component.apply {
          border = JBUI.Borders.empty(2)
        })
      })
      addToCenter(OnePixelSplitter(false, 0.25f).apply {
        firstComponent = border {
          border = JBUI.Borders.empty()
          addToTop(border {
            border = IdeBorderFactory.createBorder(SideBorder.BOTTOM)
            addToCenter(NonOpaquePanel(searchKeyView.component))
          })
          addToCenter(rootKeyTreeView.component)
        }
        secondComponent = tableView.component
      })
    }
}
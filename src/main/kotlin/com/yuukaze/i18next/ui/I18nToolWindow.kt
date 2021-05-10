package com.yuukaze.i18next.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SideBorder
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.data.I18nReduxSelectors
import com.yuukaze.i18next.ui.action.ReloadAction
import com.yuukaze.i18next.ui.action.TableDataFilterViewAction
import com.yuukaze.i18next.utils.border
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class I18nToolWindow(project: Project, val toolWindow: ToolWindow) {
  val tableView by lazy { TableView(project) }
  private val searchKeyView = SearchKeyView()
  private val rootKeyTreeView by lazy { RootKeyTreeView(project) }

  private val toolbar = ActionManager.getInstance()
    .createActionToolbar(
      ActionPlaces.UNKNOWN, DefaultActionGroup(
        ReloadAction(),
        Separator.getInstance(),
        TableDataFilterViewAction()
      ), false
    )

  val rootPanel by lazy {
    border {
      left += border {
        border = IdeBorderFactory.createBorder(SideBorder.RIGHT)
        addToCenter(toolbar.component.apply {
          border = JBUI.Borders.empty(2)
        })
      }
      center += OnePixelSplitter(false, 0.25f).apply {
        firstComponent = border {
          border = JBUI.Borders.empty()
          top += border {
            border = IdeBorderFactory.createBorder(SideBorder.BOTTOM)
            addToCenter(NonOpaquePanel(searchKeyView.component))
          }
          center += rootKeyTreeView.component
        }
        I18nReduxSelectors.filteredTranslations {
          if (it !== null)
            secondComponent = tableView.component
        }
      }
    }
  }
}
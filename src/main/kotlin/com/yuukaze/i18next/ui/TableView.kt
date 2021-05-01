package com.yuukaze.i18next.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.data.I18nReduxSelectors
import com.yuukaze.i18next.model.TableModelTranslator
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.ui.dialog.MigrateDialog
import com.yuukaze.i18next.ui.listener.DoubleClickListener
import com.yuukaze.i18next.ui.model.FilterUntranslatedModel
import com.yuukaze.i18next.ui.renderer.CustomTableCellRenderer
import com.yuukaze.i18next.ui.renderer.CustomTableHeaderCellRenderer
import com.yuukaze.i18next.utils.JComponentWrapper
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.Action

/**
 * Shows translation state as table.
 */
class TableView(private val project: Project?) :
  JComponentWrapper<JBScrollPane> {
  val table: JBTable = JBTable().apply {
    border = JBUI.Borders.empty()
    emptyText.text =
      ResourceBundle.getBundle("messages").getString("view.empty")
  }
  private val filterUntranslated = FilterUntranslatedModel()
  private val popupMenu = JBPopupMenu().let {
    it.add(JBMenuItem(TableViewAction("Edit...", this::handleEdit)))
    it.add(JBMenuItem(TableViewAction("Migrate...", this::handleMigrate)))
    it.add(JBMenuItem(TableViewAction("Delete", this::handleDelete)))
    it
  }

  init {
    table.componentPopupMenu = popupMenu
    table.setDefaultRenderer(
      String::class.java,
      CustomTableCellRenderer(project!!)
    )
    setupTableHeader()
    I18nReduxSelectors.filteredTranslations {
      table.model = TableModelTranslator(it!!)
    }
  }

  private fun setupTableHeader() {
    val header = table.tableHeader
    header.reorderingAllowed = false
    header.defaultRenderer =
      CustomTableHeaderCellRenderer(header, filterUntranslated)
    header.addMouseListener(DoubleClickListener { e: MouseEvent ->
      handleFilter(
        e
      )
    })
  }

  private fun handleFilter(e: MouseEvent) {
    val col = table.columnAtPoint(e.point)
    val colName = table.getColumnName(col)
    if (col >= 0 && filterUntranslated.available.contains(colName)) {
      val current = filterUntranslated.toggle
      filterUntranslated.toggle = if (current == colName) null else colName
    }
  }

  private fun handleEdit(row: Int) {
    //TODO implement
  }

  private fun handleMigrate(row: Int) {
    MigrateDialog(project!!).showAndHandle()
  }

  private fun handleDelete(row: Int) {
    val key = table.getValueAt(row, 0).toString()
    JBPopupFactory.getInstance()
      .createConfirmation(
        "Delete key $key?", "Yes", "No",
        {
          val dataStore = project.getEasyI18nDataStore()
          dataStore.translations.nodes.removeChildren(key)
          dataStore.doSync()
        }, 0
      ).showCenteredInCurrentWindow(project!!)
  }

  inner class TableViewAction(
    private val name: String,
    val callback: (row: Int) -> Unit
  ) :
    Action {
    override fun actionPerformed(e: ActionEvent?) {
      callback(table.selectedRow)
    }

    override fun getValue(key: String?): Any? = when (key) {
      Action.NAME -> name
      Action.MNEMONIC_KEY -> key[0].toInt()
      else -> null
    }

    override fun putValue(key: String?, value: Any?) {}

    override fun setEnabled(b: Boolean) {}

    override fun isEnabled(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener?) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener?) {}
  }

  override val component: JBScrollPane
    get() = JBScrollPane(table).apply {
      border = JBUI.Borders.empty(1, 1, 0, 0)
    }
}
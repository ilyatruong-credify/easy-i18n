package com.yuukaze.i18next.ui.tabs

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.border.CustomLineBorder
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.panel
import com.intellij.ui.table.JBTable
import com.yuukaze.i18next.model.*
import com.yuukaze.i18next.model.table.TableModelTranslator
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.service.getEasyI18nDataStore
import com.yuukaze.i18next.ui.components.RootKeyTree
import com.yuukaze.i18next.ui.dialog.MigrateDialog
import com.yuukaze.i18next.ui.listener.DeleteKeyListener
import com.yuukaze.i18next.ui.listener.DoubleClickListener
import com.yuukaze.i18next.ui.model.FilterUntranslatedModel
import com.yuukaze.i18next.ui.renderer.CustomTableCellRenderer
import com.yuukaze.i18next.ui.renderer.CustomTableHeaderCellRenderer
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.Action
import javax.swing.JPanel

/**
 * Shows translation state as table.
 */
class TableView(private val project: Project?) : DataSynchronizer {
  val rootPanel: JPanel
    get() = panel {
      row {
        scrollPane(rootKeyTree)
        scrollPane(table).constraints(pushX)
      }
    }
  val rootKeyTree: RootKeyTree
  val table: JBTable = JBTable()
  private val filterUntranslated = FilterUntranslatedModel()
  private val popupMenu = JBPopupMenu().let {
    it.add(JBMenuItem(TableViewAction("Edit...", this::handleEdit)))
    it.add(JBMenuItem(TableViewAction("Migrate...", this::handleMigrate)))
    it.add(JBMenuItem(TableViewAction("Delete", this::handleDelete)))
    it
  }

  init {
    table.emptyText.text =
      ResourceBundle.getBundle("messages").getString("view.empty")
    table.addKeyListener(DeleteKeyListener(handleDeleteKey()))
    table.setDefaultRenderer(
      String::class.java,
      CustomTableCellRenderer(project!!)
    )
    table.componentPopupMenu = popupMenu
    setupTableHeader()
    val scrollPane = JBScrollPane(table)
    scrollPane.border = CustomLineBorder(JBColor.border(), 0, 1, 0, 0)
    rootKeyTree = RootKeyTree(project)
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

  private fun handleDeleteKey(): Runnable {
    return Runnable {
      for (selectedRow in table.selectedRows) {
        val fullPath = table.getValueAt(selectedRow, 0).toString()
        DataStore.getInstance(project).processUpdate(
          TranslationDelete(KeyedTranslation(fullPath, null))
        )
      }
    }
  }

  override fun synchronize(translations: Translations, searchQuery: String?) {
    table.model = TableModelTranslator(
      translations,
      searchQuery
    ) { update: TranslationUpdate? ->
      DataStore.getInstance(
        project
      ).processUpdate(update)
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
}
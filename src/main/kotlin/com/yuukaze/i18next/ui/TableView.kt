package com.yuukaze.i18next.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.data.I18nReduxSelectors
import com.yuukaze.i18next.data.deleteI18nTranslation
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.model.TableModelTranslator
import com.yuukaze.i18next.ui.action.ChangeKeyAction
import com.yuukaze.i18next.ui.action.DuplicateAction
import com.yuukaze.i18next.ui.dialog.MigrateDialog
import com.yuukaze.i18next.ui.listener.DoubleClickListener
import com.yuukaze.i18next.ui.model.FilterUntranslatedModel
import com.yuukaze.i18next.ui.renderer.CustomTableCellRenderer
import com.yuukaze.i18next.ui.renderer.CustomTableHeaderCellRenderer
import com.yuukaze.i18next.utils.JComponentWrapper
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.*
import javax.swing.JComponent.WHEN_FOCUSED
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener


/**
 * Shows translation state as table.
 */
class TableView(private val project: Project?) :
    JComponentWrapper<JBScrollPane> {
    private val actionDelete = TableViewAction("Delete", this::handleDelete)
    val table: JBTable = JBTable().apply {
        border = JBUI.Borders.empty()
        emptyText.text =
            ResourceBundle.getBundle("messages").getString("view.empty")
        handleDeleteKey(actionDelete)
    }
    private val filterUntranslated = FilterUntranslatedModel()
    private val popupMenu by lazy {
        val duplicate = DuplicateAction(table)
        JBPopupMenu().let {
            it.add(JBMenuItem(TableViewAction("Edit...", this::handleEdit)))
            it.add(JBMenuItem(ChangeKeyAction(table)))
            it.add(JBMenuItem(duplicate))
            it.add(JBMenuItem(TableViewAction("Migrate...", this::handleMigrate)))
            it.add(JBMenuItem(actionDelete))
            it.addPopupMenuListener(object : PopupMenuListener {
                override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                    duplicate.location = it.location
                    duplicate.key = table.getValueAt(table.selectedRow, 0) as String
                }

                override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent?) {
                }

                override fun popupMenuCanceled(e: PopupMenuEvent?) {
                }

            })
            it
        }
    }

    init {
        table.apply {
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent?) {
                    val currentRow = table.rowAtPoint(e!!.point)
                    table.setRowSelectionInterval(currentRow, currentRow)
                    val rowindex = table.selectedRow;
                    if (rowindex < 0)
                        return;
                    if (e.isPopupTrigger && e.component is JTable) {
//            popupMenu.location
                        popupMenu.show(e.component, e.x, e.y)
                    }
                }
            })
            setDefaultRenderer(
                String::class.java,
                CustomTableCellRenderer(project!!)
            )
        }
        setupTableHeader()
        I18nReduxSelectors.filteredTranslations {
            table.model = TableModelTranslator(it!!)
        }
        I18nReduxSelectors.onSelectionChanged {
            if (it != null)
                scrollToSpecificKey(it.key)
        }
    }

    private fun scrollToSpecificKey(key: String) {
        val rowIndex =
            (table.model as TableModelTranslator).getTranslationKeyRowIndex(key)
        val rect = table.getCellRect(
            rowIndex,
            0,
            true
        )
        table.scrollRectToVisible(rect)
        table.setRowSelectionInterval(rowIndex, rowIndex)
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
                    i18nStore.deleteI18nTranslation(key)
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

fun JBTable.handleDeleteKey(action: Action) {
    val table = this
    val inputMap: InputMap = table.getInputMap(WHEN_FOCUSED)
    val actionMap: ActionMap = table.actionMap

    val deleteAction = "delete"
    inputMap.put(
        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
        deleteAction
    )
    actionMap.put(deleteAction, action)
}
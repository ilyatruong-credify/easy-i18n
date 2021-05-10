package com.yuukaze.i18next.ui.renderer

import com.intellij.icons.AllIcons
import com.yuukaze.i18next.ui.model.FilterUntranslatedModel
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.JTableHeader

class CustomTableHeaderCellRenderer(
    private val headers: JTableHeader,
    private val filter: FilterUntranslatedModel
) :
    DefaultTableCellRenderer() {
    init {
        filter.addChangeListener { headers.repaint() }
    }

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        super.getTableCellRendererComponent(
            table,
            value,
            isSelected,
            hasFocus,
            row,
            column
        )
        if (column > 0 && untranslatedEntry(column, table!!)) {
            val label = value!! as String
            icon =
                if (label == filter.toggle) OpaqueIcon(AllIcons.General.ShowWarning) else AllIcons.General.Warning
            filter.available.add(table.getColumnName(column))
        } else icon = null
        return this
    }

    private fun untranslatedEntry(col: Int, table: JTable): Boolean {
        val rowCount = table.rowCount
        for (i in 0 until rowCount) {
            val value = table.getValueAt(i, col)
            if (value == null || value.toString().isEmpty()) {
                return true
            }
        }
        return false
    }
}
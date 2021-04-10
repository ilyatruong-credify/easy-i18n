package com.yuukaze.i18next.ui.renderer

import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * Similar to [DefaultTableCellRenderer] but will mark the first column red if any column is empty.
 * @author marhali
 */
class CustomTableCellRenderer : DefaultTableCellRenderer() {
  override fun getTableCellRendererComponent(
    table: JTable?,
    value: Any?,
    isSelected: Boolean,
    hasFocus: Boolean,
    row: Int,
    column: Int
  ): Component {
    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    if (column == 0 && missesValues(row, table!!)) {
      component.foreground = JBColor.RED
    } else { // Reset color
      component.foreground = null
    }
    return component
  }

  private fun missesValues(row: Int, table: JTable): Boolean {
    val columns = table.columnCount
    for (i in 1 until columns) {
      val value = table.getValueAt(row, i)
      if (value == null || value.toString().isEmpty()) {
        return true
      }
    }
    return false
  }
}
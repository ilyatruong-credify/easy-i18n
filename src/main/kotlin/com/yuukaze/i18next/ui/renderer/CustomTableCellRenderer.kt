package com.yuukaze.i18next.ui.renderer

import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

private object DetectRegexes {
  val variable = Regex("\\{\\{[a-z]+}}")
  val containsHTML =
    Regex("</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>");
}

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
    val component = super.getTableCellRendererComponent(
      table,
      value,
      isSelected,
      hasFocus,
      row,
      column
    ) as JLabel
    if (column == 0) {
      val icon = ListIcon(
        listOf(
          detectValuableEntry(row, table!!),
          detectContainsHTMLEntry(row, table!!)
        )
      )
      component.icon = icon
    } else component.icon = null
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

  private fun detectValuableEntry(row: Int, table: JTable): Icon {
    val value = table.getValueAt(row, 1) as String
    return if (DetectRegexes.variable.containsMatchIn(value)) AllIcons.Nodes.Variable else AllIcons.Nodes.EmptyNode
  }

  private fun detectContainsHTMLEntry(row: Int, table: JTable): Icon {
    val value = table.getValueAt(row, 1) as String
    return if (DetectRegexes.containsHTML.containsMatchIn(value)) AllIcons.Xml.Html_id else AllIcons.Nodes.EmptyNode
  }
}
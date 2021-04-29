package com.yuukaze.i18next.model

import com.intellij.openapi.util.Pair
import com.yuukaze.i18next.data.UpdateTranslation
import com.yuukaze.i18next.data.i18nStore
import org.jetbrains.annotations.Nls
import java.util.stream.Collectors
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

/**
 * Table model to represents localized messages.
 */
class TableModelTranslator(
  private val translations: Translations
) : TableModel {
  private val locales: List<String> = translations.locales
  private val fullKeys: List<String>
  override fun getRowCount(): Int {
    return fullKeys.size
  }

  override fun getColumnCount(): Int {
    return locales.size + 1 // Number of locales plus 1 for the Key's column
  }

  override fun getColumnName(columnIndex: Int): @Nls String? {
    return if (columnIndex == 0) {
      "<html><b>Key</b></html>"
    } else "<html><b>" + locales[columnIndex - 1] + "</b></html>"
  }

  override fun getColumnClass(columnIndex: Int): Class<*> {
    return String::class.java
  }

  override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
    return rowIndex > 0 // Everything should be editable except the headline
  }

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    if (columnIndex == 0) { // Keys
      return fullKeys[rowIndex]
    }
    val key = fullKeys[rowIndex]
    val locale = locales[columnIndex - 1]
    val node = translations.getNode(key)
    return node?.value?.get(locale) ?: ""
  }

  override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
    val key = getValueAt(rowIndex, 0).toString()
    val node = translations.getNode(key)
      ?: // Unknown cell
      return
    val newKey = if (columnIndex == 0) aValue.toString() else key
    val messages = node.value

    // Locale message update
    if (columnIndex > 0) {
      if ((aValue as String).isEmpty()) {
        messages.remove(locales[columnIndex - 1])
      } else {
        messages[locales[columnIndex - 1]] = aValue.toString()
      }
    }
    i18nStore.dispatch(
      UpdateTranslation(
        KeyedTranslation(key, messages),
        KeyedTranslation(newKey, messages)
      )
    )
  }

  override fun addTableModelListener(l: TableModelListener) {}
  override fun removeTableModelListener(l: TableModelListener) {}

  init {
    val fullKeys = translations.fullKeys
    this.fullKeys =
      fullKeys.stream().map { i: Pair<String, String?> -> i.first }
        .collect(Collectors.toList())
  }
}
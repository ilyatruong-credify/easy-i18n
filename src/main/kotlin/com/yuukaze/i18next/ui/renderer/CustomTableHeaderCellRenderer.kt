package com.yuukaze.i18next.ui.renderer

import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.popup.ActiveIcon
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class CustomTableHeaderCellRenderer : DefaultTableCellRenderer() {
    private val missingActionIconButton = ActiveIcon(OpaqueIcon(AllIcons.General.ShowWarning), AllIcons.General.Warning)

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val component: JLabel =
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel;
        if (column > 0)
            component.icon = missingActionIconButton
        return component;
    }
}
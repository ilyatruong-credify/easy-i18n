package com.yuukaze.i18next.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.data.I18nReduxSelectors
import com.yuukaze.i18next.model.table.RootKeyTreeModel
import com.yuukaze.i18next.utils.JComponentWrapper
import java.util.*
import javax.swing.JTree
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode

class RootKeyTreeView(private val project: Project) :
  JComponentWrapper<JBScrollPane> {
  private val tree = Tree().apply {
    cellRenderer = object : ColoredTreeCellRenderer() {
      override fun customizeCellRenderer(
        tree: JTree,
        value: Any,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
      ) {
        val node = value as DefaultMutableTreeNode
        var text = ObjectUtils.tryCast(node.userObject, String::class.java)
        text = StringUtil.notNullize(text, "")
        this.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES)
      }
    }
    emptyText.text =
      ResourceBundle.getBundle("messages").getString("view.empty")
  }

  init {
    I18nReduxSelectors.filteredTranslations {
      tree.model = RootKeyTreeModel(project, it)
    }
  }

  private class TreeClickListener : TreeSelectionListener {
    override fun valueChanged(e: TreeSelectionEvent) {}
  }

  override val component: JBScrollPane
    get() = JBScrollPane(tree).apply {
      border = JBUI.Borders.empty(1, 0, 0, 0)
    }
}
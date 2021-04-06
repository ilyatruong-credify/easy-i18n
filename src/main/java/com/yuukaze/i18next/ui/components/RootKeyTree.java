package com.yuukaze.i18next.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ObjectUtils;
import com.yuukaze.i18next.model.DataSynchronizer;
import com.yuukaze.i18next.model.Translations;
import com.yuukaze.i18next.model.table.RootKeyTreeModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ResourceBundle;

public class RootKeyTree extends Tree implements DataSynchronizer {
    private Project project;

    public RootKeyTree(Project project) {
        super();
        this.project = project;
        setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                this.setIcon(AllIcons.Nodes.C_plocal);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String text = (String) ObjectUtils.tryCast(node.getUserObject(), String.class);
                text = StringUtil.notNullize(text, "");
                this.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });
        getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
//        setRootVisible(false);
    }

    @Override
    public void synchronize(@NotNull Translations translations, @Nullable String searchQuery) {
        setModel(new RootKeyTreeModel(project, translations));
    }

    private static final class TreeClickListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {

        }
    }
}

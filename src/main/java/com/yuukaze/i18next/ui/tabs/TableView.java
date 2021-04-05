package com.yuukaze.i18next.ui.tabs;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ObjectUtils;
import com.yuukaze.i18next.model.*;
import com.yuukaze.i18next.model.table.RootKeyTreeModel;
import com.yuukaze.i18next.model.table.TableModelTranslator;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.ui.dialog.EditDialog;
import com.yuukaze.i18next.ui.listener.DeleteKeyListener;
import com.yuukaze.i18next.ui.listener.PopupClickListener;
import com.yuukaze.i18next.ui.renderer.TableRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

/**
 * Shows translation state as table.
 *
 * @author marhali
 */
public class TableView implements DataSynchronizer {

    private final Project project;

    private JPanel rootPanel;
    private JPanel containerPanel;
    private Tree rootKeyTree;

    private final JBTable table;

    public TableView(Project project) {
        this.project = project;

        table = new JBTable();
        table.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        table.addMouseListener(new PopupClickListener(this::handlePopup));
        table.addKeyListener(new DeleteKeyListener(handleDeleteKey()));
        table.setDefaultRenderer(String.class, new TableRenderer());

        rootKeyTree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                this.setIcon(AllIcons.Nodes.C_plocal);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String text = (String) ObjectUtils.tryCast(node.getUserObject(), String.class);
                text = StringUtil.notNullize(text, "");
                this.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });
        rootKeyTree.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        rootKeyTree.setRootVisible(false);

        JBScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setBorder(new CustomLineBorder(JBColor.border(), 0, 1, 0, 0));
        containerPanel.add(scrollPane);
    }

    private void handlePopup(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());

        if (row >= 0) {
            String fullPath = String.valueOf(table.getValueAt(row, 0));
            LocalizedNode node = DataStore.getInstance(project).getTranslations().getNode(fullPath);

            if (node != null) {
                new EditDialog(project, new KeyedTranslation(fullPath, node.getValue())).showAndHandle();
            }
        }
    }

    private Runnable handleDeleteKey() {
        return () -> {
            for (int selectedRow : table.getSelectedRows()) {
                String fullPath = String.valueOf(table.getValueAt(selectedRow, 0));

                DataStore.getInstance(project).processUpdate(
                        new TranslationDelete(new KeyedTranslation(fullPath, null)));
            }
        };
    }

    @Override
    public void synchronize(@NotNull Translations translations, @Nullable String searchQuery) {
        table.setModel(new TableModelTranslator(translations, searchQuery, update ->
                DataStore.getInstance(project).processUpdate(update)));
        rootKeyTree.setModel(new RootKeyTreeModel(project, translations));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JBTable getTable() {
        return table;
    }
}
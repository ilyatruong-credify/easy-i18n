package com.yuukaze.i18next.ui.tabs;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.yuukaze.i18next.model.*;
import com.yuukaze.i18next.model.table.TableModelTranslator;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.ui.components.RootKeyTree;
import com.yuukaze.i18next.ui.dialog.EditDialog;
import com.yuukaze.i18next.ui.listener.DeleteKeyListener;
import com.yuukaze.i18next.ui.listener.PopupClickListener;
import com.yuukaze.i18next.ui.renderer.CustomTableHeaderCellRenderer;
import com.yuukaze.i18next.ui.renderer.CustomTableCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
    private JScrollPane keyTreePane;
    private final RootKeyTree rootKeyTree;
    private final JBTable table;

    public TableView(Project project) {
        this.project = project;

        table = new JBTable();
        table.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        table.addMouseListener(new PopupClickListener(this::handlePopup));
        table.addKeyListener(new DeleteKeyListener(handleDeleteKey()));
        table.setDefaultRenderer(String.class, new CustomTableCellRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new CustomTableHeaderCellRenderer());

        JBScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setBorder(new CustomLineBorder(JBColor.border(), 0, 1, 0, 0));
        containerPanel.add(scrollPane);

        rootKeyTree = new RootKeyTree(project);
        keyTreePane.setViewportView(rootKeyTree);
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
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JBTable getTable() {
        return table;
    }

    public RootKeyTree getRootKeyTree() {
        return rootKeyTree;
    }
}
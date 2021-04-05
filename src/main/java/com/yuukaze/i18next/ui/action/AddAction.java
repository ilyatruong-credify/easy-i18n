package com.yuukaze.i18next.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yuukaze.i18next.service.WindowManager;
import com.yuukaze.i18next.ui.dialog.AddDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Add translation action.
 *
 * @author phong.truonghung
 */
public class AddAction extends AnAction {

    public AddAction() {
        super(ResourceBundle.getBundle("messages").getString("action.add"),
                null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new AddDialog(e.getProject(), detectPreKey()).showAndHandle();
    }

    private String detectPreKey() {
        WindowManager manager = WindowManager.getInstance();

        if (manager == null) {
            return null;
        }
        int row = manager.getTableView().getTable().getSelectedRow();

        if (row >= 0) {
            String fullPath = String.valueOf(manager.getTableView().getTable().getValueAt(row, 0));
            int pos = fullPath.lastIndexOf(".");
            return fullPath.substring(0, pos + 1);
        }

        return null;
    }
}
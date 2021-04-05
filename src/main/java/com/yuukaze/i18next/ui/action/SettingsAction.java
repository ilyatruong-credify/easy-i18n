package com.yuukaze.i18next.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yuukaze.i18next.ui.dialog.SettingsDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Plugin settings action.
 * @author marhali
 */
public class SettingsAction extends AnAction {

    public SettingsAction() {
        super(ResourceBundle.getBundle("messages").getString("action.settings"),
                null, AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SettingsDialog(e.getProject()).showAndHandle();
    }
}
package com.yuukaze.i18next.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.service.Notifier;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Reload translations action.
 *
 * @author marhali
 */
public class ReloadAction extends AnAction {

    public ReloadAction() {
        super(ResourceBundle.getBundle("messages").getString("action.reload"),
                null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataStore.getInstance(e.getProject()).reloadFromDisk();
        Notifier.notifySuccess(e.getProject(), "Reload from disk success");
    }
}
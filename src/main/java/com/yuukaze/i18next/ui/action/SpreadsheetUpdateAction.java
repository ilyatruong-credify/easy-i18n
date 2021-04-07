package com.yuukaze.i18next.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SpreadsheetUpdateAction extends AnAction {
    public SpreadsheetUpdateAction(){
        super("Update from Spreadsheet",null, AllIcons.Actions.Download);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    private static final class Executor{
        public Executor(Project project){}
    }
}

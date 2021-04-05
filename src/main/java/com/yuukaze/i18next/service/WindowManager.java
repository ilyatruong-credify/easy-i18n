package com.yuukaze.i18next.service;

import com.intellij.openapi.wm.ToolWindow;
import com.yuukaze.i18next.ui.tabs.TableView;

public class WindowManager {

    private static WindowManager INSTANCE;

    private ToolWindow toolWindow;
    private TableView tableView;

    public static WindowManager getInstance() {
        return INSTANCE == null ? INSTANCE = new WindowManager() : INSTANCE;
    }

    private WindowManager() {}

    public void initialize(ToolWindow toolWindow, TableView tableView) {
        this.toolWindow = toolWindow;
        this.tableView = tableView;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public TableView getTableView() {
        return tableView;
    }
}
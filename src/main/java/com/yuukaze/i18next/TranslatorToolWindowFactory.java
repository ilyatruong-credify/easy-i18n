package com.yuukaze.i18next;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.yuukaze.i18next.model.SettingsState;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.service.SettingsService;
import com.yuukaze.i18next.service.WindowManager;
import com.yuukaze.i18next.ui.action.*;
import com.yuukaze.i18next.ui.tabs.TableView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Tool window factory which will represent the entire ui for this plugin.
 *
 * @author marhali
 */
public class TranslatorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // Translations table view
        TableView tableView = new TableView(project);
        Content tableContent = contentFactory.createContent(tableView.getRootPanel(),
                ResourceBundle.getBundle("messages").getString("view.table.title"), false);

        toolWindow.getContentManager().addContent(tableContent);

        SettingsState settings = SettingsService.getInstance(project).getState();
        String spreadsheetId = settings.getSpreadSheetId();
        boolean hasSpreadsheetId = spreadsheetId != null && spreadsheetId.length() > 0;

        // ToolWindow Actions (Can be used for every view)
        List<AnAction> actions = new ArrayList<>();
        if (hasSpreadsheetId) {
            actions.add(new SpreadsheetUploadAction());
            actions.add(new SpreadsheetUpdateAction());
        }
        actions.add(new AddAction());
        actions.add(new ReloadAction());
        actions.add(new SettingsAction());
        actions.add(new SearchAction((searchString) -> DataStore.getInstance(project).searchByKey(searchString)));
        toolWindow.setTitleActions(actions);

        // Initialize Window Manager
        WindowManager.getInstance().initialize(toolWindow, tableView);

        // Initialize data store and load from disk
        DataStore store = DataStore.getInstance(project);
        store.addSynchronizer(tableView);
        store.addSynchronizer(tableView.getRootKeyTree());

        store.reloadFromDisk();
    }
}
package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.yuukaze.i18next.model.SettingsState;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.service.SettingsService;

import java.util.ResourceBundle;

/**
 * Plugin configuration dialog.
 *
 * @author marhali
 */
public class SettingsDialog {

    private final Project project;

    private SettingsForm settingsForm;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        SettingsState state = SettingsService.getInstance(project).getState();

        if (prepare(state).show() == DialogWrapper.OK_EXIT_CODE) {
            settingsForm.pushDataIntoState(state);

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(SettingsState state) {
        settingsForm = new SettingsForm();
        settingsForm.pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        settingsForm.fetchDataFromState(state);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(ResourceBundle.getBundle("messages").getString("action.settings"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(settingsForm.contentPane);

        return builder;
    }
}
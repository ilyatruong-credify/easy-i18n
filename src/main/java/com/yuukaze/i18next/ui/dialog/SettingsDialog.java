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
        String localesPath = state.getLocalesPath();
        String previewLocale = state.getPreviewLocale();
        boolean disableKeySeparator = state.isHasSeparator();
        String keySeparator = state.getKeySeparator();

        if (prepare(localesPath, previewLocale, disableKeySeparator, keySeparator).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            state.setLocalesPath(settingsForm.pathText.getText());
            state.setPreviewLocale(settingsForm.previewText.getText());
            state.setHasSeparator(settingsForm.getDisableKeySeparator());
            state.setKeySeparator(settingsForm.keySeparator.getText());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(String localesPath, String previewLocale, boolean disableKeySeparator, String keySeparator) {
        settingsForm = new SettingsForm();
        settingsForm.pathText.setText(localesPath);
        settingsForm.pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        settingsForm.previewText.setText(previewLocale);
        settingsForm.setDisableKeySeparator(disableKeySeparator);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(ResourceBundle.getBundle("messages").getString("action.settings"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(settingsForm.contentPane);

        return builder;
    }
}
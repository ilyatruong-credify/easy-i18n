package de.marhali.easyi18n.ui.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.service.DataStore;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Plugin configuration dialog.
 * @author marhali
 */
public class SettingsDialog {

    private final Project project;

    private SettingsForm settingsForm;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();

        if(prepare(localesPath, previewLocale).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            SettingsService.getInstance(project).getState().setLocalesPath(settingsForm.pathText.getText());
            SettingsService.getInstance(project).getState().setPreviewLocale(settingsForm.previewText.getText());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(String localesPath, String previewLocale) {
        settingsForm = new SettingsForm();
        settingsForm.pathText.setText(localesPath);
        settingsForm.pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        settingsForm.previewText.setText(previewLocale);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(ResourceBundle.getBundle("messages").getString("action.settings"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(settingsForm.contentPane);

        return builder;
    }
}
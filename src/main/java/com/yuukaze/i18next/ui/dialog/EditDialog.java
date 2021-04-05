package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.yuukaze.i18next.model.KeyedTranslation;
import com.yuukaze.i18next.model.TranslationDelete;
import com.yuukaze.i18next.model.TranslationUpdate;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.ui.components.LocaleDialogBase;
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Edit translation dialog.
 * @author marhali
 */
public class EditDialog extends LocaleDialogBase {
    private final KeyedTranslation origin;

    public EditDialog(Project project, KeyedTranslation origin) {
        super(project,ResourceBundle.getBundle("messages").getString("action.edit"));
        this.origin = origin;
    }

    public void showAndHandle() {
        int code = prepare().show();

        if(code == DialogWrapper.OK_EXIT_CODE) { // Edit
            DataStore.getInstance(project).processUpdate(new TranslationUpdate(origin, getChanges()));

        } else if(code == DeleteActionDescriptor.EXIT_CODE) { // Delete
            DataStore.getInstance(project).processUpdate(new TranslationDelete(origin));
        }
    }

    private KeyedTranslation getChanges() {
        Map<String, String> messages = new HashMap<>();

        valueTextFields.forEach((k, v) -> {
            if(!v.getText().isEmpty()) {
                messages.put(k, v.getText());
            }
        });

        return new KeyedTranslation(keyTextField.getText(), messages);
    }

    @Nullable
    @Override
    protected String getPreKey() {
        return this.origin.getKey();
    }

    @Nullable
    @Override
    protected String getTranslation(String locale) {
        return this.origin.getTranslations().get(locale);
    }
}
package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.yuukaze.i18next.model.KeyedTranslation;
import com.yuukaze.i18next.model.TranslationCreate;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.ui.components.LocaleDialogBase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Create translation dialog.
 *
 * @author marhali
 */
public class AddDialog extends LocaleDialogBase {
    private String preKey;

    public AddDialog(Project project, String preKey) {
        super(project,ResourceBundle.getBundle("messages").getString("action.add"));
        this.preKey = preKey;
    }

    @Nullable
    @Override
    protected String getPreKey() {
        return preKey;
    }

    @Nullable
    @Override
    protected String getTranslation(String locale) {
        return null;
    }

    public void showAndHandle() {
        int code = prepare().show();

        if (code == DialogWrapper.OK_EXIT_CODE) {
            saveTranslation();
        }
    }

    private void saveTranslation() {
        Map<String, String> messages = new HashMap<>();

        valueTextFields.forEach((k, v) -> {
            if (!v.getText().isEmpty()) {
                messages.put(k, v.getText());
            }
        });

        TranslationCreate creation = new TranslationCreate(new KeyedTranslation(keyTextField.getText(), messages));
        DataStore.getInstance(project).processUpdate(creation);
    }
}
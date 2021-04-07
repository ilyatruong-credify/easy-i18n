package com.yuukaze.i18next.ui.components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.yuukaze.i18next.model.KeyedTranslation;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.ui.dialog.LocaleRecord;
import com.yuukaze.i18next.ui.dialog.descriptor.DeleteActionDescriptor;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public abstract class LocaleDialogBase {
    protected final Project project;

    public Consumer<KeyedTranslation> getCallback() {
        return callback;
    }

    public void setCallback(Consumer<KeyedTranslation> callback) {
        this.callback = callback;
    }

    protected Consumer<KeyedTranslation> callback;

    @SuppressWarnings("FieldCanBeLocal")
    protected JBTextField keyTextField;
    @SuppressWarnings("FieldCanBeLocal")
    protected Map<String, JBTextField> valueTextFields;
    private final String title;

    protected LocaleDialogBase(Project project, String title) {
        this.project = project;
        this.title = title;
    }

    @Nullable
    protected abstract String getPreKey();

    @Nullable
    protected abstract String getTranslation(String locale);

    protected DialogBuilder prepare() {
        JPanel rootPanel = new VerticalPanel();

        JPanel keyPanel = new VerticalPanel();
        JBLabel keyLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("translation.key"));
        keyLabel.setHorizontalAlignment(JBLabel.LEFT);
        String preKey = this.getPreKey();
        keyTextField = new JBTextField(preKey);
        keyLabel.setLabelFor(keyTextField);
        keyPanel.add(keyLabel);
        keyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        keyPanel.add(keyTextField);
        rootPanel.add(keyPanel);

        JPanel valuePanel = new VerticalPanel();
        valueTextFields = new HashMap<>();
        for(String locale : DataStore.getInstance(project).getTranslations().getLocales()) {
            LocaleRecord rec = new LocaleRecord(locale,project,valueTextFields);
            rec.getLocaleText().setText(this.getTranslation(locale));
            rec.insertTo(valuePanel);
        }

        JBScrollPane valuePane = new JBScrollPane(valuePanel);
        valuePane.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),
                ResourceBundle.getBundle("messages").getString("translation.locales")));
        rootPanel.add(valuePane);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(this.title);
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addActionDescriptor(new DeleteActionDescriptor());
        builder.addOkAction();
        builder.setCenterPanel(rootPanel);

        return builder;
    }
}

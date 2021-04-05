package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yuukaze.i18next.service.SettingsService;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

public class LocaleRecord implements FocusListener {
    private final Project project;
    private JBLabel localeLabel;
    private JBTextField localeText;
    private JPanel localePanel;
    private final Map<String, JBTextField> valueTextFields;
    private final String locale;

    public LocaleRecord(String locale, Project project, Map<String, JBTextField> valueTextFields) {
        this.project = project;
        this.locale = locale;
        localeLabel.setText(locale);
        localeLabel.setLabelFor(localeText);
        this.valueTextFields = valueTextFields;
        localeText.addFocusListener(this);
        valueTextFields.put(locale,localeText);
    }

    public JBLabel getLocaleLabel() {
        return localeLabel;
    }

    public JBTextField getLocaleText() {
        return localeText;
    }

    public void insertTo(JPanel panel) {
        panel.add(localePanel);
    }

    @Override
    public void focusGained(FocusEvent e) {
        String defaultLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        if (!defaultLocale.equals(locale) && localeText.getText().length() == 0) {
            localeText.setText(valueTextFields.get(defaultLocale).getText());
            localeText.selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}

package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yuukaze.i18next.service.SpreadsheetSynchronizer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SettingsForm {
    public JPanel contentPane;
    public TextFieldWithBrowseButton pathText;
    public JBTextField previewText;
    private JCheckBox disableKeySeparator;
    public JBTextField keySeparator;
    private JBLabel keySeparatorLabel;
    public JBTextField spreadsheetIdText;
    private JButton testConnectionButton;
    private JBLabel testConnectionStatusLabel;

    private SpreadsheetSynchronizer spreadsheetSynchronizer;

    public SettingsForm() {
        testConnectionButton.addActionListener(e -> {
            try {
                testConnectionStatusLabel.setText("Waiting for authorization...");
                spreadsheetSynchronizer = new SpreadsheetSynchronizer();
                testConnectionStatusLabel.setForeground(JBColor.green);
                testConnectionStatusLabel.setText("Connection successful");
            } catch (IOException | GeneralSecurityException exception) {
                exception.printStackTrace();
                testConnectionStatusLabel.setForeground(JBColor.red);
                testConnectionStatusLabel.setText("Connection failed");
            }
        });
        spreadsheetIdText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (spreadsheetSynchronizer != null) {
                    spreadsheetSynchronizer.setSpreadSheetId(spreadsheetIdText.getText());
                }
            }
        });
    }

    public void setDisableKeySeparator(boolean value) {
        disableKeySeparator.setSelected(value);
        keySeparator.setEnabled(value);
        keySeparatorLabel.setEnabled(value);
    }

    public boolean getDisableKeySeparator() {
        return disableKeySeparator.isSelected();
    }
}

package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yuukaze.i18next.model.SettingsState;
import com.yuukaze.i18next.service.SpreadsheetSynchronizer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SettingsForm implements ActionListener {
    public JPanel contentPane;
    public TextFieldWithBrowseButton pathText;
    public JBTextField previewText;
    private JCheckBox disableKeySeparator;
    public JBTextField keySeparator;
    private JBLabel keySeparatorLabel;
    public JBTextField spreadsheetIdText;
    private JButton testConnectionButton;
    private JBLabel testConnectionStatusLabel;
    public JBTextField spreadsheetTabText;

    private SpreadsheetSynchronizer spreadsheetSynchronizer;

    public SettingsForm() {
        testConnectionButton.addActionListener(this);
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

    @Override
    public void actionPerformed(ActionEvent e) {
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
    }

    public void fetchDataFromState(SettingsState data) {
        pathText.setText(data.getLocalesPath());
        previewText.setText(data.getPreviewLocale());
        disableKeySeparator.setSelected(data.isHasSeparator());
        keySeparator.setText(data.getKeySeparator());
        spreadsheetIdText.setText(data.getSpreadSheetId());
        spreadsheetTabText.setText(data.getSpreadSheetTab());
    }

    public void pushDataIntoState(SettingsState data) {
        data.setLocalesPath(pathText.getText());
        data.setPreviewLocale(previewText.getText());
        data.setHasSeparator(disableKeySeparator.isSelected());
        data.setKeySeparator(keySeparator.getText());
        data.setSpreadSheetId(spreadsheetIdText.getText());
        data.setSpreadSheetTab(spreadsheetTabText.getText());
    }
//
//    public boolean isModified(SettingsState data) {
//        if (previewText.getText() != null ? !previewText.getText().equals(data.getPreviewLocale()) : data.getPreviewLocale() != null)
//            return true;
//        if (disableKeySeparator.isSelected() != data.isHasSeparator()) return true;
//        if (keySeparator.getText() != null ? !keySeparator.getText().equals(data.getKeySeparator()) : data.getKeySeparator() != null)
//            return true;
//        if (spreadsheetIdText.getText() != null ? !spreadsheetIdText.getText().equals(data.getSpreadSheetId()) : data.getSpreadSheetId() != null)
//            return true;
//        if (spreadsheetTabText.getText() != null ? !spreadsheetTabText.getText().equals(data.getSpreadSheetTab()) : data.getSpreadSheetTab() != null)
//            return true;
//        return false;
//    }
}

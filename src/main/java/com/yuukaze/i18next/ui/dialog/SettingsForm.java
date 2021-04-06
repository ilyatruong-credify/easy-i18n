package com.yuukaze.i18next.ui.dialog;

import com.intellij.openapi.ui.Divider;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;

public class SettingsForm {
    public JPanel contentPane;
    public TextFieldWithBrowseButton pathText;
    public JBTextField previewText;
    private JCheckBox disableKeySeparator;
    public JBTextField keySeparator;
    private JBLabel keySeparatorLabel;
    public JBTextField spreadsheetIdText;
    private JButton testConnectionButton;

    public void setDisableKeySeparator(boolean value){
        disableKeySeparator.setSelected(value);
        keySeparator.setEnabled(value);
        keySeparatorLabel.setEnabled(value);
    }

    public boolean getDisableKeySeparator(){
        return disableKeySeparator.isSelected();
    }
}

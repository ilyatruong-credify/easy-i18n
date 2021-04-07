package com.yuukaze.i18next.service;

import com.google.api.services.sheets.v4.Sheets;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.security.GeneralSecurityException;

public abstract class SpreadsheetExecutorBase {
    protected SpreadsheetSynchronizer synchronizer;
    protected String spreadsheetId;
    protected Project project;
    protected static final String SPREADSHEET_RANGE="Translations!A:D";

    public SpreadsheetExecutorBase(Project project){
        this.project = project;
        spreadsheetId = SettingsService.getInstance(project).getState().getSpreadSheetId();
        try {
            synchronizer = new SpreadsheetSynchronizer();
            synchronizer.setSpreadSheetId(spreadsheetId);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public Sheets getSheets(){
        return synchronizer.getSheetService();
    }

    public abstract void doAction();
}

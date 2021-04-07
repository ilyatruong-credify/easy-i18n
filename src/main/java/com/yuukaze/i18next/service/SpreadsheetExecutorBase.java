package com.yuukaze.i18next.service;

import com.google.api.services.sheets.v4.Sheets;
import com.intellij.openapi.project.Project;
import com.yuukaze.i18next.model.SettingsState;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;

public abstract class SpreadsheetExecutorBase {
    protected SpreadsheetSynchronizer synchronizer;
    protected String spreadsheetId;
    protected Project project;
    protected String SPREADSHEET_RANGE;

    public SpreadsheetExecutorBase(Project project) {
        this.project = project;
        SettingsState state = SettingsService.getInstance(project).getState();
        spreadsheetId = state.getSpreadSheetId();
        SPREADSHEET_RANGE = state.getSpreadSheetTab() + "!A:D";
        try {
            synchronizer = new SpreadsheetSynchronizer();
            synchronizer.setSpreadSheetId(spreadsheetId);
        } catch (IOException | GeneralSecurityException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();

            Notifier.notifyError(project, sStackTrace);
        }
    }

    public Sheets getSheets() {
        return synchronizer.getSheetService();
    }

    public abstract void doAction();
}

package com.yuukaze.i18next.ui.action;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.yuukaze.i18next.model.Translations;
import com.yuukaze.i18next.model.spreadsheet.SpreadsheetUploadModel;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.service.Notifier;
import com.yuukaze.i18next.service.SpreadsheetExecutorBase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SpreadsheetUploadAction extends AnAction {
    public SpreadsheetUploadAction() {
        super("Upload to Spreadsheet", null, AllIcons.Actions.Upload);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SpreadsheetExecutorBase executor = new Executor(e.getProject());
        executor.doAction();
    }

    private static final class Executor extends SpreadsheetExecutorBase {
        public Executor(Project project) {
            super(project);
        }

        @Override
        public void doAction() {
            Translations translations = DataStore.getInstance(project).getTranslations();
            ValueRange body = new ValueRange().setValues(new SpreadsheetUploadModel(translations));
            try {
                UpdateValuesResponse result = synchronizer.getSheetService().spreadsheets().values().update(spreadsheetId, SPREADSHEET_RANGE, body)
                        .setValueInputOption("RAW")
                        .execute();
                System.out.printf("%d cells updated.", result.getUpdatedCells());
                Notifier.notifySuccess(project, "Successfully upload translation to Spreadsheet");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

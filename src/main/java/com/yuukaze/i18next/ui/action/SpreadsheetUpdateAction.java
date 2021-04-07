package com.yuukaze.i18next.ui.action;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.yuukaze.i18next.model.LocalizedNode;
import com.yuukaze.i18next.model.Translations;
import com.yuukaze.i18next.service.DataStore;
import com.yuukaze.i18next.service.Notifier;
import com.yuukaze.i18next.service.SpreadsheetExecutorBase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpreadsheetUpdateAction extends AnAction {
    public SpreadsheetUpdateAction() {
        super("Update from Spreadsheet", null, AllIcons.Actions.Download);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SpreadsheetExecutorBase executor = new SpreadsheetUpdateAction.Executor(e.getProject());
        executor.doAction();
    }

    private static final class Executor extends SpreadsheetExecutorBase {
        public Executor(Project project) {
            super(project);
        }

        @Override
        public void doAction() {
            try {
                ValueRange result = synchronizer.getSheetService().spreadsheets().values().get(spreadsheetId, SPREADSHEET_RANGE).execute();
                List<List<Object>> values = result.getValues();

//                List<String> locales = values.get(0).stream().skip(1).map(o -> (String) o).collect(Collectors.toList());
                Translations translations = DataStore.getInstance(project).getTranslations();
                List<String> locales = translations.getLocales();
                List<List<Object>> rows = values.stream().skip(1).collect(Collectors.toList());
                for (List<Object> row : rows) {
                    LocalizedNode childrenNode = translations.getOrCreateNode(row.get(0).toString());
                    Map<String, String> messages = childrenNode.getValue();
                    for (int index = 0; index < locales.size(); index++) {
                        String locale = locales.get(index);
                        String translatedText = index >= row.size() - 1 ? "" : row.get(index + 1).toString();
                        messages.put(locale, translatedText);
                    }
                }

                DataStore.getInstance(project).processUpdate(null);
                Notifier.notifySuccess(project, "Successfully update translation from Spreadsheet");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

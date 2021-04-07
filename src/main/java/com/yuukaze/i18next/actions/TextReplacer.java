package com.yuukaze.i18next.actions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 1. Search text in project translation (by preview lang - en)
 * 2. If found - collect match keys, show key replacement
 * 3. If not found - show add new translation entry dialog
 */

public class TextReplacer extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "I18n-ize...";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(() -> {
            doInvoke(editor, project, element);
        });
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return true;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "EasyI18n";
    }

    private void doInvoke(Editor editor, Project project, PsiElement element) {
        KeyRequest.INSTANCE.key(project, Objects.requireNonNull(editor.getSelectionModel().getSelectedText()));
    }
}

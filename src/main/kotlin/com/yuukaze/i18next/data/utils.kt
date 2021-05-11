package com.yuukaze.i18next.data

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement

internal fun commitPsiElementChanges(project: Project, elements: List<PsiElement>) {
    val mapped: Map<Document, List<PsiElement>> =
        elements.groupBy { FileDocumentManager.getInstance().getDocument(it.containingFile.virtualFile)!! }
    mapped.keys.forEach {
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(it)
    }
}
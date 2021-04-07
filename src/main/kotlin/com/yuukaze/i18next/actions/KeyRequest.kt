package com.yuukaze.i18next.actions

import com.intellij.openapi.project.Project
import com.yuukaze.i18next.service.DataStore
import com.yuukaze.i18next.ui.dialog.AddDialog
import com.yuukaze.i18next.util.TranslationKeyTest
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object KeyRequest {
    fun key(project: Project, text: String) {
        val translations = DataStore.getInstance(project).translations;
        val fullKeys = translations.fullKeys.filter(TranslationKeyTest(text, true)::test)

//        println(fullKeys.stream().map { i -> i.first }.collect(Collectors.toList()))
        if (fullKeys.isEmpty()) {
            val add = AddDialog(project, null)
            add.extractedText = text;
            add.setCallback { keyed ->
                run {
                    val clipboard = Toolkit.getDefaultToolkit().systemClipboard;
                    val selection = StringSelection("t(\"" + keyed.key + "\")");
                    clipboard.setContents(selection, selection)
                }
            }
            add.showAndHandle();
        }
    }
}
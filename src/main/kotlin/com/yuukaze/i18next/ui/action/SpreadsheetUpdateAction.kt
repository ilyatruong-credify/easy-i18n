package com.yuukaze.i18next.ui.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yuukaze.i18next.data.ReloadTranslations
import com.yuukaze.i18next.data.i18nStore
import com.yuukaze.i18next.service.Notifier
import com.yuukaze.i18next.service.SpreadsheetExecutorBase
import com.yuukaze.i18next.service.getEasyI18nDataStore
import java.io.IOException
import java.util.stream.Collectors

class SpreadsheetUpdateAction :
    AnAction("Update from Spreadsheet", null, AllIcons.Actions.Download) {
    override fun actionPerformed(e: AnActionEvent) {
        val executor: SpreadsheetExecutorBase = Executor(e.project)
        executor.doAction()
    }

    class Executor(project: Project?) : SpreadsheetExecutorBase(project) {
        override fun run() {
            try {
                val result =
                    synchronizer.sheetService!!.spreadsheets()
                        .values()[spreadsheetId, SPREADSHEET_RANGE].execute()
                val values = result.getValues()

                val translations = project.getEasyI18nDataStore().translations
                val locales = translations.locales
                val rows = values.stream().skip(1).collect(Collectors.toList())
                for (row in rows) {
                    val childrenNode = translations.getOrCreateNode(row[0].toString())
                    val messages = childrenNode.value
                    for (index in locales.indices) {
                        val locale = locales[index]
                        val translatedText =
                            if (index >= row.size - 1) "" else row[index + 1].toString()
                        messages[locale] = translatedText
                    }
                }
                println(translations.nodes.children.size)
                i18nStore.dispatch(ReloadTranslations(translations = translations))
                Notifier.notifySuccess(
                    project,
                    "Successfully update translation from Spreadsheet"
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
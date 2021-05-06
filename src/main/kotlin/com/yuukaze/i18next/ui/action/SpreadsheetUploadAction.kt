package com.yuukaze.i18next.ui.action

import com.google.api.services.sheets.v4.model.ValueRange
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yuukaze.i18next.model.spreadsheet.SpreadsheetUploadModel
import com.yuukaze.i18next.service.Notifier
import com.yuukaze.i18next.service.SpreadsheetExecutorBase
import com.yuukaze.i18next.service.getEasyI18nDataStore
import java.io.IOException

class SpreadsheetUploadAction :
  AnAction("Upload to Spreadsheet", null, AllIcons.Actions.Upload) {
  override fun actionPerformed(e: AnActionEvent) {
    val executor: SpreadsheetExecutorBase = Executor(e.project)
    executor.doAction()
  }

  class Executor(project: Project?) : SpreadsheetExecutorBase(project) {
    override fun run() {
      val translations = project.getEasyI18nDataStore().translations
      val body = ValueRange().setValues(SpreadsheetUploadModel(translations))
      try {
        val result =
          synchronizer.sheetService!!.spreadsheets().values()
            .update(spreadsheetId, SPREADSHEET_RANGE, body)
            .setValueInputOption("RAW")
            .execute()
        System.out.printf("%d cells updated.", result.updatedCells)
        Notifier.notifySuccess(
          project,
          "Successfully upload translation to Spreadsheet"
        )
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }
}
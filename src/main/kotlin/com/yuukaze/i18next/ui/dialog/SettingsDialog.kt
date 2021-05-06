package com.yuukaze.i18next.ui.dialog

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.yuukaze.i18next.model.SettingsState
import com.yuukaze.i18next.service.EasyI18nSettingsService
import com.yuukaze.i18next.service.getEasyI18nDataStore
import java.util.*

/**
 * Plugin configuration dialog.
 */
class SettingsDialog(private val project: Project) {
  private var settingsForm: SettingsForm? = null
  fun showAndHandle() {
    val state = project.getService(
      EasyI18nSettingsService::class.java
    ).state
    if (prepare(state).show() == DialogWrapper.OK_EXIT_CODE) {
      settingsForm!!.pushDataIntoState(state)

      // Reload instance
      project.getEasyI18nDataStore().reloadFromDisk()
    }
  }

  private fun prepare(state: SettingsState): DialogBuilder {
    settingsForm = SettingsForm()
    settingsForm!!.pathText.addBrowseFolderListener(
      ResourceBundle.getBundle("messages").getString("settings.path.title"),
      null,
      project,
      FileChooserDescriptor(
        false, true, false, false, false, false
      )
    )
    settingsForm!!.fetchDataFromState(state)
    val builder = DialogBuilder()
    builder.setTitle(
      ResourceBundle.getBundle("messages").getString("action.settings")
    )
    builder.removeAllActions()
    builder.addCancelAction()
    builder.addOkAction()
    builder.setCenterPanel(settingsForm!!.contentPane)
    return builder
  }
}
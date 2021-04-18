package com.yuukaze.i18next.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import com.yuukaze.i18next.service.getEasyI18nDataStore
import java.awt.Dimension
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

class MigrateDialog(private val project: Project) {

  private var replacePattern: String = ""
  private val fullKeys
    get() = project.getEasyI18nDataStore().translations.fullKeysWithoutPreview.filter {
      if (findPattern.isEmpty()) true else findPattern.toRegex()
        .matches(it)
    }
  private val listComponent =
    JBList<String>().apply { setListData(fullKeys.toTypedArray()) }

  @Suppress("RedundantNullableReturnType")
  private val findPatternComponent: JBTextField? = JBTextField().apply {
    addFocusListener(object : FocusListener {
      override fun focusGained(e: FocusEvent?) {}

      override fun focusLost(e: FocusEvent?) {
        listComponent.setListData(fullKeys.toTypedArray())
      }

    })
  }
  private val findPattern: String
    get() = findPatternComponent?.text ?: ""

  private val centerPanel = panel {
    row {
      row("Find pattern") { findPatternComponent!!() }
      row("Replace pattern") { textField(::replacePattern) }
    }
    row {
      scrollPane(listComponent).constraints(CCFlags.pushY)
    }
  }.apply {
    preferredSize = Dimension(0, 300)
  }

  val builder = DialogBuilder(project).let {
    it.setTitle("Migrate")
    it.setCenterPanel(centerPanel)
    it
  }

  fun showAndHandle() {
    if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
      fullKeys.forEach { key ->
        var newKey = replacePattern
        findPattern.toRegex()
          .find(key)!!.groups.forEachIndexed { index, group ->
            if (index > 0)
              newKey = newKey.replace("\$$index", group?.value ?: "")
          }
        if (newKey != key)
          project.getEasyI18nDataStore().translations.changeKey(key, newKey)
      }
      run {
        project.getEasyI18nDataStore().doSync()
      }
    }
  }
}

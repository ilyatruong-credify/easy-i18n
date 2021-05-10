package com.yuukaze.i18next.ui.action

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import com.yuukaze.i18next.data.duplicateI18nKey
import java.awt.Point
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JTable

enum class DuplicateKeyType {
  DEFAULT,
  PLURAL
}

class DuplicateAction(val table: JTable) : AbstractAction("Duplicate key") {
  var key: String = ""
    set(value) {
      field = value
      putValue(
        Action.NAME,
        "Duplicate key '${value.ifEmpty { "unknown" }}'"
      )
    }
  var location: Point? = null

  override fun actionPerformed(e: ActionEvent?) {
    val dialog = DuplicateActionDialog()
    if (dialog.showAndGet()) {
      duplicateI18nKey(key, dialog.newKey)
    }
  }

  inner class DuplicateActionDialog : DialogWrapper(true) {
    var newKey = "$key."
    private var duplicateType = DuplicateKeyType.DEFAULT
      set(value) {
        field = value
        newKey = when (field) {
          DuplicateKeyType.DEFAULT -> "$key."
          DuplicateKeyType.PLURAL -> "${key}_plural"
        }
      }

    init {
      init()
      title = getValue(Action.NAME) as String
    }

    override fun createCenterPanel() = panel {
      row("I18n Key") {
        textField(::newKey).focused()
      }
      row("") {
        cell {
          buttonGroup {
            radioButton("Default",
              { duplicateType == DuplicateKeyType.DEFAULT },
              { duplicateType = DuplicateKeyType.DEFAULT })
            radioButton("Plural",
              { duplicateType == DuplicateKeyType.PLURAL },
              { duplicateType = DuplicateKeyType.PLURAL })
          }
        }
      }
    }

  }
}
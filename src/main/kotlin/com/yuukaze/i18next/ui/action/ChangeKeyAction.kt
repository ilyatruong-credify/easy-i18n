package com.yuukaze.i18next.ui.action

import com.yuukaze.i18next.data.changeI18nKey
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JTable

class ChangeKeyAction(val table: JTable) : AbstractAction("Change key") {
     override fun actionPerformed(e: ActionEvent?) {
        changeI18nKey("login.button.login-as-organization", "login.button.login-as-org")
    }
}
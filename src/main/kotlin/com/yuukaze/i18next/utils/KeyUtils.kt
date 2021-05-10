package com.yuukaze.i18next.utils

import javax.swing.text.JTextComponent

fun <T : JTextComponent> getPreviousKeyPart(component: T, caret: Int): String =
    component.text.indexOfLast { it == '.' }
        .let {
            if (it < 1) component.text else component.getText(
                it + 1,
                caret - it - 1
            )
        }
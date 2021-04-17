package com.yuukaze.i18next.ui.tabs
import com.intellij.ide.ui.laf.IntelliJLaf
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.IconManager
import javax.swing.JFrame
import javax.swing.UIManager

internal class TableViewTest{
  fun main(){
    IconLoader.activate()
    IconManager.activate()
    val laf = IntelliJLaf()
    UIManager.setLookAndFeel(laf)

    val frame = JFrame("Settings")
  }
}
package com.yuukaze.i18next.ui

import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI
import com.intellij.ui.SearchTextField
import com.intellij.util.Consumer
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.utils.JComponentWrapper
import org.jdesktop.swingx.prompt.PromptSupport
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.JComponent

class SearchKeyView(private val callback: Consumer<String>) :
  JComponentWrapper<JComponent> {

  val textField = SearchTextField(false)

  init {
    textField.addKeyListener(handleKeyListener())
    textField.isOpaque = false
    textField.textEditor!!.apply {
      border = JBUI.Borders.empty(3,5)
      setUI(DarculaTextFieldUI())
    }
    PromptSupport.setPrompt(
      ResourceBundle.getBundle("messages").getString("action.search"),
      textField.textEditor
    )
  }

  override val component: JComponent
    get() = textField

  private fun handleKeyListener(): KeyAdapter = object : KeyAdapter() {
    override fun keyPressed(e: KeyEvent) {
      if (e.keyCode == KeyEvent.VK_ENTER) {
        e.consume()
        callback.consume(textField.text)
      }
    }
  }
}
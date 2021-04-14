package com.yuukaze.i18next.ui.renderer

import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.components.JBList
import com.yuukaze.i18next.utils.getPreviousKeyPart
import java.awt.Point
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.geom.Rectangle2D
import java.util.function.Consumer
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException
import javax.swing.text.JTextComponent

/**
 * @see <i>https://www.logicbig.com/tutorials/java-swing/text-suggestion-component.html</i>
 */

interface SuggestionClient<C : JComponent> {
  fun getPopupLocation(invoker: C): Point?
  fun setSelectedText(invoker: C, selectedValue: String)
  fun getSuggestions(invoker: C): List<String>?
}

class SuggestionDropDownDecorator<C : JComponent> private constructor(
  private val invoker: C,
  private val suggestionClient: SuggestionClient<C>
) {
  private var popupMenu: JPopupMenu? = null
  private var listComp: JList<String>? = null
  private lateinit var listModel: DefaultListModel<String>
  private var disableTextEvent = false

  companion object {
    fun <C : JComponent> decorate(
      component: C,
      suggestionClient: SuggestionClient<C>
    ) {
      val d = SuggestionDropDownDecorator(component, suggestionClient)
      d.init()
    }
  }

  fun init() {
    initPopup()
    initSuggestionCompListener()
    initInvokerKeyListeners()
  }

  private fun initPopup() {
    popupMenu = JBPopupMenu()
    listModel = JBList.createDefaultListModel(mutableListOf<String>())
    listComp = JBList(listModel)
    listComp!!.border = BorderFactory.createEmptyBorder(0, 2, 5, 2)
    listComp!!.isFocusable = false
    popupMenu!!.isFocusable = false
    popupMenu!!.add(listComp)
  }

  private fun initSuggestionCompListener() {
    if (invoker is JTextComponent) {
      val tc = invoker as JTextComponent
      tc.document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent) {
          update()
        }

        override fun removeUpdate(e: DocumentEvent) {
          update()
        }

        override fun changedUpdate(e: DocumentEvent) {
          update()
        }

        private fun update() {
          if (disableTextEvent) {
            return
          }
          SwingUtilities.invokeLater {
            val suggestions =
              suggestionClient.getSuggestions(
                invoker
              )
            if (suggestions != null && suggestions.isNotEmpty()) {
              showPopup(suggestions)
            } else {
              popupMenu!!.isVisible = false
            }
          }
        }
      })
    } //todo init invoker components other than text components
  }

  private fun showPopup(suggestions: List<String>) {
    listModel.clear()
    suggestions.forEach(Consumer { element: String ->
      listModel.addElement(
        element
      )
    })
    val p: Point = suggestionClient.getPopupLocation(invoker) ?: return
    popupMenu!!.pack()
    listComp!!.selectedIndex = 0
    popupMenu!!.show(invoker, p.getX().toInt(), p.getY().toInt())
  }

  private fun initInvokerKeyListeners() {
    //not using key inputMap cause that would override the original handling
    invoker.addKeyListener(object : KeyAdapter() {
      override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
          VK_ENTER -> selectFromList(e)
          VK_UP -> moveUp(e)
          VK_DOWN -> moveDown(e)
          VK_ESCAPE -> popupMenu!!.isVisible = false
        }
      }
    })
  }

  private fun selectFromList(e: KeyEvent) {
    if (popupMenu!!.isVisible) {
      val selectedIndex = listComp!!.selectedIndex
      if (selectedIndex != -1) {
        popupMenu!!.isVisible = false
        val selectedValue = listComp!!.selectedValue
        disableTextEvent = true
        suggestionClient.setSelectedText(invoker, selectedValue)
        disableTextEvent = false
        e.consume()
      }
    }
  }

  private fun moveDown(keyEvent: KeyEvent) {
    if (popupMenu!!.isVisible && listModel.size > 0) {
      val selectedIndex = listComp!!.selectedIndex
      if (selectedIndex < listModel.size) {
        listComp!!.selectedIndex = selectedIndex + 1
        keyEvent.consume()
      }
    }
  }

  private fun moveUp(keyEvent: KeyEvent) {
    if (popupMenu!!.isVisible && listModel.size > 0) {
      val selectedIndex = listComp!!.selectedIndex
      if (selectedIndex > 0) {
        listComp!!.selectedIndex = selectedIndex - 1
        keyEvent.consume()
      }
    }
  }

}

class I18nKeyComponentSuggestionClient(private val suggestionProvider: (String) -> List<String>?) :
  SuggestionClient<JTextComponent> {
  override fun getPopupLocation(invoker: JTextComponent): Point? {
    val caretPosition = invoker.caretPosition
    try {
      val rectangle2D: Rectangle2D = invoker.modelToView(caretPosition)
      return Point(
        rectangle2D.x.toInt(),
        (rectangle2D.y + rectangle2D.height).toInt()
      )
    } catch (e: BadLocationException) {
      System.err.println(e)
    }
    return null
  }

  override fun setSelectedText(invoker: JTextComponent, selectedValue: String) {
    val cp = invoker.caretPosition
    try {
      if (cp == 0 || invoker.getText(cp - 1, 1).trim() == ".") {
        invoker.document.insertString(cp, selectedValue, null)
      } else {
        val text = getPreviousKeyPart(invoker, cp)
        if (selectedValue.startsWith(text)) {
          invoker.document.insertString(
            cp,
            selectedValue.substring(text.length),
            null
          )
        } else {
          invoker.document.insertString(cp, selectedValue, null)
        }
      }
    } catch (e: BadLocationException) {
      System.err.println(e)
    }
  }

  override fun getSuggestions(invoker: JTextComponent): List<String>? {
    try {
      val cp = invoker.caretPosition
      if (cp != 0) {
        val text = invoker.getText(cp - 1, 1)
        if (text.trim() == ".") {
          return null
        }
      }
      val text = getPreviousKeyPart(invoker, cp)
      println(text)
      return suggestionProvider(text.trim())
    } catch (e: BadLocationException) {
      System.err.println(e)
    }
    return null
  }
}
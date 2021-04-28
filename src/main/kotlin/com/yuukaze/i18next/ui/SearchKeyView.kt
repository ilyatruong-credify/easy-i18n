package com.yuukaze.i18next.ui

import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.SearchTextField
import com.intellij.util.ui.JBUI
import com.yuukaze.i18next.utils.JComponentWrapper
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import org.jdesktop.swingx.prompt.PromptSupport
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.JComponent
import javax.swing.event.DocumentEvent


class SearchKeyView(private val callback: Consumer<String>) :
  JComponentWrapper<JComponent> {

  val textField = SearchTextField(false)

  init {
    textField.isOpaque = false
    textField.textEditor!!.apply {
      border = JBUI.Borders.empty(3, 5)
      setUI(DarculaTextFieldUI())
    }
    PromptSupport.setPrompt(
      ResourceBundle.getBundle("messages").getString("action.search"),
      textField.textEditor
    )
    textField.asyncSearchTextChangedHandler(callback)
  }

  override val component: JComponent
    get() = textField
}

fun SearchTextField.asyncSearchTextChangedHandler(callback: Consumer<String>) {
  RxSearchObservable.fromView(this)
    .debounce(300, TimeUnit.MILLISECONDS)
    .distinctUntilChanged()
    .subscribe(callback)
}

object RxSearchObservable {
  fun fromView(searchView: SearchTextField): PublishSubject<String> {
    val subject: PublishSubject<String> = PublishSubject.create()
    searchView.addDocumentListener(object : DocumentAdapter() {
      override fun textChanged(e: DocumentEvent) {
        subject.onNext(e.document.getText(0, e.document.length))
      }

    })
    return subject
  }
}
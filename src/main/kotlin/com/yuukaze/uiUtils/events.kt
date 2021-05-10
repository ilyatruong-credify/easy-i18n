package com.yuukaze.uiUtils

import com.intellij.ui.DocumentAdapter
import com.intellij.ui.SearchTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.swing.event.DocumentEvent

private fun SearchTextField.addDebouncedDocumentListenerStateFlow(): StateFlow<DocumentEvent?> {
    val query = MutableStateFlow<DocumentEvent?>(null)
    addDocumentListener(object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) {
            query.value = e
        }
    })
    return query
}

fun SearchTextField.addDebouncedDocumentListener(listener: DocumentEvent.() -> Unit) {
    val that = this
    GlobalScope.launch(Dispatchers.Main) {
        that.addDebouncedDocumentListenerStateFlow()
            .debounce(300)
            .filter { it != null }
            .distinctUntilChanged()
            .collect { it!!.listener() }
    }
}
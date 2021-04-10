package com.yuukaze.i18next.ui.model

import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class FilterUntranslatedModel {
  private val changeListener = ArrayList<ChangeListener>()
  var toggle: String? = null
    set(value) {
      val changed = field != value
      field = value
      if (changed)
        changeListener.forEach { l -> l.stateChanged(ChangeEvent(this)) }
    }
  val available: MutableSet<String> = mutableSetOf()

  fun addChangeListener(l: ChangeListener) {
    changeListener.add(l)
  }
}

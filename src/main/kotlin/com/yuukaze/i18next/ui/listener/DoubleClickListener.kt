package com.yuukaze.i18next.ui.listener

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.function.Consumer

class DoubleClickListener(val callback: Consumer<MouseEvent>) : MouseListener {
  override fun mouseClicked(e: MouseEvent?) {
    if (e?.clickCount == 2 && e.button == MouseEvent.BUTTON1)
      callback.accept(e)
  }

  override fun mousePressed(e: MouseEvent?) {
  }

  override fun mouseReleased(e: MouseEvent?) {
  }

  override fun mouseEntered(e: MouseEvent?) {
  }

  override fun mouseExited(e: MouseEvent?) {
  }
}
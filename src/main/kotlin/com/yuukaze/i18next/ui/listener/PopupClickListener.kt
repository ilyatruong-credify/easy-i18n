package com.yuukaze.i18next.ui.listener

import com.intellij.util.Consumer
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * Popup click listener for awt [MouseListener].
 * Emits consumer defined in constructor on popup open action.
 * @author marhali
 */
class PopupClickListener(private val callback: Consumer<MouseEvent>) :
  MouseListener {
  override fun mouseClicked(e: MouseEvent) {}
  override fun mousePressed(e: MouseEvent) {
    if (e.isPopupTrigger) {
      callback.consume(e)
    }
  }

  override fun mouseReleased(e: MouseEvent) {
    if (e.isPopupTrigger) {
      callback.consume(e)
    }
  }

  override fun mouseEntered(e: MouseEvent) {}
  override fun mouseExited(e: MouseEvent) {}
}
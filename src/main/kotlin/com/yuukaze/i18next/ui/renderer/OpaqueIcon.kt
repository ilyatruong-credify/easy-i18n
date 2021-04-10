package com.yuukaze.i18next.ui.renderer

import java.awt.AlphaComposite
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.Icon

class OpaqueIcon(private val icon: Icon) : Icon {
  override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
    val composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F)
      val g2d = g as Graphics2D
    val oldComposite = g2d.composite
    g2d.composite = composite
    icon.paintIcon(c, g2d, x, y)
    g2d.composite = oldComposite
  }

  override fun getIconWidth(): Int = icon.iconWidth

  override fun getIconHeight(): Int = icon.iconHeight
}
package com.yuukaze.i18next.ui.renderer

import java.awt.Component
import java.awt.Graphics
import java.lang.Integer.max
import javax.swing.Icon

class ListIcon(private val icons: List<Icon>, private val hgap: Int) : Icon {
    constructor(icons: List<Icon>) : this(icons, 2)

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        var cx = x
        icons.forEach { icon ->
            icon.paintIcon(c, g, cx, y)
            cx += (icon.iconWidth + hgap)
        }
    }

    override fun getIconWidth(): Int =
        icons.map { icon -> icon.iconWidth }
            .reduce { acc, iconWidth -> if (acc == 0) iconWidth else acc + hgap + iconWidth }

    override fun getIconHeight(): Int =
        icons.map { icon -> icon.iconHeight }
            .reduce { acc, iconHeight -> max(acc, iconHeight) }
}